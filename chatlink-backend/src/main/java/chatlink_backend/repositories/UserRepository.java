package chatlink_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import chatlink_backend.utils.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String>{

    @Query("{ 'login' : ?0 }")
    Optional<User> findByLogin(String login);
}