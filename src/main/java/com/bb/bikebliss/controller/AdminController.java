package com.bb.bikebliss.controller;

import com.bb.bikebliss.entity.UserRole;
import com.bb.bikebliss.service.dto.UserDTO;
import com.bb.bikebliss.service.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/changeRole/{userId}")
    public ResponseEntity<String> changeUserRole(@PathVariable Integer userId, @RequestBody UserRole newRole) {
        userService.updateUserRole(userId, newRole);
        return ResponseEntity.ok("User role updated successfully.");
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }
    @GetMapping("/listUsers")
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}
