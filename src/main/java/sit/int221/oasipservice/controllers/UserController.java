package sit.int221.oasipservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import sit.int221.oasipservice.dtos.NewUserDTO;
import sit.int221.oasipservice.EnumRole;
import sit.int221.oasipservice.dtos.UserDTO;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    // Get all-users
    @GetMapping("")
    public List<UserDTO> getAllUser() {
        return userService.getAllUserByDTO();
    }

    //    Get user-by-id
    @GetMapping("/{id}")
    public List<UserDTO> getSimpleUserDTO(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    //    add new user
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody UserDTO newUser) {
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
        repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist !"));
        repository.deleteById(id);
    }
}
