package com.mestanza.apigateway.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

	@RequestMapping("/fallback/instructor")
	public ResponseEntity<?> instructorFallback() {
		return ResponseEntity.status(503)
				.body(Map.of("error", "Servicio de instructores no disponible"));
	}

	@RequestMapping("/fallback/alumno")
	public ResponseEntity<?> alumnoFallback() {
		return ResponseEntity.status(503)
				.body(Map.of("error", "Servicio de alumnos no disponible"));
	}

	@RequestMapping("/fallback/taller")
	public ResponseEntity<?> tallerFallback() {
		return ResponseEntity.status(503)
				.body(Map.of("error", "Servicio de talleres no disponible"));
	}
}
