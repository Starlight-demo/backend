package starlight.backend.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import starlight.backend.user.model.enums.Role;
import starlight.backend.sponsor.model.enums.SponsorStatus;

import java.util.Collection;
import java.util.List;


public class UserDetailsImpl implements UserDetails {

    private String username;
    private String password;
    private Role role;
    private SponsorStatus status;

    public UserDetailsImpl(String username, String password, Role role, SponsorStatus status) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStatus() {
        return status.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
