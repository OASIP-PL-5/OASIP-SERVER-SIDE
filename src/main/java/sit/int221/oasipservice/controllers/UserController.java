package sit.int221.oasipservice.controllers;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import sit.int221.oasipservice.dtos.NewUserDTO;
import sit.int221.oasipservice.dtos.*;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.services.UserService;
import sit.int221.oasipservice.utils.JwtUtility;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    private final UserRepository repository;

//    @Autowired
//    private JwtUtility jwtUtility;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    //assign role จาก string ดิบๆ เก็บเป็น object-String เลย เพื่อความสะดวกในการเช็ค roles
    private final String admin = "admin";
    private final String lecturer = "lecturer";
    private final String student = "student";

    // Get all-users
    @GetMapping("")
    public List<UserDTO> getAllUser(HttpServletRequest request) {
        System.out.println("ส่วนการทำงารน getAllUser");
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
//ไม่ใช่ guest == มี token
        else {
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
//        เช็คว่าตรงกับ algorithm ไหน เป็นของ azure ดีกว่า
            System.out.println(tokenDecoded.getAlgorithm());
//หาก algorithm เป็น RS256 ก็เท่ากับ เป็น token จาก azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
                boolean isStd = tokenDecoded.getClaims().get("roles").toString().contains(student);
                boolean isLec = tokenDecoded.getClaims().get("roles").toString().contains(lecturer);
                boolean isAd = tokenDecoded.getClaims().get("roles").toString().contains(admin);
                if (isStd || isLec) {
                    System.out.println("are student or lecturer : " + tokenDecoded.getClaims().get("roles").toString());
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                } else if (isAd) {
                    System.out.println("this is admin");
                    return userService.getAllUserByDTO();
                }
            }
// token from oasip
            else if (tokenDecoded.getAlgorithm().contains("HS512")) {
//ทำงานตามปกติ
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                System.out.println("check role before condition" + role);
                if (role.contains("admin")) {
                    return userService.getAllUserByDTO();
                } else if (role.contains("student") || role.contains("lecturer")) {
                    System.out.println("are you student or lecturer ? : " + role);
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                }
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
    }


    //    Get user-by-id
    @GetMapping("/{id}")
    public List<UserDTO> getSimpleUserDTO(@PathVariable Integer id, HttpServletRequest request) {
        System.out.println("ส่วนการทำงาน getUserById");
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
//ไม่ใช่ guest == มี token
        else {
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
//        เช็คว่าตรงกับ algorithm ไหน เป็นของ azure ดีกว่า
            System.out.println(tokenDecoded.getAlgorithm());
//หาก algorithm เป็น RS256 ก็เท่ากับ เป็น token จาก azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
                boolean isStd = tokenDecoded.getClaims().get("roles").toString().contains(student);
                boolean isLec = tokenDecoded.getClaims().get("roles").toString().contains(lecturer);
                boolean isAd = tokenDecoded.getClaims().get("roles").toString().contains(admin);
                if (isStd || isLec) {
                    System.out.println("are student or lecturer : " + tokenDecoded.getClaims().get("roles").toString());
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                } else if (isAd) {
                    System.out.println("this is admin");
                    return userService.getUserById(id);
                }
            }
// token from oasip
            else if (tokenDecoded.getAlgorithm().contains("HS512")) {
//ทำงานตามปกติ
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                System.out.println("check role before condition" + role);
                if (role.contains("admin")) {
                    return userService.getUserById(id);
                } else if (role.contains(student) || role.contains(lecturer)) {
                    System.out.println("are you student or lecturer ? : " + role);
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                }
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
    }

    //    add new user
    @Deprecated
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createUser(@Valid @RequestBody User newUser, HttpServletRequest request) {
        System.out.println("ส่วนการทำงาน createUser");
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
//ไม่ใช่ guest == มี token
        else {
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
//        เช็คว่าตรงกับ algorithm ไหน เป็นของ azure ดีกว่า
            System.out.println(tokenDecoded.getAlgorithm());
//หาก algorithm เป็น RS256 ก็เท่ากับ เป็น token จาก azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
                boolean isStd = tokenDecoded.getClaims().get("roles").toString().contains(student);
                boolean isLec = tokenDecoded.getClaims().get("roles").toString().contains(lecturer);
                boolean isAd = tokenDecoded.getClaims().get("roles").toString().contains(admin);
                if (isStd || isLec) {
                    System.out.println("are student or lecturer : " + tokenDecoded.getClaims().get("roles").toString());
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                } else if (isAd) {
                    System.out.println("this is admin");
                    return userService.save(newUser);
                }
            }
// token from oasip
            else if (tokenDecoded.getAlgorithm().contains("HS512")) {
//ทำงานตามปกติ
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                System.out.println("check role before condition" + role);
                if (role.contains("admin")) {
                    return userService.save(newUser);
                } else if (role.contains(student) || role.contains(lecturer)) {
                    System.out.println("are you student or lecturer ? : " + role);
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                }
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
    }

    // edit user
    @PutMapping("/{id}")
    public User updateUser(@Valid @RequestBody UserDTO updateUser, @PathVariable Integer id, HttpServletRequest request) {
        System.out.println("ส่วนการทำงาน updateUser");
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
//ไม่ใช่ guest == มี token
        else {
            User updateUserDetails = repository.getById(id);
            if (updateUserDetails.getId() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user with id: " + id + " not found !");
            }

            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
//        เช็คว่าตรงกับ algorithm ไหน เป็นของ azure ดีกว่า
            System.out.println(tokenDecoded.getAlgorithm());
//หาก algorithm เป็น RS256 ก็เท่ากับ เป็น token จาก azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
                boolean isStd = tokenDecoded.getClaims().get("roles").toString().contains(student);
                boolean isLec = tokenDecoded.getClaims().get("roles").toString().contains(lecturer);
                boolean isAd = tokenDecoded.getClaims().get("roles").toString().contains(admin);
                if (isStd || isLec) {
                    System.out.println("are student or lecturer : " + tokenDecoded.getClaims().get("roles").toString());
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                } else if (isAd) {
                    System.out.println("this is admin");
                    updateUserDetails.setName(updateUser.getName());
                    updateUserDetails.setEmail(updateUser.getEmail());
                    updateUserDetails.setRole(updateUser.getRole());
                    updateUserDetails.setUpdatedOn(updateUser.getUpdatedOn());
                    return repository.saveAndFlush(updateUserDetails);
                }
            }
// token from oasip
            else if (tokenDecoded.getAlgorithm().contains("HS512")) {
//ทำงานตามปกติ
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                System.out.println("check role before condition" + role);
                if (role.contains("admin")) {
                    updateUserDetails.setName(updateUser.getName());
                    updateUserDetails.setEmail(updateUser.getEmail());
                    updateUserDetails.setRole(updateUser.getRole());
                    updateUserDetails.setUpdatedOn(updateUser.getUpdatedOn());
                    return repository.saveAndFlush(updateUserDetails);
                } else if (role.contains(student) || role.contains(lecturer)) {
                    System.out.println("are you student or lecturer ? : " + role);
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "something went wrong");
    }

    //delete-user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        System.out.println("ส่วนการทำงาน deleteUser");
        if (request.getHeader("Authorization") == null) {
            System.out.println("this is [guest] user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
        }
//ไม่ใช่ guest == มี token
        else {
            final String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            DecodedJWT tokenDecoded = JWT.decode(token);
//        เช็คว่าตรงกับ algorithm ไหน เป็นของ azure ดีกว่า
            System.out.println(tokenDecoded.getAlgorithm());
//หาก algorithm เป็น RS256 ก็เท่ากับ เป็น token จาก azure
            if (tokenDecoded.getAlgorithm().contains("RS256")) {
                System.out.println("this is token from azure");
                boolean isStd = tokenDecoded.getClaims().get("roles").toString().contains(student);
                boolean isLec = tokenDecoded.getClaims().get("roles").toString().contains(lecturer);
                boolean isAd = tokenDecoded.getClaims().get("roles").toString().contains(admin);
                if (isStd || isLec) {
                    System.out.println("are student or lecturer : " + tokenDecoded.getClaims().get("roles").toString());
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                } else if (isAd) {
                    System.out.println("this is admin");
                    repository.findById(id).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist !"));
                    repository.deleteById(id);
                }
            }
// token from oasip
            else if (tokenDecoded.getAlgorithm().contains("HS512")) {
//ทำงานตามปกติ
                String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
                System.out.println("check role before condition" + role);
                if (role.contains(admin)) {
                    repository.findById(id).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist !"));
                    repository.deleteById(id);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this page");
                }
            }
        }
    }

    @PutMapping("/change-password")
    public User forgotPassword(@Valid @RequestBody ChangeDTO changeDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();


        return userService.changePassword(email, changeDTO);
    }

    @PutMapping("/forgot")
    public User forgot(@Valid @RequestBody NewPasswordDTO newPasswordDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println("email : " + email);
//        System.out.println("newPasswordDTO : " + newPasswordDTO);
//        User updateUserDetails = repository.findByEmail(email);
//        //encrypt password with argon2 before save to database
//        if (newPasswordDTO.getPassword().length() < 8 || newPasswordDTO.getPassword().length() > 14) {
//            System.out.println("invalid number of password : " + newPasswordDTO.getPassword().length());
////            return new ResponseEntity("The password must be between 8 and 14 characters long", HttpStatus.BAD_REQUEST);
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password must be between 8 and 14 characters long");
//        }
//        System.out.println("valid number of password (8-14): " + newPasswordDTO.getPassword() + " --> (" + newPasswordDTO.getPassword().length() + ")");
//        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
//        String hash = argon2.hash(2, 16, 1, newPasswordDTO.getPassword());
//        newPasswordDTO.setPassword(hash);
//        updateUserDetails.setPassword(newPasswordDTO.getPassword());
//        return repository.saveAndFlush(updateUserDetails);

        return userService.forgotPassword(email, newPasswordDTO);
    }


    @PostMapping("/mailForgot")
    public SendMailDTO mailForgot(@RequestBody SendMailDTO SendMailDTO) {
        System.out.println("การทำงาน mailForgot()");
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        System.out.println("user role: " + role);
        //find user in users entity
        String email = SendMailDTO.getEmail();
        User getUser = repository.findByEmail(email);
        System.out.println(getUser);
        if (getUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found please try again");
        }

        userService.sendMail(SendMailDTO);
        throw new ResponseStatusException(HttpStatus.OK, "Send email complete");
    }


}