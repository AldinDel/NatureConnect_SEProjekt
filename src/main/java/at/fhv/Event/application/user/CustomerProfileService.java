package at.fhv.Event.application.user;

import at.fhv.Event.domain.model.user.CustomerProfile;
import at.fhv.Event.domain.model.user.CustomerProfileRepository;
import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserAccountRepository userAccountRepository;
    private final AvatarService avatarService;

    public CustomerProfileService(CustomerProfileRepository customerProfileRepository,
                                  UserAccountRepository userAccountRepository,
                                  AvatarService avatarService) {
        this.customerProfileRepository = customerProfileRepository;
        this.userAccountRepository = userAccountRepository;
        this.avatarService = avatarService;
    }

    public CustomerProfile getProfileByUserId(Long userId) {
        return customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Customer profile not found"));
    }

    public CustomerProfile updateProfile(CustomerProfile profile) {
        return customerProfileRepository.save(profile);
    }

    public CustomerProfile getProfileByEmail(String email) {
        return customerProfileRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Customer profile not found"));
    }

    public CustomerProfile getOrCreateProfileByEmail(String email) {
        return customerProfileRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserAccount user = userAccountRepository
                            .findByEmailIgnoreCase(email)
                            .orElseThrow();

                    CustomerProfile profile = new CustomerProfile();
                    profile.setUser(user);
                    profile.setFirstName(user.getFirstName());
                    profile.setLastName(user.getLastName());
                    profile.setEmail(user.getEmail());
                    profile.setCreatedAt(OffsetDateTime.now());
                    profile.setUpdatedAt(OffsetDateTime.now());

                    return customerProfileRepository.save(profile);
                });
    }

    public void updateAvatar(CustomerProfile profile, MultipartFile file) {
        String url = avatarService.upload(file);
        profile.setAvatarUrl(url);
        profile.setUpdatedAt(OffsetDateTime.now());
    }

    public void removeAvatar(CustomerProfile profile) {
        if (profile.getAvatarUrl() != null) {
            avatarService.delete(profile.getAvatarUrl());
            profile.setAvatarUrl(null);
            profile.setUpdatedAt(OffsetDateTime.now());
        }
    }
}


