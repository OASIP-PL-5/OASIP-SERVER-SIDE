//package sit.int221.oasipservice.filters;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import sit.int221.oasipservice.entities.User;
//import sit.int221.oasipservice.services.UserService;
//import sit.int221.oasipservice.utils.JwtUtility;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//
//@Component
////public class JwtFilter extends OncePerRequestFilter {
//public class JwtFilter extends OncePerRequestFilter {
//    private AuthenticationManager authenticationManager;
//
//    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//
//
//        setFilterProcessesUrl("/api/login");
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
//        try {
//            User creds = new ObjectMapper().readValue(req.getInputStream(), User.class);
//            return new UsernamePasswordAuthenticationToken(
//                    creds.getEmail(),
//                    creds.getPassword(),
//                    new ArrayList<>()
//            );
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        @Override
//        protected void successfulAuthentication(HttpServletRequest req,
//            HttpServletResponse res, FilterChain chain,Authentication auth) throws IOException {
//            String token = JWT.create()
//                    .withSubject(((User) auth.getPrincipal()).getUsername())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                    .sign(Algorithm.HMAC512(SECRET.getBytes()));
//
//            String body = ((User) auth.getPrincipal()).getUsername() + " " + token;
//
//            res.getWriter().write(body);
//            res.getWriter().flush();
//        }
//    }
//
//
////    @Autowired
////    private JwtUtility jwtUtility;
////
////    @Autowired
////    private UserService userService;
////
////    @Override
////    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
////            throws ServletException, IOException {
////        final String authorizationHeader = request.getHeader("Authorization");
////        String jwtToken = null;
////        String email = null;
////        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
////            jwtToken = authorizationHeader.substring(7);
////            email = jwtUtility.getUsernameFromToken(jwtToken);
////        }
////        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
////            UserDetails userDetails = userService.loadUserByUsername(email);
////            if (jwtUtility.validateToken(jwtToken, userDetails)) {
////                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
////                        userDetails, null, userDetails.getAuthorities());
////                usernamePasswordAuthenticationToken
////                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
////            }
////        }
////        filterChain.doFilter(request, response);
////    }
//
////}
//
