package sit.int221.oasipservice;

import io.jsonwebtoken.Jwts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.utils.JwtUtility;

@SpringBootApplication
public class OasipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OasipServiceApplication.class, args);
        System.out.println("Hello World");
    }


}
