package chatlink_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class ChatlinkBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatlinkBackendApplication.class, args);
	}

}
