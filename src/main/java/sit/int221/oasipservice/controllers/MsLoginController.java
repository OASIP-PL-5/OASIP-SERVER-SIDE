// package sit.int221.oasipservice.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import org.springframework.security.oauth2.core.oidc.user.OidcUser;
// import sit.int221.oasipservice.models.JwtResponse;
// import sit.int221.oasipservice.utils.JwtUtility;

// import java.util.Map;

// @RestController
// @RequestMapping("/api/mslogin")
// public class MsLoginController {
//     @Autowired
//     private JwtUtility jwtUtility;

//     @GetMapping("")
//     public ResponseEntity tokenDetails(@AuthenticationPrincipal OidcUser principal) {
//         Map<String, Object> claims = principal.getIdToken().getClaims();
//         String email = (String) claims.get("email");
//         //call services to generate token and refresh token
//         String token = jwtUtility.generateTokenMs(claims,email);
//         String refreshToken = jwtUtility.generateReTokenMs(claims,email);
//         return ResponseEntity.ok(new JwtResponse(token, refreshToken));
//     }
// }