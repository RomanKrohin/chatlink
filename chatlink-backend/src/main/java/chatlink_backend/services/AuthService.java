package chatlink_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import chatlink_backend.models.User;
import chatlink_backend.security.JwtUtil;

import java.util.Collections;
import java.util.logging.Logger;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = Logger.getLogger("pivo");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // User user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User user = new User();
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    public String authenticate(String login, String password) {
        // User user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User user = new User();
        if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return jwtUtil.generateToken(login);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public String registerUser(User user) throws JsonProcessingException {
        try {
            if (userExistsByLogin(user.getLogin())) throw new DataIntegrityViolationException(null);
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

            String userJson = objectMapper.writeValueAsString(user);
            kafkaTemplate.send("user-registration", userJson);

            return jwtUtil.generateToken(user.getLogin());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("User with login " + user.getLogin() + " already exists");
        }
    }

    public boolean userExistsByLogin(String login) {
        String url = "http://localhost:8082/users/existsByLogin?login=" + login;
        return restTemplate.getForObject(url, Boolean.class);
    }
    
}
