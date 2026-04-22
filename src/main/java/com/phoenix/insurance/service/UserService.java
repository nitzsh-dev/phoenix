package com.phoenix.insurance.service;

import com.phoenix.insurance.dto.UserRegistrationDto;
import com.phoenix.insurance.model.*;
import com.phoenix.insurance.repository.IdentificationSessionRepository;
import com.phoenix.insurance.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.phoenix.insurance.model.ContactMethod.EMAIL;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final IdentificationSessionRepository identificationSessionRepository;
    private final IdentificationService identificationService;
    // Simple counter to generate unique IDs in memory
    private final AtomicLong idCounter = new AtomicLong(1);

    public UserService(UserRepository userRepository, IdentificationSessionRepository identificationSessionRepository, @Lazy IdentificationService identificationService) {
        this.userRepository = userRepository;
        this.identificationSessionRepository = identificationSessionRepository;
        this.identificationService = identificationService;
    }

    // Register a new user
    public Long registerUser(UserRegistrationDto dto) {
        // 1. Create the user entity
        User user = new User();
        user.setId(dto.id());
        user.setPhone(new PhoneNumber(dto.phone()));
        user.setEmail(new EmailAddress(dto.email()));

        // 2. Save to "DB"
        userRepository.save(user);

        // 3. Auto-initiate OTP
        IdentificationSession session = identificationService.initiateIdentification(
                user.getId(),
                EMAIL
        );

        return session.getId();
    }

    public String login(String userId, ContactMethod method) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "משתמש לא נמצא במערכת, עליך להירשם"));

        IdentificationSession session = identificationService.initiateIdentification(userId, method);
        return "קוד אימות נשלח בהצלחה ל-" + (method == EMAIL ? user.getEmail().value() : user.getPhone().value()) + " קוד הסשן הוא " + session.getId();
    }

    // Find a user by ID
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    public boolean existsById(String id) {
        return userRepository.findById(id).isPresent();
    }

    // Purchase a product for a user with duplicate check
    public void purchaseProduct(Long sessionId, Long productId) {
        IdentificationSession session = identificationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "סשן לא נמצא - עליך להתחבר מחדש"));

        if (session.getStatus() != SessionStatus.IDENTIFIED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "חובה לבצע אימות OTP כדי לרכוש במוצרים");
        }

        if (LocalDateTime.now().isAfter(session.getExpiryTime())) {
            throw new ResponseStatusException(HttpStatus.GONE, "הסשן פג תוקף, בצע לוגין מחדש");
        }

        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "משתמש לא נמצא"));

        // 2. Business Logic: Check if product was already purchased
        if (user.getPurchasedProductIds().contains(productId)) {
            // In a real app, you might want to create a custom exception like ProductAlreadyOwnedException
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already owns product with ID: " + productId);
        }

        // 3. Action: Add product ID
        user.getPurchasedProductIds().add(productId);
        userRepository.save(user);
    }

    public Set<Long> getUserProductsBySession(Long sessionId) {
        IdentificationSession session = identificationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "סשן לא נמצא - עליך להתחבר מחדש"));

        if (session.getStatus() != SessionStatus.IDENTIFIED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "חובה לבצע אימות OTP כדי לצפות במוצרים");
        }

        if (LocalDateTime.now().isAfter(session.getExpiryTime())) {
            throw new ResponseStatusException(HttpStatus.GONE, "הסשן פג תוקף, בצע לוגין מחדש");
        }

        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "משתמש לא נמצא"));

        return user.getPurchasedProductIds();
    }
}