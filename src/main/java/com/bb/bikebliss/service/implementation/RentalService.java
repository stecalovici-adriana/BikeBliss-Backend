package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.*;
import com.bb.bikebliss.repository.BikeRepository;
import com.bb.bikebliss.repository.RentalRepository;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.service.dto.RentalDTO;
import com.bb.bikebliss.service.dto.UnavailableDateDTO;
import com.bb.bikebliss.service.mapper.RentalMapper;
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
public class RentalService {

    private final BikeRepository bikeRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public RentalService(RentalRepository rentalRepository, BikeRepository bikeRepository,
                         UserRepository userRepository, EmailService emailService) {
        this.rentalRepository = rentalRepository;
        this.rentalMapper = RentalMapper.INSTANCE;
        this.bikeRepository = bikeRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public RentalDTO createRental(RentalDTO rentalDTO, Integer modelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated to rent a bike");
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LocalDate startDateOnly = rentalDTO.startDate();
        LocalDate endDateOnly = rentalDTO.endDate();

        LocalDateTime startDate = startDateOnly.atTime(6, 0);
        LocalDateTime endDate = endDateOnly.atTime(23, 59, 59, 999999999);

        if (startDateOnly.equals(endDateOnly)) {
            startDate = startDateOnly.atTime(6, 0);
            endDate = startDateOnly.atTime(23, 59, 59, 999999999);
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        long hoursUntilStart = ChronoUnit.HOURS.between(currentDateTime, startDate);
        if (hoursUntilStart < 6) {
            throw new IllegalStateException("The rental must be made at least 6 hours before the start time.");
        }

        long daysUntilStart = ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), startDate.toLocalDate());
        if (daysUntilStart > 60) {
            throw new IllegalStateException("The rental cannot be made more than 60 days in advance.");
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        if (daysBetween < 1) {
            throw new IllegalStateException("The rental period must be at least 6 hours.");
        } else if (daysBetween > 60) {
            throw new IllegalStateException("The rental period cannot exceed 60 days.");
        }

        Bike availableBike = bikeRepository.findAvailableBikes(modelId, startDate, endDate)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available bikes for the selected model."));

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setBike(availableBike);
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setRentalStatus(RentalStatus.PENDING);

        BigDecimal pricePerDay = availableBike.getBikeModel().getPricePerDay();
        BigDecimal totalPrice = pricePerDay.multiply(BigDecimal.valueOf(daysBetween));
        rental.setTotalPrice(totalPrice);

        rental = rentalRepository.save(rental);

        notifyAdminOfPendingRental(rental);

        return rentalMapper.rentalToRentalDTO(rental);
    }

    private void notifyAdminOfPendingRental(Rental rental) {
        String adminEmail = "adriana.stecalovici200@gmail.com";
        String subject = "New Rental Pending Approval";
        String body = "A new rental by is pending approval.\nRental Details:\n" +
                "Start Date: " + rental.getStartDate() +
                "\nEnd Date: " + rental.getEndDate() +
                "\nTotal Price: " + rental.getTotalPrice();

        emailService.sendHtmlEmail(adminEmail, subject, body);
    }
    public void approveRental(Integer rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        rental.setRentalStatus(RentalStatus.APPROVED);

        rentalRepository.save(rental);
        String locationAddress = rental.getBike().getBikeModel().getLocation().getAddress();
        String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(locationAddress, StandardCharsets.UTF_8);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedStartDate = rental.getStartDate().format(dateFormatter) + " from " + rental.getStartDate().format(timeFormatter);
        String formattedEndDate = rental.getEndDate().format(dateFormatter) + " at " + rental.getEndDate().format(timeFormatter);
        BigDecimal totalPrice = rental.getTotalPrice();

        String emailBody = "<p>Dear " + rental.getUser().getFirstName() + ",</p>" +
                "<p>Thank you for choosing BikeBliss. Your rental has been approved. Here are your rental details:</p>" +
                "<ul>" +
                "<li><strong>Bike Model:</strong> " + rental.getBike().getBikeModel().getBikeModel() + "</li>" +
                "<li><strong>Rental Period:</strong> " + formattedStartDate + " to " + formattedEndDate + "</li>" +
                "<li><strong>Total Price:</strong> " + totalPrice + " RON</li>" +
                "<li><strong>Pickup Location:</strong> <a href='" + mapsLink + "' target='_blank'>" + locationAddress + "</a></li>" +
                "</ul>" +
                "<p>You can pick up the bike starting from 6:00 AM at the location mentioned above.</p>" +
                "<p>We hope you enjoy your ride!</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(rental.getUser().getEmail(), "BikeBliss Rental Approval", emailBody);
    }

    public void rejectRental(Integer rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        rental.setRentalStatus(RentalStatus.REJECTED);
        rentalRepository.save(rental);

        String locationAddress = rental.getBike().getBikeModel().getLocation().getAddress();
        String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(locationAddress, StandardCharsets.UTF_8);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedStartDate = rental.getStartDate().format(dateFormatter) + " from " + rental.getStartDate().format(timeFormatter);
        String formattedEndDate = rental.getEndDate().format(dateFormatter) + " at " + rental.getEndDate().format(timeFormatter);
        BigDecimal totalPrice = rental.getTotalPrice();

        String emailBody = "<p>Dear " + rental.getUser().getFirstName() + ",</p>" +
                "<p>We regret to inform you that your rental request for the following bike has been rejected:</p>" +
                "<ul>" +
                "<li><strong>Bike Model:</strong> " + rental.getBike().getBikeModel().getBikeModel() + "</li>" +
                "<li><strong>Rental Period:</strong> " + formattedStartDate + " to " + formattedEndDate + "</li>" +
                "<li><strong>Total Price:</strong> " + totalPrice + " RON</li>" +
                "<li><strong>Intended Pickup Location:</strong> <a href='" + mapsLink + "' target='_blank'>" + locationAddress + "</a></li>" +
                "</ul>" +
                "<p>Please contact us if you have any questions or need further assistance.</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(rental.getUser().getEmail(), "BikeBliss Rental Rejection", emailBody);
    }

    public List<UnavailableDateDTO> getUnavailableDatesForModel(Integer modelId) {
        List<Rental> rentals = rentalRepository.findRentalsByModelId(modelId);
        // Fetch the total number of bikes for this model
        int totalBikes = bikeRepository.countByModelId(modelId);
        // Map of dates to count of bikes rented
        Map<LocalDate, Integer> bikeCountPerDay = new HashMap<>();
        for (Rental rental : rentals) {
            rental.getStartDate().toLocalDate().datesUntil(rental.getEndDate().toLocalDate().plusDays(1))
                    .forEach(date -> bikeCountPerDay.put(date, bikeCountPerDay.getOrDefault(date, 0) + 1));
        }
        // Collect all dates where the number of bikes rented is equal to or greater than the total number of bikes
        List<LocalDate> unavailableDates = bikeCountPerDay.entrySet().stream()
                .filter(entry -> entry.getValue() >= totalBikes)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        // Convert the list of dates to date ranges (if needed) or just return as individual dates
        return convertToUnavailableDateDTOs(unavailableDates);
    }
    private List<UnavailableDateDTO> convertToUnavailableDateDTOs(List<LocalDate> unavailableDates) {
        return unavailableDates.stream()
                .map(date -> new UnavailableDateDTO(date, date)) // Assuming each date is a single day range
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateRentalStatuses() {
        List<Rental> rentals = rentalRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Rental rental : rentals) {
            if (rental.getRentalStatus() == RentalStatus.PENDING && now.isAfter(rental.getStartDate()) && now.isBefore(rental.getEndDate())) {
                rental.setRentalStatus(RentalStatus.ACTIVE);
            } else if (rental.getRentalStatus() == RentalStatus.ACTIVE && now.isAfter(rental.getEndDate())) {
                rental.setRentalStatus(RentalStatus.COMPLETED);
            }
            rentalRepository.save(rental);
        }
    }

    public List<RentalDTO> getUserRentals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Rental> rentals = rentalRepository.findByUser(user);
        return rentals.stream()
                .map(rentalMapper::rentalToRentalDTO)
                .collect(Collectors.toList());
    }
    public List<RentalDTO> getActiveRentals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Rental> rentals;
        if (user.getUserRole() == UserRole.ADMIN) {
            rentals = rentalRepository.findByRentalStatus(RentalStatus.ACTIVE);
        } else {
            rentals = rentalRepository.findByRentalStatusAndUser(RentalStatus.ACTIVE, user);
        }
        return rentals.stream()
                .map(rentalMapper::rentalToRentalDTO)
                .collect(Collectors.toList());
    }
    public List<RentalDTO> getPendingRentals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Rental> rentals;
        if (user.getUserRole() == UserRole.ADMIN) {
            rentals = rentalRepository.findByRentalStatus(RentalStatus.PENDING);
        } else {
            rentals = rentalRepository.findByRentalStatusAndUser(RentalStatus.PENDING, user);
        }
        return rentals.stream()
                .map(rentalMapper::rentalToRentalDTO)
                .collect(Collectors.toList());
    }
    public List<RentalDTO> getCompletedRentals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Rental> rentals;
        if (user.getUserRole() == UserRole.ADMIN) {
            rentals = rentalRepository.findByRentalStatus(RentalStatus.COMPLETED);
        } else {
            rentals = rentalRepository.findByRentalStatusAndUser(RentalStatus.COMPLETED, user);
        }
        return rentals.stream()
                .map(rentalMapper::rentalToRentalDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public void cancelRental(Integer rentalId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!rental.getUser().equals(user)) {
            throw new IllegalStateException("You are not authorized to cancel this rental.");
        }

        long hoursUntilStart = ChronoUnit.HOURS.between(LocalDateTime.now(), rental.getStartDate());
        if (hoursUntilStart < 6) {
            throw new IllegalStateException("The rental can only be canceled at least 6 hours before the start time.");
        }

        Bike bike = rental.getBike();
        String emailBody = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                "<p>Your bike rental has been canceled successfully.</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(user.getEmail(), "BikeBliss Rental Cancellation", emailBody);

        bikeRepository.save(bike);
        rentalRepository.delete(rental);
    }
    @Scheduled(cron = "0 0 * * * *") // Acest cron job rulează la fiecare oră.
    @Transactional
    public void sendEndRentalReminder() {
        List<Rental> rentals = rentalRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Rental rental : rentals) {
            if (rental.getRentalStatus() == RentalStatus.ACTIVE) {
                long hoursUntilEnd = ChronoUnit.HOURS.between(now, rental.getEndDate());

                if (hoursUntilEnd == 1) {
                    User user = rental.getUser();
                    Bike bike = rental.getBike();
                    String emailBody = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                            "<p>Your rental period for the bike model <strong>" + bike.getBikeModel() + "</strong> is about to end in one hour.</p>" +
                            "<p>Please remember that you need to return the bike within 12 hours after the end of the rental period.</p>" +
                            "<p>If you have any questions or need further assistance, please contact us.</p>" +
                            "<p>Best regards,</p>" +
                            "<p>BikeBliss Team</p>";

                    emailService.sendHtmlEmail(user.getEmail(), "Reminder: Bike Rental Period Ending Soon", emailBody);
                }
            }
        }
    }

}