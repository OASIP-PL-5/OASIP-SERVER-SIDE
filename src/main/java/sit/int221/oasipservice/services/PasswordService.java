package sit.int221.oasipservice.services;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sit.int221.oasipservice.dtos.MatchPasswordDTO;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class PasswordService {
//    @Autowired
//    UserRepository userRepository;
//
//    public User matchPassword(MatchPasswordDTO matchPasswordDTO) {
//        User user = userRepository.findByEmail(matchPasswordDTO.getEmail());
//        if (user == null) {
//            return null;
//        }
//        Argon2 argon2 = Argon2Factory.create();
//        if (argon2.verify(user.getPassword(), matchPasswordDTO.getPassword())) {
//            return user;
//        }
//        return null;
//    }
//


    private PasswordConfig passwordConfig;
    private Argon2 argon2;

    public PasswordService(PasswordConfig passwordConfig) {
        this.passwordConfig = passwordConfig;
        argon2 = getArgon2Instance();
    }

    public String securePassword(String password) {
        return argon2.hash(passwordConfig.getIterations(), passwordConfig.getMemory(), passwordConfig.getParralellism(), password.toCharArray());

    }

    public boolean validatePassword(String hash, String userEnterPassword) {
        return argon2.verify(hash, userEnterPassword.toCharArray());
    }

    private Argon2 getArgon2Instance() {
        Argon2Factory.Argon2Types type = Argon2Factory.Argon2Types.ARGON2d;
        if (passwordConfig.getType() == 1) {
            type = Argon2Factory.Argon2Types.ARGON2i;
        } else if (passwordConfig.getType() == 2) {
            type = Argon2Factory.Argon2Types.ARGON2id;
        }
        return Argon2Factory.create(type, passwordConfig.getSaltLength(), passwordConfig.getHashLength());
    }
//
//    @Autowired
//    private UserRepository repository;
//
//    public ResponseEntity update(MatchPasswordDTO matchPasswordDTO) {
//        matchPasswordDTO.setEmail(matchPasswordDTO.getEmail());
//        matchPasswordDTO.setPassword(matchPasswordDTO.getPassword());
//        List<User> checkpassword = repository.findAll();
//        for (int i = 0; i < checkpassword.size(); i++) {
//            if (Objects.equals(matchPasswordDTO.getEmail(), checkpassword.get(i).getEmail())) {
//                if (Objects.equals(matchPasswordDTO.getPassword(), checkpassword.get(i).getPassword())) {
//                    return new ResponseEntity<>("success match email and password", HttpStatus.OK);
//                }
//                return new ResponseEntity<>("Yo wrong password or email LMAO XD",
//                        HttpStatus.UNAUTHORIZED);
//            }
//        }
//        return
//    }
}
