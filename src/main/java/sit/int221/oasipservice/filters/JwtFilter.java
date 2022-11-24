package sit.int221.oasipservice.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.oasipservice.services.UserService;
import sit.int221.oasipservice.utils.JwtUtility;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;


@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        System.out.println("การทำงานใน jwtfilter.java");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            jwtToken = authorizationHeader.substring(7);
            System.out.println("jwtToken : "+jwtToken);
//            username is email from token
            if (jwtToken.length() < 300){
            username = jwtUtility.getUsernameFromToken(jwtToken);
            System.out.println("username : "+username);
            }
            else if(jwtToken.length() > 300){
                System.out.println("this is token from ms-azure ::");
//                username = jwtUtility.getUsernameFromAzureToken(jwtToken);
//                System.out.println("username(from azure-token) : "+username);
                DecodedJWT jwtAzure = JWT.decode(jwtToken);
                System.out.println("jwt azure expired: "+jwtAzure.getExpiresAt());
                System.out.println("this is cliaims from jwtAzure(roles) : "+jwtAzure.getClaims().get("roles"));
                System.out.println("this is cliaims from jwtAzure(email) : "+jwtAzure.getClaims().get("preferred_username"));
                System.out.println("this is cliaims from jwtAzure(name) : "+jwtAzure.getClaims().get("name"));

            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            System.out.println("loadUser by username success : "+userDetails);
            if (jwtUtility.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        jwtUtility.getAuthentication(jwtToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                System.out.println("authentication : "+authentication);
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}