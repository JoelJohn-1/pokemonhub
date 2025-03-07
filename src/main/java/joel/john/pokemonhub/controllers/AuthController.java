package joel.john.pokemonhub.controllers;

import joel.john.pokemonhub.models.User;
import joel.john.pokemonhub.repositories.UserRepository;
import joel.john.pokemonhub.security.JWTUtility;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JWTUtility jwtUtility;
    private PasswordEncoder encoder;
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, JWTUtility jwtUtility, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtility = jwtUtility;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(jwtUtility.generateToken(userDetails.getUsername()), HttpStatus.OK);
    }


    @PostMapping("/signup")
    public ResponseEntity<String> singup(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        }
        
        try {
            User newUser = new User(
                    null,
                    user.getUsername(),
                    encoder.encode(user.getPassword())
            );
            userRepository.save(newUser);
            return new ResponseEntity<>("Successful signup!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
