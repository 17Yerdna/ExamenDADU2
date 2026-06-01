package com.mestanza.servicioalumno.controllers;

import com.mestanza.servicioalumno.dto.AlumnoCreateDTO;
import com.mestanza.servicioalumno.dto.AlumnoDTO;
import com.mestanza.servicioalumno.dto.InscripcionDTO;
import com.mestanza.servicioalumno.services.AlumnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

	private final AlumnoService alumnoService;

	@GetMapping
	public ResponseEntity<Page<AlumnoDTO>> findAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		return ResponseEntity.ok(alumnoService.findAll(pageable));
	}

	@GetMapping("/estado/{estado}")
	public ResponseEntity<Page<AlumnoDTO>> findByEstado(
			@PathVariable String estado,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(alumnoService.findByEstado(estado, pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AlumnoDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(alumnoService.findById(id));
	}

	@PostMapping
	public ResponseEntity<AlumnoDTO> create(@Valid @RequestBody AlumnoCreateDTO dto) {
		AlumnoDTO created = alumnoService.create(dto);
		return ResponseEntity.created(URI.create("/api/alumnos/" + created.getId()))
				.body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<AlumnoDTO> update(
			@PathVariable Long id,
			@Valid @RequestBody AlumnoCreateDTO dto) {
		return ResponseEntity.ok(alumnoService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		alumnoService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/inscripciones")
	public ResponseEntity<InscripcionDTO> inscribirEnTaller(
			@PathVariable Long id,
			@RequestParam Long tallerId) {
		InscripcionDTO inscripcion = alumnoService.inscribirEnTaller(id, tallerId);
		return ResponseEntity.created(URI.create("/api/alumnos/" + id + "/inscripciones/" + inscripcion.getId()))
				.body(inscripcion);
	}

	@GetMapping("/{id}/inscripciones")
	public ResponseEntity<List<InscripcionDTO>> obtenerInscripciones(@PathVariable Long id) {
		return ResponseEntity.ok(alumnoService.obtenerInscripciones(id));
	}

	@GetMapping("/{id}/talleres")
	public ResponseEntity<List<Map<String, Object>>> obtenerTalleres(@PathVariable Long id) {
		return ResponseEntity.ok(alumnoService.obtenerTalleresPorAlumno(id));
	}
}
