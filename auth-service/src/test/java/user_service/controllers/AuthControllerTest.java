package user_service.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import user_service.controllers.AuthController;
import user_service.models.User;
import user_service.services.AuthService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }



    @Test
    void register_test() throws JsonProcessingException, InterruptedException {
        when(authService.registerUser(any(User.class))).thenReturn("token");

        User user = new User();
        var response = authController.registerUser(user);
        assertEquals("token", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void loginUser_test() throws InterruptedException {
        when(authService.authenticate(any(String.class), any(String.class))).thenReturn("token");
        var response = authController.loginUser("login", "password");
        assertEquals("token", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void register_test_failure() throws JsonProcessingException, InterruptedException {
        when(authService.registerUser(any(User.class))).thenThrow(RuntimeException.class);

        User user = new User();
        var response = authController.registerUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void login_test_failure() throws InterruptedException {
        when(authService.authenticate(any(String.class), any(String.class))).thenThrow(RuntimeException.class);
        var response = authController.loginUser("login", "password");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }


}
