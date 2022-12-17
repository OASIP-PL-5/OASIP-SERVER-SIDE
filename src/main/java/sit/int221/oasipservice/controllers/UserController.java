package sit.int221.oasipservice.controllers;


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

    @PutMapping("/change-password")
    public User forgotPassword(@Valid @RequestBody ChangeDTO changeDTO) {
        System.out.println("การทำงานที่ change-password");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();


        return userService.changePassword(email,changeDTO);
    }

    @PutMapping("/forgot")
    public User forgot(@Valid @RequestBody NewPasswordDTO newPasswordDTO) {
        System.out.println("การทำงานที่ forgot");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//
        return userService.forgotPassword(email,newPasswordDTO);
    }


    @PostMapping("/mailForgot")
    public SendMailDTO mailForgot(@RequestBody SendMailDTO SendMailDTO) {
        System.out.println("การทำงาน mailForgot()");
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("user role: "+role);
        //find user in users entity
        String email = SendMailDTO.getEmail();
        System.out.println(SendMailDTO.getEmail());
        User getUser = repository.findByEmail(email);
        System.out.println("repoFindMail "+repository.findByEmail(email));
        System.out.println("getUser"+getUser);
        if(getUser == null){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found please try again");
        }else{
            userService.sendMail(SendMailDTO);
            throw new ResponseStatusException(HttpStatus.OK, "Send email complete");
        }


    }


}