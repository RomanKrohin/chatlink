package user_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import user_service.models.User;
import user_service.security.JwtUtil;

import java.util.Collections;
import java.util.logging.Logger;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;


import user_service.models.ServiceResponse;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = Logger.getLogger("pivo");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, CountDownLatch> latches = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ServiceResponse> responses = new ConcurrentHashMap<>();

    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        sendUserCheckRequest(login);

        ServiceResponse authResponse = getAuthResponse(login);
        
        return new org.springframework.security.core.userdetails.User(
                authResponse.getMessage()[0],
                authResponse.getMessage()[1],
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + authResponse.getMessage()[2]))
        );
    }

    public String authenticate(String login, String password) throws InterruptedException {
        sendUserCheckRequest(login);

        ServiceResponse authResponse = getAuthResponse(login);

        if (new BCryptPasswordEncoder().matches(password, authResponse.getMessage()[1])) {
            return jwtUtil.generateToken(login);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public String registerUser(User user) throws JsonProcessingException, InterruptedException {
        sendUserCheckRequest(user.getLogin());

        ServiceResponse authResponse = getAuthResponse(user.getLogin());

        if (authResponse.isStatus()) {
            throw new DataIntegrityViolationException(null);
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        String userJson = objectMapper.writeValueAsString(user);
        kafkaTemplate.send("user-registration", userJson);

        return jwtUtil.generateToken(user.getLogin());
    }

    private void sendUserCheckRequest(String login) {
        CountDownLatch latch = new CountDownLatch(1);
        latches.put(login, latch);
        kafkaTemplate.send("user-check", login);
    }

    private ServiceResponse getAuthResponse(String login) {
        CountDownLatch latch = latches.get(login);


        try {
            if (latch != null) {
                latch.await(3, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Timeout waiting for authentication response");
        }
        

        ServiceResponse authResponse = responses.get(login);

        latches.remove(login);
        responses.remove(login);

        if (authResponse == null) {
            throw new RuntimeException("Timeout waiting for authentication response");
        }

        return authResponse;
    }

    @KafkaListener(topics = "auth-check", groupId = "auth-group")
    public void userExistsByLogin(String jsonResponse) throws JsonProcessingException {
        ServiceResponse response = new ObjectMapper().readValue(jsonResponse, ServiceResponse.class);

        String login = response.getMessage()[0];
        responses.put(login, response);

        CountDownLatch latch = latches.get(login);
        if (latch != null) {
            latch.countDown();
        }
    }
}

