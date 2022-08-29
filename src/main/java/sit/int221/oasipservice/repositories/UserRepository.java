package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasipservice.entities.User;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(
            value = "SELECT * FROM user where email = :e", nativeQuery = true
    )
    User findByEmail(@Param("e") String email);




}
