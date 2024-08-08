package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.*;
import com.bb.bikebliss.repository.*;
import com.bb.bikebliss.service.dto.EquipmentRentalDTO;
import com.bb.bikebliss.service.dto.UnavailableDateDTO;
import com.bb.bikebliss.service.mapper.EquipmentRentalMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentRentalService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentRentalRepository equipmentRentalRepository;
    private final EquipmentRentalMapper equipmentRentalMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public EquipmentRentalService(EquipmentRepository equipmentRepository, EquipmentRentalRepository equipmentRentalRepository,
                         UserRepository userRepository, EmailService emailService) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentRentalMapper = EquipmentRentalMapper.INSTANCE;
        this.equipmentRentalRepository = equipmentRentalRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    @Transactional
    public EquipmentRentalDTO createEquipmentRental(EquipmentRentalDTO equipmentRentalDTO, Integer equipmentModelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated to rent a bike");
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LocalDate startDateOnly = equipmentRentalDTO.startDate();
        LocalDate endDateOnly = equipmentRentalDTO.endDate();

        LocalDateTime startDate = startDateOnly.atTime(6, 0);
        LocalDateTime endDate = endDateOnly.atTime(23, 59, 59, 999999999);

        if (startDateOnly.equals(endDateOnly)) {
            startDate = startDateOnly.atTime(6, 0);
            endDate = startDateOnly.atTime(23, 59, 59, 999999999);
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        long hoursUntilStart = ChronoUnit.HOURS.between(currentDateTime, startDate);
        if (hoursUntilStart < 6) {
            throw new IllegalStateException("The equipment rental must be made at least 6 hours before the start time.");
        }

        long daysUntilStart = ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), startDate.toLocalDate());
        if (daysUntilStart > 60) {
            throw new IllegalStateException("The equipment rental cannot be made more than 60 days in advance.");
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        if (daysBetween < 1) {
            throw new IllegalStateException("The equipment rental period must be at least 6 hours.");
        } else if (daysBetween > 14) {
            throw new IllegalStateException("The equipment rental period cannot exceed 14 days.");
        }

        Equipment availableEquipment = equipmentRepository.findAvailableEquipments(equipmentModelId, startDate, endDate)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available equipments for the selected model."));

        EquipmentRental equipmentRental = new EquipmentRental();
        equipmentRental.setUser(user);
        equipmentRental.setEquipment(availableEquipment);
        equipmentRental.setStartDate(startDate);
        equipmentRental.setEndDate(endDate);
        equipmentRental.setRentalStatus(RentalStatus.PENDING);

        BigDecimal pricePerDay = availableEquipment.getEquipmentModel().getPricePerDay();
        BigDecimal totalPrice = pricePerDay.multiply(BigDecimal.valueOf(daysBetween));

        // Verificați numărul de închirieri anterioare ale utilizatorului
        long equipmentRentalCount = equipmentRentalRepository.countEquipmentRentalsByUser(user);
        if (equipmentRentalCount > 3) {
            // Aplicați o reducere de 15% dacă utilizatorul a închiriat mai mult de 3 ori
            totalPrice = totalPrice.multiply(BigDecimal.valueOf(0.85));
        }

        equipmentRental.setTotalPrice(totalPrice);

        equipmentRental = equipmentRentalRepository.save(equipmentRental);

        notifyAdminOfPendingRental(equipmentRental);
        return equipmentRentalMapper.equipmentRentalToEquipmentRentalDTO(equipmentRental);
    }

    private void notifyAdminOfPendingRental(EquipmentRental equipmentRental) {
        String adminEmail = "adriana.stecalovici200@gmail.com";
        String subject = "New Rental Pending Approval";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedStartDate = equipmentRental.getStartDate().format(dateFormatter) + " from " + equipmentRental.getStartDate().format(timeFormatter);
        String formattedEndDate = equipmentRental.getEndDate().format(dateFormatter) + " at " + equipmentRental.getEndDate().format(timeFormatter);

        String body = "<p>Dear Owner,</p>" +
                "<p>A new rental is pending approval. Please view the details below:</p>" +
                "<ul>" +
                "<li><strong>Start Date:</strong> " + formattedStartDate + "</li>" +
                "<li><strong>End Date:</strong> " + formattedEndDate + "</li>" +
                "<li><strong>Total Price:</strong> " + equipmentRental.getTotalPrice() + " RON</li>" +
                "</ul>" +
                "<p>Please review and approve or reject the rental request at your earliest convenience.</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(adminEmail, subject, body);
    }
    public void approveEquipmentRental(Integer equipmentRentalId) {
        EquipmentRental equipmentRental= equipmentRentalRepository.findById(equipmentRentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        equipmentRental.setRentalStatus(RentalStatus.APPROVED);

        equipmentRentalRepository.save(equipmentRental);
        String locationAddress = equipmentRental.getEquipment().getEquipmentModel().getLocation().getAddress();
        String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(locationAddress, StandardCharsets.UTF_8);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedStartDate = equipmentRental.getStartDate().format(dateFormatter) + " from " + equipmentRental.getStartDate().format(timeFormatter);
        String formattedEndDate = equipmentRental.getEndDate().format(dateFormatter) + " at " + equipmentRental.getEndDate().format(timeFormatter);
        BigDecimal totalPrice = equipmentRental.getTotalPrice();

// Verificați numărul de închirieri anterioare ale utilizatorului
        long equipmentRentalCount = equipmentRentalRepository.countEquipmentRentalsByUser(equipmentRental.getUser());
        boolean discountApplied = equipmentRentalCount > 3;

        String emailBody = "<p>Dear " + equipmentRental.getUser().getFirstName() + ",</p>" +
                "<p>Thank you for choosing BikeBliss. Your rental has been approved. Here are your rental details:</p>" +
                "<ul>" +
                "<li><strong>Equipment Model:</strong> " + equipmentRental.getEquipment().getEquipmentModel().getEquipmentModel() + "</li>" +
                "<li><strong>Rental Period:</strong> " + formattedStartDate + " to " + formattedEndDate + "</li>" +
                "<li><strong>Total Price:</strong> " + totalPrice + " RON" + (discountApplied ? " (including 15% discount)" : "") + "</li>" +
                "<li><strong>Pickup Location:</strong> <a href='" + mapsLink + "' target='_blank'>" + locationAddress + "</a></li>" +
                "</ul>" +
                "<p>You can pick up the equipment starting from 6:00 AM at the location mentioned above.</p>" +
                "<p>We hope you enjoy your ride!</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(equipmentRental.getUser().getEmail(), "BikeBliss Rental Approval", emailBody);
    }
    public void rejectEquipmentRental(Integer equipmentRentalId) {
        EquipmentRental equipmentRental = equipmentRentalRepository.findById(equipmentRentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        equipmentRental.setRentalStatus(RentalStatus.REJECTED);
        equipmentRentalRepository.save(equipmentRental);

        String locationAddress = equipmentRental.getEquipment().getEquipmentModel().getLocation().getAddress();
        String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(locationAddress, StandardCharsets.UTF_8);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedStartDate = equipmentRental.getStartDate().format(dateFormatter) + " from " + equipmentRental.getStartDate().format(timeFormatter);
        String formattedEndDate = equipmentRental.getEndDate().format(dateFormatter) + " at " + equipmentRental.getEndDate().format(timeFormatter);
        BigDecimal totalPrice = equipmentRental.getTotalPrice();

// Verificați numărul de închirieri anterioare ale utilizatorului
        long equipmentRentalCount = equipmentRentalRepository.countEquipmentRentalsByUser(equipmentRental.getUser());
        boolean discountApplied = equipmentRentalCount > 3;

        String emailBody = "<p>Dear " + equipmentRental.getUser().getFirstName() + ",</p>" +
                "<p>We regret to inform you that your rental request for the following bike has been rejected:</p>" +
                "<ul>" +
                "<li><strong>Equipment Model:</strong> " + equipmentRental.getEquipment().getEquipmentModel().getEquipmentModel() + "</li>" +
                "<li><strong>Rental Period:</strong> " + formattedStartDate + " to " + formattedEndDate + "</li>" +
                "<li><strong>Total Price:</strong> " + totalPrice + " RON" + (discountApplied ? " (including 15% discount)" : "") + "</li>" +
                "<li><strong>Intended Pickup Location:</strong> <a href='" + mapsLink + "' target='_blank'>" + locationAddress + "</a></li>" +
                "</ul>" +
                "<p>Please contact us if you have any questions or need further assistance.</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(equipmentRental.getUser().getEmail(), "BikeBliss Rental Rejection", emailBody);
    }
    public List<UnavailableDateDTO> getUnavailableDatesForModel(Integer equipmentModelId) {
        List<EquipmentRental> equipmentRentals = equipmentRentalRepository.findEquipmentRentalsByEquipmentModelId(equipmentModelId);

        int totalEquipments = equipmentRepository.countByModelId(equipmentModelId);

        Map<LocalDate, Integer> equipmentCountPerDay = new HashMap<>();
        for (EquipmentRental equipmentRental: equipmentRentals) {
            equipmentRental.getStartDate().toLocalDate().datesUntil(equipmentRental.getEndDate().toLocalDate().plusDays(1))
                    .forEach(date -> equipmentCountPerDay.put(date, equipmentCountPerDay.getOrDefault(date, 0) + 1));
        }
        List<LocalDate> unavailableDates = equipmentCountPerDay.entrySet().stream()
                .filter(entry -> entry.getValue() >= totalEquipments)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return convertToUnavailableDateDTOs(unavailableDates);
    }
    private List<UnavailableDateDTO> convertToUnavailableDateDTOs(List<LocalDate> unavailableDates) {
        return unavailableDates.stream()
                .map(date -> new UnavailableDateDTO(date, date))
                .collect(Collectors.toList());
    }
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateRentalStatuses() {
        List<EquipmentRental> equipmentRentals = equipmentRentalRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (EquipmentRental equipmentRental : equipmentRentals) {
            if (equipmentRental.getRentalStatus() == RentalStatus.PENDING && now.isAfter(equipmentRental.getStartDate()) && now.isBefore(equipmentRental.getEndDate())) {
                equipmentRental.setRentalStatus(RentalStatus.ACTIVE);
            } else if (equipmentRental.getRentalStatus() == RentalStatus.ACTIVE && now.isAfter(equipmentRental.getEndDate())) {
                equipmentRental.setRentalStatus(RentalStatus.COMPLETED);
            }
            equipmentRentalRepository.save(equipmentRental);
        }
    }
    public List<EquipmentRentalDTO> getUserRentals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<EquipmentRental> equipmentRentals = equipmentRentalRepository.findByUser(user);
        return equipmentRentals.stream()
                .map(equipmentRentalMapper::equipmentRentalToEquipmentRentalDTO)
                .collect(Collectors.toList());
    }
    public List<EquipmentRentalDTO> getActiveEquipmentRentals() {
        List<EquipmentRental> equipmentRentals = equipmentRentalRepository.findByRentalStatus(RentalStatus.ACTIVE);
        return equipmentRentals.stream()
                .map(equipmentRentalMapper::equipmentRentalToEquipmentRentalDTO)
                .collect(Collectors.toList());
    }
    public List<EquipmentRentalDTO> getPendingEquipmentRentals() {
        List<EquipmentRental> equipmentRentals = equipmentRentalRepository.findByRentalStatus(RentalStatus.PENDING);
        return equipmentRentals.stream()
                .map(equipmentRentalMapper::equipmentRentalToEquipmentRentalDTO)
                .collect(Collectors.toList());
    }
    public List<EquipmentRentalDTO> getCompletedEquipmentRentals() {
        List<EquipmentRental> equipmentRentals = equipmentRentalRepository.findByRentalStatus(RentalStatus.COMPLETED);
        return equipmentRentals.stream()
                .map(equipmentRentalMapper::equipmentRentalToEquipmentRentalDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public void cancelEquipmentRental(Integer equipmentRentalId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        EquipmentRental equipmentRental = equipmentRentalRepository.findById(equipmentRentalId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!equipmentRental.getUser().equals(user)) {
            throw new IllegalStateException("You are not authorized to cancel this rental.");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        long hoursUntilStart = ChronoUnit.HOURS.between(currentDateTime, equipmentRental.getStartDate());

        if (hoursUntilStart < 6) {
            throw new IllegalStateException("The rental can only be canceled at least 6 hours before the start time.");
        }

        Equipment equipment = equipmentRental.getEquipment();
        String emailBody = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                "<p>Your equipment rental has been canceled successfully.</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(user.getEmail(), "BikeBliss Rental Cancellation", emailBody);

        equipmentRepository.save(equipment);
        equipmentRentalRepository.delete(equipmentRental);
    }
    @Scheduled(cron = "0 0 * * * *") // cron job rulează la fiecare oră.
    @Transactional
    public void sendEndRentalReminder() {
        List<EquipmentRental> equipmentRentals = equipmentRentalRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (EquipmentRental equipmentRental : equipmentRentals) {
            if (equipmentRental.getRentalStatus() == RentalStatus.ACTIVE) {
                long hoursUntilEnd = ChronoUnit.HOURS.between(now, equipmentRental.getEndDate());

                if (hoursUntilEnd == 1) {
                    User user = equipmentRental.getUser();
                    Equipment equipment = equipmentRental.getEquipment();
                    String emailBody = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                            "<p>Your rental period for the bike model <strong>" + equipment.getEquipmentModel() + "</strong> is about to end in one hour.</p>" +
                            "<p>Please remember that you need to return the equipment within 12 hours after the end of the rental period.</p>" +
                            "<p>If you have any questions or need further assistance, please contact us.</p>" +
                            "<p>Best regards,</p>" +
                            "<p>BikeBliss Team</p>";

                    emailService.sendHtmlEmail(user.getEmail(), "Reminder: Equipment Rental Period Ending Soon", emailBody);
                }
            }
        }
    }
    public List<EquipmentRentalDTO> getAllRentals() {
        List<EquipmentRental> rentals = equipmentRentalRepository.findAll();
        return rentals.stream()
                .map(equipmentRentalMapper::equipmentRentalToEquipmentRentalDTO)
                .collect(Collectors.toList());
    }
}
