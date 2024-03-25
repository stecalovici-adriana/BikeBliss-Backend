package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.entity.UserRole;
import com.bb.bikebliss.entity.VerificationToken;
import com.bb.bikebliss.exception.EmailAlreadyExistsException;
import com.bb.bikebliss.exception.UserNotFoundException;
import com.bb.bikebliss.exception.UsernameAlreadyExistsException;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.repository.VerificationTokenRepository;
import com.bb.bikebliss.service.dto.UserDTO;
import com.bb.bikebliss.service.dto.UserRegistrationDTO;
import com.bb.bikebliss.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("unused")
public class UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public UserDTO createUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }
        if (userRepository.findByUsername(registrationDTO.username()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        String[] nameParts = registrationDTO.fullName().trim().split("\\s+");
        String lastName = nameParts[nameParts.length - 1];
        String firstName = String.join(" ", Arrays.copyOf(nameParts, nameParts.length - 1));

        User newUser = userMapper.userRegistrationDTOToUser(registrationDTO);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPassword(passwordEncoder.encode(registrationDTO.password()));
        newUser.setVerified(false);
        newUser.setUserRole(UserRole.USER);
        User savedUser = userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/api/users/verify?token=" + token;
        emailService.sendEmail(savedUser.getEmail(), "Confirm your email", "Please click on the following link to verify your email: " + verificationLink);

        return userMapper.userToUserDTO(savedUser);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }
    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameAlreadyExistsException("User not found with username: " + username));
        return userMapper.userToUserDTO(user);
    }
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailAlreadyExistsException("User not found with email: " + email));
        return userMapper.userToUserDTO(user);
    }
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
    public UserDTO updateUser(Integer userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (userDTO.email() != null && !userDTO.email().equals(user.getEmail()) && userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new IllegalStateException("Email is already in use by another account.");
        }
        if (userDTO.username() != null && !userDTO.username().equals(user.getUsername()) && userRepository.findByUsername(userDTO.username()).isPresent()) {
            throw new IllegalStateException("Username is already taken.");
        }

        if (userDTO.firstName() != null) user.setFirstName(userDTO.firstName());
        if (userDTO.lastName() != null) user.setLastName(userDTO.lastName());
        if (userDTO.email() != null) user.setEmail(userDTO.email());
        if (userDTO.username() != null) user.setUsername(userDTO.username());

        User updatedUser = userRepository.save(user);
        return userMapper.userToUserDTO(updatedUser);
    }

    public void changePassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}