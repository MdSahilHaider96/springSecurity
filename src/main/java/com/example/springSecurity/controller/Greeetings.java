package com.example.springSecurity.controller;

import com.example.springSecurity.jwt.JwtUtils;
import com.example.springSecurity.jwt.LoginRequest;
import com.example.springSecurity.jwt.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class Greeetings {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    public AuthenticationManager authenticationManager;

    @GetMapping("/greetings")
    public String greeting() {
    return "Hello World";
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userEndPoint () {
        return "Hello User";
    }
    @GetMapping("/admin")
    public String adminEndPoint () {
        return "Hello Admin";
    }
    @PreAuthorize("hasRole('SAHIL')")
    @GetMapping("/sahil")
    public String sahilEndPoint () {
        return "Hello Sahiil";
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // retrieve the userdetails and generate the jwt
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

            LoginResponse response = new LoginResponse(userDetails.getUsername(), jwtToken, roles );

        return ResponseEntity.ok(response);
    }
}

