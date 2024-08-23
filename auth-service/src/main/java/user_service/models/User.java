package user_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {


    public User(String login, String password){
        this.login = login;
        this.password = password;
    }


    private String id;
    
    private String login;

    private String password;

    private Role role;

}

enum Role {
    USER,
    MODERATOR
}
