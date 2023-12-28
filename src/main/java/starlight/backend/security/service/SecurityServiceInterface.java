package starlight.backend.security.service;

import org.springframework.security.core.Authentication;
import starlight.backend.security.model.UserDetailsImpl;
import starlight.backend.security.model.request.NewUser;
import starlight.backend.security.model.response.SessionInfo;

import java.util.Objects;

public interface SecurityServiceInterface {
    SessionInfo register(NewUser newUser);

    SessionInfo loginInfo(Authentication auth);

    default boolean checkingLoggedAndToken(long userId, Authentication auth) {
        return Objects.equals(auth.getName(), String.valueOf(userId));
    }

    boolean isSponsorActive(Authentication auth);

    String getJWTToken(UserDetailsImpl authentication, long id);

    String createScope(UserDetailsImpl authentication);

    SessionInfo registerSponsor(NewUser newUser);

    SessionInfo registerAdmin(NewUser newUser);

    SessionInfo loginInfoSponsor(Authentication auth);

    SessionInfo loginInfoAdmin(Authentication auth);
}
