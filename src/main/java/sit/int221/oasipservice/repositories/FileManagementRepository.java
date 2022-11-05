package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.oasipservice.entities.FilesManagement;

public interface FileManagementRepository extends JpaRepository <FilesManagement,Integer> {
}
