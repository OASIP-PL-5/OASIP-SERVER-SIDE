package sit.int221.oasipservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import sit.int221.oasipservice.dtos.NewUserDTO;
import sit.int221.oasipservice.dtos.MatchPasswordDTO;
import sit.int221.oasipservice.dtos.UserDTO;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.UserService;
import sit.int221.oasipservice.utils.JwtUtility;

import javax.annotation.security.RolesAllowed;
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
    public List<UserDTO> getAllUser() throws Exception {


        return userService.getAllUserByDTO();
    }


    //    Get user-by-id
    @GetMapping("/{id}")
    public List<UserDTO> getSimpleUserDTO(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    //    add new user
    @Deprecated
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User newUser) {
        return userService.save(newUser);
    }

    // edit user
    @PreAuthorize("hasRole('admin')")
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
        repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist !"));
        repository.deleteById(id);
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


}