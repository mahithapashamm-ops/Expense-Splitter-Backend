package ExpenseSplitter.service;

import ExpenseSplitter.dto.AuthResponse;
import ExpenseSplitter.dto.LoginRequest;
import ExpenseSplitter.dto.RegisterRequest;
import ExpenseSplitter.model.User;
import ExpenseSplitter.repository.UserRepository;
import ExpenseSplitter.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
            token, user.getId(), user.getName(), user.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(
            request.email().trim().toLowerCase()
        ).orElseThrow(() ->
            new RuntimeException("Invalid email or password")
        );

        if (!passwordEncoder.matches(
                request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
            token, user.getId(), user.getName(), user.getEmail()
        );
    }
}
