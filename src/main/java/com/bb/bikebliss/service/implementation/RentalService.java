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
import java.util.List;
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

        BigDecimal pricePerDay = availableBike.getBikeModel().getPricePerDay();
        BigDecimal totalPrice = pricePerDay.multiply(BigDecimal.valueOf(daysBetween));
        rental.setTotalPrice(totalPrice);

        rental.setRentalStatus(RentalStatus.PENDING);
        rental = rentalRepository.save(rental);

        // Obține informațiile despre locația bicicletei
        String locationAddress = availableBike.getBikeModel().getLocation().getAddress();
        String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(locationAddress, StandardCharsets.UTF_8);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        String formattedStartDate = startDate.format(dateFormatter) + " from " + startDate.format(timeFormatter);
        String formattedEndDate = endDate.format(dateFormatter) + " at " + endDate.format(timeFormatter);


        String emailBody = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                "<p>Thank you for choosing BikeBliss. Here are your rental details:</p>" +
                "<ul>" +
                "<li><strong>Bike Model:</strong> " + availableBike.getBikeModel().getBikeModel() + "</li>" +
                "<li><strong>Rental Period:</strong> " + formattedStartDate + " to " + formattedEndDate + "</li>" +
                "<li><strong>Total Price:</strong> " + totalPrice + " RON</li>" +
                "<li><strong>Pickup Location:</strong> <a href='" + mapsLink + "' target='_blank'>" + locationAddress + "</a></li>" +
                "</ul>" +
                "<p>You can pick up the bike starting from 6:00 AM at the location mentioned above.</p>" +
                "<p>We hope you enjoy your ride!</p>" +
                "<p>Best regards,</p>" +
                "<p>BikeBliss Team</p>";

        emailService.sendHtmlEmail(user.getEmail(), "BikeBliss Rental Confirmation", emailBody);

        return rentalMapper.rentalToRentalDTO(rental);
    }
    public List<UnavailableDateDTO> getUnavailableDatesForModel(Integer modelId) {
        List<Rental> rentals = rentalRepository.findRentalsByModelId(modelId);

        return rentals.stream()
                .map(rental -> new UnavailableDateDTO(
                        rental.getStartDate().toLocalDate(),
                        rental.getEndDate().toLocalDate()
                ))
                .collect(Collectors.toList());
    }


    @Scheduled(cron = "0 0 * * * *") //ruleaza din ora in ora
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
        List<Rental> rentals = rentalRepository.findByRentalStatus(RentalStatus.ACTIVE);
        return rentals.stream()
                .map(rentalMapper::rentalToRentalDTO)
                .collect(Collectors.toList());
    }
    public List<RentalDTO> getPendingRentals() {
        List<Rental> rentals = rentalRepository.findByRentalStatus(RentalStatus.PENDING);
        return rentals.stream()
                .map(rentalMapper::rentalToRentalDTO)
                .collect(Collectors.toList());
    }
    public List<RentalDTO> getCompletedRentals() {
        List<Rental> rentals = rentalRepository.findByRentalStatus(RentalStatus.COMPLETED);
        return rentals.stream()
                .map(rentalMapper::rentalToRentalDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public void cancelRental(Integer rentalId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!rental.getUser().equals(user)) {
            throw new IllegalStateException("You are not authorized to cancel this rental.");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        long hoursUntilStart = ChronoUnit.HOURS.between(currentDateTime, rental.getStartDate());

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

                if (hoursUntilEnd == 1) { // Trimite un e-mail cu o oră înainte de sfârșitul închirierii.
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