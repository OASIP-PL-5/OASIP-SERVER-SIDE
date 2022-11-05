package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.oasipservice.entities.File;

public interface FileRepository extends JpaRepository<File, String> {

}

