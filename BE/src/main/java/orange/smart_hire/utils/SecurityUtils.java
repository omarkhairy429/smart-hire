package orange.smart_hire.utils;

import orange.smart_hire.model.CustomUserDetails;
import orange.smart_hire.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        throw new IllegalStateException("No authenticated user found in Security Context");
    }

    public static UUID getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }

    public static User getCurrentUser() {
        return getCurrentUserDetails().getUser();
    }
}