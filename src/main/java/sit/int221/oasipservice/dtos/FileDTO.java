package sit.int221.oasipservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
//    private String id;
    private String fileName;
    private String fileType;
    private byte[] data;
}
