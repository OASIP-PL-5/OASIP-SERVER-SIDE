package sit.int221.oasipservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.oasipservice.dtos.EventDTO;
//import sit.int221.oasipservice.dtos.NewUserDTO;
import sit.int221.oasipservice.dtos.UserDTO;
import sit.int221.oasipservice.entities.Event;
import sit.int221.oasipservice.entities.User;
import sit.int221.oasipservice.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    public List<UserDTO> getAllUserByDTO() {
        return repository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    public List<UserDTO> getUserById(Integer id) {
        return repository.findById(id).stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

// service for add-user
    public User save(UserDTO newUser){
        User user = modelMapper.map(newUser, User.class);
        return repository.saveAndFlush(user);
    }

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

}
