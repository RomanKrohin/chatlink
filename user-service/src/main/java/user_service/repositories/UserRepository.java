package user_service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import user_service.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String>{

    @Query("{ 'login' : ?0 }")
    Optional<User> findByLogin(String login);

    @Query("{ 'login' : ?0, 'password' : ?1 }")
    Optional<User> findByLoginAndPassword(String login, String password);

}
