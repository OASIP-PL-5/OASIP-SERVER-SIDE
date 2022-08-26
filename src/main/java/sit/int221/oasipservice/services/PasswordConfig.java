package sit.int221.oasipservice.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "password.hashing.algo.argon2")
@Getter @Setter
public class  PasswordConfig {
    private int type;
    private int hashLength;
    private int saltLength;
    private int iterations;
    private int memory;
    private int parralellism;

}
