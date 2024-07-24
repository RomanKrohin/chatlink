package chatlink_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import chatlink_backend.repositories.UserRepository;
import chatlink_backend.security.JwtUtil;
import chatlink_backend.utils.User;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    public String authenticate(String login, String password) {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return jwtUtil.generateToken(login);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public String registerUser(User user) {
        try {
            Optional<User> user_check = userRepository.findByLogin(user.getLogin());
            if (user_check.isPresent()) throw new DataIntegrityViolationException(null);
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user = userRepository.save(user);
            return jwtUtil.generateToken(user.getLogin());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("User with login " + user.getLogin() + " already exists");
        }
    }
    
}
