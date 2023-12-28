//package starlight.backend.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import starlight.backend.security.model.request.NewUser;
//import starlight.backend.security.model.response.SessionInfo;
//import starlight.backend.security.service.SecurityServiceInterface;
//import starlight.backend.talent.model.entity.TalentEntity;
//
//import java.util.Base64;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = SecurityController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class SecurityControllerTest {
//    @MockBean
//    private SecurityServiceInterface service;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private Authentication auth;
//
//    private NewUser newUser;
//
//    private TalentEntity user;
//
//    @BeforeEach
//    public void setup() {
//        user = TalentEntity.builder()
//                .talentId(1L)
//                .fullName("Jon Snow")
//                .email("myemail@gmail.com")
//                .password("Secret123")
//                .build();
//        newUser = NewUser.builder()
//                .fullName(user.getFullName())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .build();
//    }
//
//    @DisplayName("JUnit test for login talents")
//    @Test
//    void login() throws Exception {
//        //Given
//        SessionInfo sessionInfo = SessionInfo.builder().build();
//        when(service.loginInfo(auth)).thenReturn(sessionInfo);
//        String username = auth.getName();
//        String password = user.getPassword();
//        String base64Credentials = new String(Base64.getEncoder()
//                .encode((username + ":" + password).getBytes()));
//
//        //When //Then
//        mockMvc.perform(post("/api/v1/talents/login")
//                        .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("JUnit test for register method talents")
//    @Test
//    void register() throws Exception {
//        //Given
//        SessionInfo sessionInfo = SessionInfo.builder().build();
//        when(service.register(newUser)).thenReturn(sessionInfo);
//
//        //When //Then
//        mockMvc.perform(post("/api/v1/talents")
//                        .content(objectMapper.writeValueAsString(newUser))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isNotEmpty())
//                .andExpect(jsonPath("$.token").value(sessionInfo.token()));
//    }
//
//    @DisplayName("JUnit test for login Sponsor")
//    @Test
//    void loginSponsor() throws Exception {
//        //Given
//        SessionInfo sessionInfo = SessionInfo.builder().build();
//        when(service.loginSponsor(auth)).thenReturn(sessionInfo);
//        String username = auth.getName();
//        String password = user.getPassword();
//        String base64Credentials = new String(Base64.getEncoder()
//                .encode((username + ":" + password).getBytes()));
//
//        //When //Then
//        mockMvc.perform(post("/api/v1/sponsors/login")
//                        .header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("JUnit test for register method Sponsor")
//    @Test
//    void registerSponsor() throws Exception {
//        //Given
//        SessionInfo sessionInfo = SessionInfo.builder().build();
//        when(service.registerSponsor(newUser)).thenReturn(sessionInfo);
//
//        //When //Then
//        mockMvc.perform(post("/api/v1/sponsors")
//                        .content(objectMapper.writeValueAsString(newUser))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isNotEmpty())
//                .andExpect(jsonPath("$.token").value(sessionInfo.token()));
//    }
//}