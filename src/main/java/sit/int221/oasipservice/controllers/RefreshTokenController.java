package sit.int221.oasipservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int221.oasipservice.models.JwtRequest;
import sit.int221.oasipservice.services.UserService;
import sit.int221.oasipservice.utils.JwtUtility;

@RestController
@RequestMapping("/api/refresh")
public class RefreshTokenController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private AuthenticationManager authenticationManager;

    //GET mapping to get refresh token
//    @GetMapping("")
//    public ResponseEntity refreshToken(@RequestBody JwtRequest jwtRequestz)throws Exception{
//       //check if token is valid
//         if(jwtUtility.validateToken(jwtRequestz.getRefreshToken())){
//              //get user detail
//              final UserDetails userDetail = userService.loadUserByUsername(jwtRequestz.getEmail());
//              //generate new access token
//              final String accessToken = jwtUtility.generateToken(userDetail);
//              //return new access token
//              return ResponseEntity.ok(new JwtResponse(accessToken));
//         }
//         else{
//              return ResponseEntity.badRequest().build();
//         }
//
//
//    }
//


}
