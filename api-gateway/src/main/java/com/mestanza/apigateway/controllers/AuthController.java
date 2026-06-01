package com.mestanza.apigateway.controllers;

import com.mestanza.apigateway.dto.LoginRequest;
import com.mestanza.apigateway.dto.LoginResponse;
import com.mestanza.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtil jwtUtil;

	@Value("${jwt.expiration:900000}")
	private Long expiration;

	private static final Map<String, String> USERS = Map.of(
			"admin", "admin123",
			"instructor", "instructor123",
			"alumno", "alumno123"
	);

	private static final Map<String, String> ROLES = Map.of(
			"admin", "ADMIN",
			"instructor", "INSTRUCTOR",
			"alumno", "ALUMNO"
	);

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		log.info("Intento de login para usuario: {}", request.getUsername());

		String storedPassword = USERS.get(request.getUsername());

		if (storedPassword == null || !storedPassword.equals(request.getPassword())) {
			log.warn("Credenciales inválidas para usuario: {}", request.getUsername());
			return ResponseEntity.status(401)
					.body(Map.of("error", "Credenciales inválidas"));
		}

		String role = ROLES.get(request.getUsername());
		String token = jwtUtil.generateToken(request.getUsername(), role);

		log.info("Login exitoso para usuario: {} con rol: {}", request.getUsername(), role);

		return ResponseEntity.ok(LoginResponse.builder()
				.token(token)
				.username(request.getUsername())
				.role(role)
				.expiresIn(expiration)
				.build());
	}

	@GetMapping("/health")
	public ResponseEntity<?> health() {
		return ResponseEntity.ok(Map.of("status", "UP"));
	}
}
