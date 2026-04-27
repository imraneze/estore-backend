package com.estore.customer.controller;

import com.estore.customer.dto.*;
import com.estore.customer.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<ProfileResponse> saveProfile(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(profileService.saveProfile(userId, request));
    }
}