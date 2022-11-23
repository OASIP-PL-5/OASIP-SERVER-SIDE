package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.UserService;

@RestController
@RequestMapping("/api")
public class PasswordController {
    @Autowired
    private UserService userService;
    private final UserRepository repository;
    public PasswordController(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private AuthenticationManager authenticationManager;


}
