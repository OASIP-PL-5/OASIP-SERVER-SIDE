package sit.int221.oasipservice.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtility implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;
    private static final long JWT_TOKEN_VALIDITY = 1 * 30 * 60;
    //private static final long JWT_TOKEN_VALIDITY = 60;
    private static final long JWT_TOKEN_VALIDITY_REFRESH = 24 * 60 * 60;

    @Autowired
    private UserRepository userRepository;

    private String secret = "secret";

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        User getUser = userRepository.findByEmail(subject);
        return Jwts.builder().setSubject(subject)
                .claim("role",getUser.getRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateRefreshToken(String token) {
        String email = getUsernameFromToken(token);
        return doGenerateRefreshToken(new HashMap<>(), email);
    }
    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        User getUser = userRepository.findByEmail(subject);
        return Jwts.builder().setSubject(subject)
                .claim("role",getUser.getRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_REFRESH * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //    method for authorization by role
    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails){
        final JwtParser jwtParser = Jwts.parser().setSigningKey(secret);
        final Jws claimsJws = jwtParser.parseClaimsJws(token);
        final Claims claims = (Claims) claimsJws.getBody();
        final Collection authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }


}