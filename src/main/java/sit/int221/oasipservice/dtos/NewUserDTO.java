package sit.int221.oasipservice.dtos;

import lombok.*;
import sit.int221.oasipservice.services.PasswordService;

import java.time.Instant;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Data

public class NewUserDTO {
        private String name;
        private String email;
        private String role;
        private String password;

}
