package com.estore.customer.service;

import com.estore.customer.dto.*;
import com.estore.customer.entity.*;
import com.estore.customer.repository.*;
import com.estore.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileResponse getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
        return toResponse(profile);
    }

    public ProfileResponse saveProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Profile profile = profileRepository.findByUserId(userId)
                .orElse(Profile.builder().user(user).build());

        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setCountry(request.getCountry());

        return toResponse(profileRepository.save(profile));
    }

    private ProfileResponse toResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .city(profile.getCity())
                .country(profile.getCountry())
                .userId(profile.getUser().getId())
                .build();
    }
}