package com.ged.auth.controller;

import com.ged.auth.Model.LoginDTO;
import com.ged.auth.Model.RegisterDTO;
import com.ged.auth.Model.User;
import com.ged.auth.repository.UserRepository;
import com.ged.auth.security.JwtTokenUtil;
import com.ged.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO login) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.username(), login.password())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final User user = authService.loadUserByUsername(login.username());

        final String jwt = jwtTokenUtil.generateToken(user);

        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterDTO regiter) {
        if(userRepository.findByUsername(regiter.username()) != null) {
           throw new BadCredentialsException("user already exists");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(regiter.password());
        User user = new User(regiter.username(), encryptedPassword);

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
