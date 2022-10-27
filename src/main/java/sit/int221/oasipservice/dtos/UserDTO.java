package sit.int221.oasipservice.dtos;

import lombok.*;
import sit.int221.oasipservice.EnumRole;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data

public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String role;
//    private OffsetDateTime createdOn;
//    private OffsetDateTime updatedOn;
    private Instant createdOn;
    private Instant updatedOn;
}
