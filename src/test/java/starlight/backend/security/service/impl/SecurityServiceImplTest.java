//package starlight.backend.security.service.impl;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtClaimsSet;
//import org.springframework.security.oauth2.jwt.JwtEncoder;
//import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
//import starlight.backend.exception.EmailAlreadyOccupiedException;
//import starlight.backend.security.MapperSecurity;
//import starlight.backend.security.model.UserDetailsImpl;
//import starlight.backend.security.model.request.NewUser;
//import starlight.backend.security.model.response.SessionInfo;
//import starlight.backend.sponsor.SponsorRepository;
//import starlight.backend.sponsor.model.entity.SponsorEntity;
//import starlight.backend.sponsor.model.enums.SponsorStatus;
//import starlight.backend.talent.model.entity.TalentEntity;
//import starlight.backend.talent.repository.TalentRepository;
//
//import java.time.Instant;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class SecurityServiceImplTest {
//
//    @Mock
//    private JwtEncoder jwtEncoder;
//
//    @Mock
//    private TalentRepository userRepository;
//
//    @Mock
//    private SponsorRepository sponsorRepository;
//
//    @Mock
//    private MapperSecurity mapperSecurity;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private SecurityServiceImpl securityService;
//
//    private NewUser newUser;
//    private TalentEntity user;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        user = TalentEntity.builder()
//                .talentId(1L)
//                .fullName("Jon Snow")
//                .email("myemail@gmail.com")
//                .password("Secret123")
//                .build();
//
//        newUser = NewUser.builder()
//                .fullName(user.getFullName())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .build();
//    }
//
//    @DisplayName("Test loginInfo: User found")
//    @Test
//    void testLoginInfo_UserFound() {
//        // Given
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(user.getEmail(), user.getPassword());
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(Instant.now())
//                .expiresAt(Instant.now().plusSeconds(180))
//                .subject(String.valueOf(user.getTalentId()))
//                .claim("scope", "ROLE_TALENT")
//                .build();
//        SessionInfo expectedSessionInfo = SessionInfo.builder().build();
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//        when(mapperSecurity.toUserDetailsImpl(user)).thenReturn(userDetails);
//        when(mapperSecurity.toSessionInfo(expectedSessionInfo.token())).thenReturn(expectedSessionInfo);
//        Jwt mockJwt = mock(Jwt.class);
//        when(mockJwt.getClaims()).thenReturn(any());
//        when(jwtEncoder.encode(JwtEncoderParameters.from(claims))).thenReturn(mockJwt);
//
//        // When
//        SessionInfo sessionInfo = securityService.loginInfo(authentication);
//
//        // Then
//        assertEquals(expectedSessionInfo.token(), sessionInfo.token());
//        verify(userRepository, times(1)).findByEmail(user.getEmail());
//        verify(mapperSecurity, times(1)).toUserDetailsImpl(user);
//        verify(mapperSecurity, times(1)).toSessionInfo(expectedSessionInfo.token());
//    }
//
//    @DisplayName("Test loginInfo: User not found")
//    @Test
//    void testLoginInfo_UserNotFound() {
//        // Given
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
//
//        // When/Then
//        assertThrows(UsernameNotFoundException.class, () -> securityService.loginInfo(authentication));
//
//        verify(userRepository, times(1)).findByEmail(user.getEmail());
//        verifyNoMoreInteractions(mapperSecurity, jwtEncoder);
//    }
//
//    @DisplayName("Test register: Successful registration")
//    @Test
//    void testRegister_SuccessfulRegistration() {
//        // Given
//        TalentEntity savedUser = TalentEntity.builder()
//                .talentId(1L)
//                .fullName(newUser.fullName())
//                .email(newUser.email())
//                .password("encoded_password")
//                .build();
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(Instant.now())
//                .expiresAt(Instant.now().plusSeconds(180))
//                .subject(String.valueOf(user.getTalentId()))
//                .claim("scope", "ROLE_TALENT")
//                .build();
//        SessionInfo expectedSessionInfo = SessionInfo.builder().build();
//        UserDetailsImpl userDetails = new UserDetailsImpl(newUser.email(), savedUser.getPassword());
//
//        when(userRepository.existsByEmail(newUser.email())).thenReturn(false);
//        when(userRepository.save(any(TalentEntity.class))).thenReturn(savedUser);
//        when(securityService.saveNewUser(newUser)).thenReturn(savedUser);
//        when(mapperSecurity.toUserDetailsImpl(savedUser)).thenReturn(userDetails);
//        when(mapperSecurity.toSessionInfo(expectedSessionInfo.token())).thenReturn(expectedSessionInfo);
//        when(jwtEncoder.encode(JwtEncoderParameters.from(claims))).thenReturn(mock(Jwt.class));
//        when(securityService.saveNewUser(newUser)).thenReturn(savedUser);
//        when(mapperSecurity.toUserDetailsImpl(savedUser)).thenReturn(userDetails);
//        when(mapperSecurity.toSessionInfo(expectedSessionInfo.token())).thenReturn(expectedSessionInfo);
//        Jwt mockJwt = mock(Jwt.class);
//        when(mockJwt.getClaims()).thenReturn(any());
//        when(jwtEncoder.encode(JwtEncoderParameters.from(claims))).thenReturn(mockJwt);
//
//        // When
//        SessionInfo sessionInfo = securityService.register(newUser);
//
//        // Then
//        assertEquals(expectedSessionInfo.token(), sessionInfo.token());
//        verify(userRepository, times(1)).save(any(TalentEntity.class));
//        verify(mapperSecurity, times(1)).toUserDetailsImpl(savedUser);
//        verify(mapperSecurity, times(1)).toSessionInfo(expectedSessionInfo.token());
//    }
//
//    @DisplayName("Test register: User email already occupied")
//    @Test
//    void testRegister_UserEmailAlreadyOccupied() {
//        // Given
//        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
//
//        // When/Then
//        assertThrows(EmailAlreadyOccupiedException.class, () -> securityService.register(newUser));
//
//        verify(userRepository, times(1)).existsByEmail(user.getEmail());
//    }
//
//    @DisplayName("Test isSponsorActive: Active sponsor")
//    @Test
//    void testIsSponsorActive_ActiveSponsor() {
//        // Given
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                user.getEmail(),
//                user.getPassword());
//
//        SponsorEntity sponsorACTIVE = SponsorEntity.builder()
//                .sponsorId(1L)
//                .fullName("Sponsor")
//                .email(user.getEmail())
//                .password("encoded_password")
//                .status(SponsorStatus.ACTIVE)
//                .build();
//
//        when(sponsorRepository.findById(sponsorACTIVE.getSponsorId())).thenReturn(Optional.of(sponsorACTIVE));
//
//        // When
//        boolean isSponsorActive = securityService.isSponsorActive(authentication);
//
//        // Then
//        assertTrue(isSponsorActive);
//        verifyNoMoreInteractions(sponsorRepository);
//    }
//
//    @DisplayName("Test isSponsorActive: User is not a sponsor")
//    @Test
//    void testIsSponsorActive_UserNotSponsor() {
//        // Given
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
//
//        // When
//        boolean isSponsorActive = securityService.isSponsorActive(authentication);
//
//        // Then
//        assertTrue(isSponsorActive);
//        verifyNoInteractions(sponsorRepository);
//    }
//
//    @DisplayName("Test loginSponsor: Successful sponsor login")
//    @Test
//    void testLoginSponsor_SuccessfulSponsorLogin() {
//        // Given
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                user.getEmail(),
//                user.getPassword());
//
//        SponsorEntity sponsor = SponsorEntity.builder()
//                .sponsorId(1L)
//                .fullName("Sponsor")
//                .email(user.getEmail())
//                .password("encoded_password")
//                .status(SponsorStatus.ACTIVE)
//                .build();
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(Instant.now())
//                .expiresAt(Instant.now().plusSeconds(180))
//                .subject(String.valueOf(sponsor.getSponsorId()))
//                .claim("scope", "ROLE_SPONSOR")
//                .build();
//        SessionInfo expectedSessionInfo = SessionInfo.builder().build();
//        UserDetailsImpl userDetails = new UserDetailsImpl(sponsor.getEmail(), sponsor.getPassword());
//
//        when(sponsorRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(sponsor));
//        when(mapperSecurity.toUserDetailsImplForSponsor(sponsor)).thenReturn(userDetails);
//        when(mapperSecurity.toSessionInfo(expectedSessionInfo.token())).thenReturn(expectedSessionInfo);
//        Jwt mockJwt = mock(Jwt.class);
//        when(mockJwt.getClaims()).thenReturn(any());
//        when(jwtEncoder.encode(JwtEncoderParameters.from(claims))).thenReturn(mockJwt);
//
//        // When
//        SessionInfo sessionInfo = securityService.loginSponsor(authentication);
//
//        // Then
//        assertEquals(expectedSessionInfo.token(), sessionInfo.token());
//        verify(sponsorRepository, times(1)).findByEmail(authentication.getName());
//        verify(mapperSecurity, times(1)).toUserDetailsImplForSponsor(sponsor);
//        verify(mapperSecurity, times(1)).toSessionInfo(expectedSessionInfo.token());
//    }
//
//    @DisplayName("Test loginSponsor: Sponsor not found")
//    @Test
//    void testLoginSponsor_SponsorNotFound() {
//        // Given
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
//
//        when(sponsorRepository.findByEmail(authentication.getName())).thenReturn(Optional.empty());
//
//        // When/Then
//        assertThrows(UsernameNotFoundException.class, () -> securityService.loginSponsor(authentication));
//
//        verify(sponsorRepository, times(1)).findByEmail(authentication.getName());
//        verifyNoMoreInteractions(mapperSecurity, jwtEncoder);
//    }
//
//    @DisplayName("Test saveNewUser: Successful save")
//    @Test
//    void testSaveNewUser_SuccessfulSave() {
//        // Given
//        TalentEntity savedUser = TalentEntity.builder()
//                .talentId(1L)
//                .fullName(newUser.fullName())
//                .email(newUser.email())
//                .password("encoded_password")
//                .build();
//
//        when(passwordEncoder.encode(newUser.password())).thenReturn("encoded_password");
//        when(userRepository.save(any(TalentEntity.class))).thenReturn(savedUser);
//
//        // When
//        TalentEntity result = securityService.saveNewUser(newUser);
//
//        // Then
//        assertEquals(savedUser.getTalentId(), result.getTalentId());
//        assertEquals(savedUser.getFullName(), result.getFullName());
//        assertEquals(savedUser.getEmail(), result.getEmail());
//        assertEquals(savedUser.getPassword(), result.getPassword());
//
//        verify(passwordEncoder, times(1)).encode(newUser.password());
//        verify(userRepository, times(1)).save(any(TalentEntity.class));
//    }
//}
