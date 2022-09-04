package sit.int221.oasipservice.services;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sit.int221.oasipservice.dtos.EventDTO;
//import sit.int221.oasipservice.dtos.NewUserDTO;
import sit.int221.oasipservice.dtos.MatchPasswordDTO;
import sit.int221.oasipservice.dtos.NewUserDTO;
import sit.int221.oasipservice.dtos.UserDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private PasswordService passwordService;

    public List<UserDTO> getAllUserByDTO() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    public List<UserDTO> getUserById(Integer id) {
        return repository.findById(id).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    // service for add-user
    @Deprecated
    public User save(User newUser) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
        String hash = argon2.hash(2, 16, 1, newUser.getPassword());
//        hash(int iterations, int memory, int parallelism, char[] password)
        newUser.setPassword(hash);
        return repository.save(newUser);
    }

    //method for email/password authentication return with http status code
    public ResponseEntity checkLogin(MatchPasswordDTO matchPasswordDTO) {
        //get user by email
        User user = repository.findByEmail(matchPasswordDTO.getEmail());
        //check if user is null, return 404
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //check password
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
        boolean isPasswordCorrect = argon2.verify(user.getPassword(), matchPasswordDTO.getPassword().toCharArray());
        //if password is correct, return 200
        if (isPasswordCorrect) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        //if password is incorrect, return 401
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


//    public User save(User user) {
//        //  วิธีนี้เก่าแล้ว เราไม่ทำ
////        User user = modelMapper.map(newUser, User.class);
////        return repository.saveAndFlush(user);
//
//// ต้องใช้ วิธีที่เรียกจาก entity เลย เพราะวิธีอื่นยังทำไม่ได้
//        user.setName(user.getName());
//        user.setEmail(user.getEmail());
//        user.setRole(user.getRole());
//// for-password
//        user.setPassword(passwordService.securePassword(user.getPassword()));
//
//        User savedUser= repository.save(user);
//        savedUser.setPassword("**********");
//        return savedUser;
//    }


    // ไว้ล่างสุด !!!
    //    serviceMethod: convert-Entity-to-DTO (ใช้งานใน serviceMethod เกี่ยวกับการ GET-users ทั้งสิ้น )
    private UserDTO convertEntityToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setCreatedOn(user.getCreatedOn());
        userDTO.setUpdatedOn(user.getUpdatedOn());
        return userDTO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Use email as username
        User user = repository.findByEmail(email);
        // If user with entered email not found, throw exception
        if (user == null) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());

//        User user = repository.findByEmail(email);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());

    }
}