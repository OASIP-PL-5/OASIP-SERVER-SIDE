package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import sit.int221.oasipservice.models.JwtRequest;
import sit.int221.oasipservice.models.JwtResponse;
import sit.int221.oasipservice.models.RefreshToken;
import sit.int221.oasipservice.services.UserService;
import sit.int221.oasipservice.utils.JwtUtility;

@RestController
@RequestMapping("/api/refresh")
public class RefreshTokenController {

    @Autowired
    private JwtUtility jwtUtility;


    @PostMapping("")
    public ResponseEntity authenticate(@RequestBody RefreshToken refreshToken) {
//        String email = jwtUtility.getUsernameFromToken(jwtToken.getToken());
        final String token = jwtUtility.generateNewToken(refreshToken);
        final String newToken = jwtUtility.generateNewRefreshToken(refreshToken);

        return ResponseEntity.ok(new JwtResponse(token,newToken));
    }
}