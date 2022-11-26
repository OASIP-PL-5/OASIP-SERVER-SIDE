package sit.int221.oasipservice.controllers;


import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import sit.int221.oasipservice.dtos.NewUserDTO;
import sit.int221.oasipservice.dtos.*;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.UserService;
import sit.int221.oasipservice.utils.JwtUtility;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    private final UserRepository repository;

//    @Autowired
//    private JwtUtility jwtUtility;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    // Get all-users
    @GetMapping("")
    public List<UserDTO> getAllUser() {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("check role before condition" + role);
        if (role.contains("admin")) {
            return userService.getAllUserByDTO();
        } else if (role.contains("student") || role.contains("lecturer")) {
            System.out.println("are you student or lecturer ? : " + role);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
    }


    //    Get user-by-id
    @GetMapping("/{id}")
    public List<UserDTO> getSimpleUserDTO(@PathVariable Integer id) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("check role before condition" + role);
        if (role.contains("admin")) {
            return userService.getUserById(id);
        } else if (role.contains("student") || role.contains("lecturer")) {
            System.out.println("are you student or lecturer ? : " + role);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
    }

    //    add new user
    @Deprecated
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createUser(@Valid @RequestBody User newUser) {

        return userService.save(newUser);
    }

    // edit user
    @PutMapping("/{id}")
    public User updateUser(@Valid @RequestBody UserDTO updateUser, @PathVariable Integer id) {
        User updateUserDetails = repository.getById(id);
//       updateUserDetails.setId(updateUser.getId());
        updateUserDetails.setName(updateUser.getName());
        updateUserDetails.setEmail(updateUser.getEmail());
        updateUserDetails.setRole(updateUser.getRole());
        updateUserDetails.setUpdatedOn(updateUser.getUpdatedOn());
        return repository.saveAndFlush(updateUserDetails);
    }

    //delete-user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("check role before condition" + role);
        if (role.contains("admin")) {
            repository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist !"));
            repository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
    }

//    check-login user
//    @PostMapping("")
//    @ResponseStatus(HttpStatus.FOUND)

    //    check-login user
//    @PostMapping("/login")
//    @ResponseStatus(HttpStatus.FOUND)
//    public UserDTO login(@Valid @RequestBody MatchPasswordDTO matchPassword) {
//        return userService.checkLogin(matchPassword);
//    }

    //use post method to match password by using checkLogin method in UserService
//    @PostMapping("")
//    public ResponseEntity checkLogin(@Valid @RequestBody MatchPasswordDTO matchPasswordDTO) {
//        return userService.checkLogin(matchPasswordDTO);
//    }

    //use post to send email to change password
    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@Valid @RequestBody SendMailDTO email) {
        System.out.println(email);
        userService.sendMail(email);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/forgot")
    public User forgotPassword(@Valid @RequestBody NewPasswordDTO newPasswordDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User updateUserDetails = repository.findByEmail(email);
        //encrypt password with argon2 before save to database
        if (newPasswordDTO.getPassword().length() < 8 || newPasswordDTO.getPassword().length() > 14) {
            System.out.println("invalid number of password : " + newPasswordDTO.getPassword().length());
//            return new ResponseEntity("The password must be between 8 and 14 characters long", HttpStatus.BAD_REQUEST);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password must be between 8 and 14 characters long");
        }
        System.out.println("valid number of password (8-14): " + newPasswordDTO.getPassword() + " --> (" + newPasswordDTO.getPassword().length() + ")");
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
        String hash = argon2.hash(2, 16, 1, newPasswordDTO.getPassword());
        newPasswordDTO.setPassword(hash);
        updateUserDetails.setPassword(newPasswordDTO.getPassword());
        return repository.saveAndFlush(updateUserDetails);
    }

}