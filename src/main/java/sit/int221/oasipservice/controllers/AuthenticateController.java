//package sit.int221.oasipservice.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import sit.int221.oasipservice.services.UserService;
//
//@RestController
//@RequestMapping("/api/login")
//public class AuthenticateController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private JwtUtility jwtUtility;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @PostMapping("")
//    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            jwtRequest.getUsername(),
//                            jwtRequest.getPassword()
//                    )
//            );
//        } catch (BadCredentialsException e) {
//            throw new Exception("Invalid credentials", e);
//        }
//        final UserDetails userDetail = userService.loadUserByUsername(jwtRequest.getUsername());
//
//        final String token = jwtUtility.generateToken(userDetail);
//
//        return new JwtResponse(token);
//    }
//
//
//}