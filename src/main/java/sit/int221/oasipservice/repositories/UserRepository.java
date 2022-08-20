package sit.int221.oasipservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasipservice.entities.User;

import javax.transaction.Transactional;
import java.time.Instant;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Modifying
    @Query(
            value = "INSERT INTO user(name,email,role) values(:n,:e,:r)", nativeQuery = true
    )
    @Transactional
    void addUser(@Param("n") String name, @Param("e") String email, @Param("r") String role);

}
