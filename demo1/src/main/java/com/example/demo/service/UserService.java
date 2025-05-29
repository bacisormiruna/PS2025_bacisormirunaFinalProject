package com.example.demo.service;

import com.example.demo.builder.userbuilder.UserBuilder;
import com.example.demo.builder.userbuilder.UserViewBuilder;
import com.example.demo.dto.userdto.UserDTO;
import com.example.demo.dto.userdto.UserViewDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.errorhandler.UserException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.validator.UserFieldValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//aici aveam si password encoder
@Service
public class UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final JWTService jwtService;
    private AuthenticationManager authenticationManager;

    public UserService(RoleRepository roleRepository,
                       UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       JWTService jwtService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public List<UserViewDTO> findAllUserView() {
        return userRepository.findAll().stream()
                .map(UserViewBuilder::generateDTOFromEntity)
                .collect(Collectors.toList());
    }

    public UserViewDTO findUserViewById(Long id) throws UserException {
        Optional<User> user  = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserException("User not found with id field: " + id);
        }
        return UserViewBuilder.generateDTOFromEntity(user.get());
    }

    public UserViewDTO findUserViewByEmail(String email) throws UserException {
        Optional<User> user  = userRepository.findUserByEmail(email);

        if (user.isEmpty()) {
            throw new UserException("User not found with email field: " + email);
        }
        return UserViewBuilder.generateDTOFromEntity(user.get());
    }

    public Long createUser(UserDTO userDTO) throws UserException {
        List<String> errors = UserFieldValidator.validateInsertOrUpdate(userDTO);
        if(!errors.isEmpty())
        {
            throw new UserException(StringUtils.collectionToDelimitedString(errors, "\n"));
        }
        Optional<Role> role = roleRepository.findRoleByName(userDTO.getRoleName().toUpperCase());
        if (role.isEmpty()) {
            throw new UserException("Role not found with name field: " + userDTO.getRoleName().toUpperCase());
        }
        Optional<User> user = userRepository.findUserByEmail(userDTO.getEmail());
        if(user.isPresent() ){
            throw new UserException("User record does not permit duplicates for email field: " + userDTO.getEmail());
        }
        User userSave = UserBuilder.generateEntityFromDTO(userDTO, role.get());
        userSave.setPassword(encoder.encode(userSave.getPassword()));
        return userRepository.save(userSave).getId();
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    public Long updateUser(UserDTO userDTO) throws UserException {
        List<String> errors = UserFieldValidator.validateInsertOrUpdate(userDTO);
        if(!errors.isEmpty())
        {
            throw new UserException(StringUtils.collectionToDelimitedString(errors, "\n"));
        }
        Optional<Role> role = roleRepository.findRoleByName(userDTO.getRoleName().toUpperCase());
        if (role.isEmpty()) {
            throw new UserException("Role not found with name field: " + userDTO.getRoleName().toUpperCase());
        }
        Optional<User> user = userRepository.findById(userDTO.getId());
        if(user.isEmpty()){
            throw new UserException("User not found with id field: " + userDTO.getId());
        }
        if(!user.get().getEmail().equals(userDTO.getEmail()))
        {
            Optional<User> verifyDuplicated = userRepository.findUserByEmail(userDTO.getEmail());
            if(verifyDuplicated.isPresent() ){
                throw new UserException("User record does not permit duplicates for email field: " + userDTO.getEmail());
            }
        }
        user.get().setName(userDTO.getName());
        user.get().setEmail(userDTO.getEmail());
        user.get().setPassword(userDTO.getPassword());
        user.get().setRole(role.get());

        return userRepository.save(user.get()).getId();
    }

    public void deleteUser(Long id) throws UserException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserException("User not found with id field: " + id);
        }
        this.userRepository.deleteById(id);
    }

    public List<UserViewDTO> findUserViewByRoleName(String roleName) throws UserException {
        List<User> userList  = userRepository.findUserByRoleName(roleName);

        if (userList.isEmpty()) {
            throw new UserException("User not found with role name field: " + roleName);
        }
        return  userList.stream()
                .map(UserViewBuilder::generateDTOFromEntity)
                .collect(Collectors.toList());
    }

    public String verify(UserDTO loginDTO) throws UserException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getName(), loginDTO.getPassword())
        );
        User user = userRepository.findByName(loginDTO.getName());
        if (user == null) {
            throw new UserException("User not found");
        }
        boolean en = encoder.matches(loginDTO.getPassword(), user.getPassword());
        if (!en) {
            throw new UserException("Login failed. Invalid credentials.");
        }
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .build();
        return jwtService.generateToken(userDTO);
    }


    public String verify2(UserDTO userDTO) throws UserException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getName(), userDTO.getPassword())
        );
        if (!authentication.isAuthenticated()) {
            throw new UserException("Login failed. Invalid credentials.");
        }
        User user = userRepository.findByName(userDTO.getName());
        if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new UserException("Access denied. Only admins can log in.");
        }
        return jwtService.generateToken(userDTO);
    }

    public void changeUserRole(Long id, String roleName) throws UserException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(() -> new UserException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public UserViewDTO getUserByToken(String token) throws UserException {
        Long userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        return UserViewDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .timeStamp(user.getTimeStamp().toString())
                .build();
    }

    public String getUserRoleById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRole().getName();
    }
}
