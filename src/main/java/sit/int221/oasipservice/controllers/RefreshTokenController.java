package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int221.oasipservice.models.JwtResponse;
import sit.int221.oasipservice.models.RefreshToken;
import sit.int221.oasipservice.utils.JwtUtility;

@RestController
@RequestMapping("/api/refresh")
public class RefreshTokenController {

    @Autowired
    private JwtUtility jwtUtility;


    @PostMapping("")
    public ResponseEntity authenticate(@RequestBody RefreshToken jwtToken) {
        final String token = jwtUtility.generateRefreshToken(jwtToken.getToken());

        return ResponseEntity.ok(new JwtResponse(token));
    }
}