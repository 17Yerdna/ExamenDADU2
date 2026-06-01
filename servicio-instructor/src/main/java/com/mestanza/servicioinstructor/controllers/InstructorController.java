package com.mestanza.servicioinstructor.controllers;

import com.mestanza.servicioinstructor.dto.InstructorCreateDTO;
import com.mestanza.servicioinstructor.dto.InstructorDTO;
import com.mestanza.servicioinstructor.enums.EstadoInstructor;
import com.mestanza.servicioinstructor.services.InstructorService;
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

@Slf4j
@RestController
@RequestMapping("/api/instructores")
@RequiredArgsConstructor
public class InstructorController {

	private final InstructorService instructorService;

	@GetMapping
	public ResponseEntity<Page<InstructorDTO>> findAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		return ResponseEntity.ok(instructorService.findAll(pageable));
	}

	@GetMapping("/estado/{estado}")
	public ResponseEntity<Page<InstructorDTO>> findByEstado(
			@PathVariable EstadoInstructor estado,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(instructorService.findByEstado(estado, pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<InstructorDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(instructorService.findById(id));
	}

	@GetMapping("/search")
	public ResponseEntity<List<InstructorDTO>> findByEspecialidad(
			@RequestParam String especialidad) {
		return ResponseEntity.ok(instructorService.findByEspecialidad(especialidad));
	}

	@PostMapping
	public ResponseEntity<InstructorDTO> create(@Valid @RequestBody InstructorCreateDTO dto) {
		InstructorDTO created = instructorService.create(dto);
		return ResponseEntity.created(URI.create("/api/instructores/" + created.getId()))
				.body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<InstructorDTO> update(
			@PathVariable Long id,
			@Valid @RequestBody InstructorCreateDTO dto) {
		return ResponseEntity.ok(instructorService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		instructorService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
