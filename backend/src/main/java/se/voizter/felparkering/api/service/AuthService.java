package se.voizter.felparkering.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import se.voizter.felparkering.api.dto.LoginRequest;
import se.voizter.felparkering.api.dto.RegisterRequest;
import se.voizter.felparkering.api.dto.UserDetailDto;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.enums.Role;
import se.voizter.felparkering.api.exception.exceptions.InvalidCredentialsException;
import se.voizter.felparkering.api.exception.exceptions.NotFoundException;
import se.voizter.felparkering.api.exception.exceptions.PasswordMismatchException;
import se.voizter.felparkering.api.exception.exceptions.UserConflictException;
import se.voizter.felparkering.api.model.User;
import se.voizter.felparkering.api.repository.UserRepository;
import se.voizter.felparkering.api.security.JwtProvider;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public UserDetailDto login(LoginRequest request) {
        String email = request.email();
        String password = request.password();

        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isEmpty()) {
            throw new NotFoundException(Message.USER_NOT_FOUND.toString());
        }

        User user = maybeUser.get();

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException(Message.INVALID_CREDENTIALS.toString());
        }

        return toDetailDto(getToken(user));
    }

    @Transactional
    public UserDetailDto register(RegisterRequest request) {
        String email = request.email();
        String password = request.password();

        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isPresent()) {
            throw new UserConflictException(Message.USER_EXISTS.toString());
        }

        if (!password.equals(request.confPassword())) {
            throw new PasswordMismatchException(Message.PASSWORD_MISMATCH.toString());
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        return toDetailDto(getToken(user));
    }

    private UserDetailDto toDetailDto(String token) {
        return new UserDetailDto(token);
    }

    private String getToken(User user) {
        return jwtProvider.generateToken(user.getId(), user.getRole());
    }
}
