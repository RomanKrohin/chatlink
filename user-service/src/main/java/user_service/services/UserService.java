package user_service.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import user_service.models.ServiceResponse;
import user_service.models.User;
import user_service.repositories.UserRepository;
import java.util.Optional;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {




    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Logger logger;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "user-registration", groupId = "user-group")
    public void handleUserRegistration(String userJson) {
        try {
            User user = new ObjectMapper().readValue(userJson, User.class);
            logger.info("Received user: " + user);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "user-check", groupId = "user-group")
    public void userExistsByLogin(String login) throws JsonProcessingException{
        try {
            logger.info(login);
            Optional<User> user = userRepository.findByLogin(login);
            ServiceResponse serviceResponse = new ServiceResponse(user.isPresent());
            logger.info(String.valueOf(user.isPresent()));
            if (serviceResponse.isStatus()) serviceResponse.setMessage(new String[]{user.get().getLogin(), user.get().getPassword(), String.valueOf(user.get().getLogin())});
            else serviceResponse.setMessage(new String[]{login});
            kafkaTemplate.send("auth-check", objectMapper.writeValueAsString(serviceResponse));
            logger.info("Sent serviceResponse to auth-service");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

