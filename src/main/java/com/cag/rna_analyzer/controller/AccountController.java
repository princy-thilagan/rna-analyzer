package com.cag.rna_analyzer.controller;

import com.cag.rna_analyzer.dao.UserDao;
import com.cag.rna_analyzer.model.LoginDto;
import com.cag.rna_analyzer.model.RegisterDto;
import com.cag.rna_analyzer.model.User;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("api/v1/account")
public class AccountController {
    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;
    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    @Autowired
    private UserDao userRepo;
    @Autowired
    private AuthenticationManager authManager;

    @GetMapping(path = "/profile")
    public ResponseEntity<Object> profile(Authentication auth){
        var response = new HashMap<String, Object>();
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());

        User user = userRepo.findByUsername(auth.getName());
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterDto userDetails) {
        User user = new User();
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        user.setCreatedTime(Instant.now().getEpochSecond());
        user.setPassword(new BCryptPasswordEncoder().encode(userDetails.getPassword()));

        try {
            User existingUser = userRepo.findByUsername(userDetails.getUsername());
            if (existingUser != null) {
                return ResponseEntity.badRequest().body("Username not available");
            }
            existingUser = userRepo.findByEmail(userDetails.getEmail());
            if (existingUser != null) {
                return ResponseEntity.badRequest().body("Your email is already registered with us. Please sign in to your account");
            }

            userRepo.save(user);

            String jwtToken = createJwtToken(user);
            var response = new HashMap<String, Object>();
            response.put("token", jwtToken);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            //Log
        }
        return ResponseEntity.internalServerError().body(null);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto userCredentials) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentials.getUsername(),
                            userCredentials.getPassword()
                    )
            );

            User user = userRepo.findByUsername(userCredentials.getUsername());
            String jwtToken = createJwtToken(user);
            var response = new HashMap<String, Object>();
            response.put("token", jwtToken);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            //log
        }
        return ResponseEntity.internalServerError().body(null);
    }

    private String createJwtToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(24 * 3600))
                .subject(user.getUsername())
                .build();

        var encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(jwtSecretKey.getBytes())
        );
        var params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), jwtClaimsSet
        );
        return encoder.encode(params).getTokenValue();
    }
}