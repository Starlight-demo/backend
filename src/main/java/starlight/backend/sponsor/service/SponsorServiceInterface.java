package starlight.backend.sponsor.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import starlight.backend.sponsor.model.response.SponsorFullInfo;
import starlight.backend.sponsor.model.response.UnusableKudos;

public interface SponsorServiceInterface {
    UnusableKudos getUnusableKudos(long sponsorId);

    SponsorFullInfo getSponsorFullInfo(long sponsorId, Authentication auth);

    void deleteSponsor(long sponsorId, Authentication authentication, HttpServletRequest request);
}
