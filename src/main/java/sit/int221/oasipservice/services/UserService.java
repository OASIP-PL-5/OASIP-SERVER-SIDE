package sit.int221.oasipservice.services;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//import sit.int221.oasipservice.dtos.NewUserDTO;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasipservice.dtos.*;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;
import sit.int221.oasipservice.utils.JwtUtility;

import java.util.*;
import java.util.stream.Collectors;

//@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public List<UserDTO> getAllUserByDTO() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }


    public List<UserDTO> getUserById(Integer id) {
        return repository.findById(id).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }


    // service for add-user
    @Deprecated
    public ResponseEntity save(User newUser) {
        List<User> userList = repository.findAll();
        for (int i = 0; i < userList.size(); i++) {
            if (newUser.getName().equals(userList.get(i).getName())) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT);
//                throw new RuntimeException("Email already exists");
                return new ResponseEntity("This user name already exists", HttpStatus.EXPECTATION_FAILED);
            } else if (newUser.getEmail().equals(userList.get(i).getEmail())) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT);
//                throw new RuntimeException("Email already exists");
                return new ResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
            } else if (newUser.getPassword().length() < 8 || newUser.getPassword().length() > 14) {
                System.out.println("invalid number of password : " + newUser.getPassword().length());
                return new ResponseEntity("The password must be between 8 and 14 characters long", HttpStatus.BAD_REQUEST);
            }
        }
        System.out.println("valid number of password (8-14): " + newUser.getPassword() + " --> (" + newUser.getPassword().length() + ")");
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
        String hash = argon2.hash(2, 16, 1, newUser.getPassword());
        newUser.setPassword(hash); //        hash(int iterations, int memory, int parallelism, char[] password)
        repository.save(newUser);
        return new ResponseEntity("Create user successfully", HttpStatus.CREATED);
    }


    //method for email/password authentication return with http status code
    public ResponseEntity checkLogin(MatchPasswordDTO matchPasswordDTO) {
        //get user by email
        User user = repository.findByEmail(matchPasswordDTO.getEmail());
        //check if user is null, return 404
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //check password
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
        boolean isPasswordCorrect = argon2.verify(user.getPassword(), matchPasswordDTO.getPassword().toCharArray());
        //if password is correct, return 200
        if (isPasswordCorrect) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        //if password is incorrect, return 401
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


//    public User save(User user) {
//        //  วิธีนี้เก่าแล้ว เราไม่ทำ
////        User user = modelMapper.map(newUser, User.class);
////        return repository.saveAndFlush(user);
//
//// ต้องใช้ วิธีที่เรียกจาก entity เลย เพราะวิธีอื่นยังทำไม่ได้
//        user.setName(user.getName());
//        user.setEmail(user.getEmail());
//        user.setRole(user.getRole());
//// for-password
//        user.setPassword(passwordService.securePassword(user.getPassword()));
//
//        User savedUser= repository.save(user);
//        savedUser.setPassword("**********");
//        return savedUser;
//    }


    // ไว้ล่างสุด !!!
    //    serviceMethod: convert-Entity-to-DTO (ใช้งานใน serviceMethod เกี่ยวกับการ GET-users ทั้งสิ้น )
    private UserDTO convertEntityToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setCreatedOn(user.getCreatedOn());
        userDTO.setUpdatedOn(user.getUpdatedOn());
        return userDTO;
    }


    //    for login and gen jwt-token "api/login"
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return new org.springframework.security.core.userdetails.User("admin", "password", new ArrayList<>());
//
//        ทำการ findByEmail เพื่อนำ email ไปเทียบ password (ติดแค่ตรง ตอน login มันยังไม่แปลง password-argon2 เป็น raw ให้)
        User user = repository.findByEmail(username);
//        argon2 สำหรับการ verify password
//        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);


        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }

    //send mail
    public void sendMail(SendMailDTO email){
        //check user by email
//        User user = repository.findByEmail(email);
//        System.out.println("email: " + email);
        System.out.println("email: " + email.getEmail());
        // Creating a simple mail message
        SimpleMailMessage mailMessage
                = new SimpleMailMessage();

        String mail = email.getEmail();
//        System.out.println("This mail");
//        System.out.println(mail);
        // Setting up necessary details
        mailMessage.setFrom(sender);
        mailMessage.setTo(email.getEmail());
//        mailMessage.setText("https://intproj21.sit.kmutt.ac.th/pl5/reset-password?token="+jwtUtility.genToken(mail)); //<-https://intproj21.sit.kmutt.ac.th/pl5/?token="+service.gentoken() maybe
        mailMessage.setText("http://localhost:3000/reset-password?token="+jwtUtility.genToken(mail));
        mailMessage.setSubject("Change Password");

        javaMailSender.send(mailMessage);
    }

    //    public ResponseEntity newPassword(NewPasswordDTO newPasswordDTO) {
//        //get email by token
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        //get user by email
//        User user = repository.findByEmail(email);
//        //check if user is null, return 404
//        if (user == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        //check password
//        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
//        boolean isPasswordCorrect = argon2.verify(user.getPassword(), newPasswordDTO.getPassword().toCharArray());
//        //if password is correct, return 200
//        if (isPasswordCorrect) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        }
//        //if password is incorrect, return 401
//        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }
    public User forgotPassword(String email,NewPasswordDTO newPasswordDTO) {
        System.out.println("email: " + email);
        System.out.println("newPasswordDTO: " + newPasswordDTO.getPassword());
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User updateUserDetails = repository.findByEmail(email);
        //encrypt password with argon2 before save to database
        if (newPasswordDTO.getPassword().length() < 8 || newPasswordDTO.getPassword().length() > 14) {
            System.out.println("invalid number of password : " + newPasswordDTO.getPassword().length());
//            return new ResponseEntity("The password must be between 8 and 14 characters long", HttpStatus.BAD_REQUEST);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password must be between 8 and 14 characters long");
        }
        System.out.println("valid number of password (8-14): " + newPasswordDTO.getPassword() + " --> (" + newPasswordDTO.getPassword().length() + ")");
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
        String hash = argon2.hash(2, 16, 1, newPasswordDTO.getPassword());
        newPasswordDTO.setPassword(hash);
        updateUserDetails.setPassword(newPasswordDTO.getPassword());
        return repository.saveAndFlush(updateUserDetails);
    }




    public User changePassword(String email ,ChangeDTO changeDTO) {
        System.out.println("changeDTO: " + changeDTO.getPassword());
        System.out.println("changeDTO: " + changeDTO.getNewPassword());
        //get user by email
        User user = repository.findByEmail(email);
        //check if user is null, return 404
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        //check password
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
        boolean isPasswordCorrect = argon2.verify(user.getPassword(), changeDTO.getPassword().toCharArray());
        //if password is correct then change password
        if (isPasswordCorrect) {
            System.out.println(isPasswordCorrect);
            if (changeDTO.getNewPassword().length() < 8 || changeDTO.getNewPassword().length() > 14) {
                System.out.println("invalid number of password : " + changeDTO.getNewPassword().length());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password must be between 8 and 14 characters long");
            }
//            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 16, 16);
            String hash = argon2.hash(2, 16, 1, changeDTO.getNewPassword());
            changeDTO.setNewPassword(hash);
            changeDTO.setNewPassword(changeDTO.getNewPassword());
            user.setPassword(changeDTO.getNewPassword());
            return repository.saveAndFlush(user);
        }
        //if password is incorrect, return 401
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect");
    }
}