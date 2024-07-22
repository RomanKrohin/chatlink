package chatlink_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import chatlink_backend.utils.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String>{
    public Optional<User> findByLogin(String login);
}