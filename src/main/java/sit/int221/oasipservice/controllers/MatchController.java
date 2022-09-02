package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int221.oasipservice.dtos.MatchPasswordDTO;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    @Autowired
    private UserService userService;


    //use post method to match password by using checkLogin method in UserService
    @PostMapping("")
    public ResponseEntity checkLogin(@Valid @RequestBody MatchPasswordDTO matchPasswordDTO) {
        return userService.checkLogin(matchPasswordDTO);
    }

}
