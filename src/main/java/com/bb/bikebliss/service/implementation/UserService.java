package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.exception.EmailAlreadyExistsException;
import com.bb.bikebliss.exception.UserNotFoundException;
import com.bb.bikebliss.exception.UsernameAlreadyExistsException;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.service.dto.UserDTO;
import com.bb.bikebliss.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("unused")
public class UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       BCryptPasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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