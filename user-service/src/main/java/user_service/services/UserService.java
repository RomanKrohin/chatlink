package user_service.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import user_service.models.User;
import user_service.repositories.UserRepository;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger("pivo");
    
    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "user-registration", groupId = "user-group")
    public void handleUserRegistration(String userJson) {
        logger.info(userJson);
        try {
            User user = new ObjectMapper().readValue(userJson, User.class);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean userExistsByLogin(String login) {
        return userRepository.findByLogin(login).isPresent();
    }
}

