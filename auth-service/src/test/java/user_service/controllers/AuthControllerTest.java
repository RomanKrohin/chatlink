package user_service.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.BadRequestException;
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

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.logging.Logger;
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {







    @Mock
    private Logger logger;

    @Mock
    private AuthService authService;

    @InjectMocks
    ExceptionApiHandler exceptionApiHandler;

    @InjectMocks
    private AuthController authController;





    @Test
    void register_test() throws JsonProcessingException, InterruptedException, BadRequestException {
        String testUsername = "testUsername";
        String testPassword = "testPassword";

        User testUser = new User();

        testUser.setLogin(testUsername);
        testUser.setPassword(testPassword);

        when(authService.registerUser(any(User.class))).thenReturn("token");

        var response = authController.registerUser(testUser);

        assertEquals("token", response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void loginUser_test() throws InterruptedException, BadRequestException {
        String testUsername = "testUsername";
        String testPassword = "testPassword";

        User testUser = new User();
        testUser.setLogin(testUsername);
        testUser.setPassword(testPassword);

        when(authService.authenticate(testUsername, testPassword)).thenReturn("token");

        var response = authController.loginUser(testUser);

        assertEquals("token", response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void register_test_failure() throws JsonProcessingException, InterruptedException {

        try {
            authController.registerUser(any(User.class));
            fail("GG");
        } catch (BadRequestException e) {
            var response = exceptionApiHandler.handleBadRequestException(e) ;
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }


    @Test
    void login_test_failure() throws InterruptedException, BadRequestException {
        try {
            authController.loginUser(any(User.class));
            fail("GG");
        } catch (BadRequestException e) {
            var response = exceptionApiHandler.handleBadRequestException(e) ;
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

    }


}
