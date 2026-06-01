package com.mestanza.serviciotaller.controllers;

import com.mestanza.serviciotaller.dto.*;
import com.mestanza.serviciotaller.enums.EstadoTaller;
import com.mestanza.serviciotaller.services.TallerService;
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
@RequestMapping("/api/talleres")
@RequiredArgsConstructor
public class TallerController {

	private final TallerService tallerService;

	@GetMapping
	public ResponseEntity<Page<TallerDTO>> findAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		return ResponseEntity.ok(tallerService.findAll(pageable));
	}

	@GetMapping("/estado/{estado}")
	public ResponseEntity<Page<TallerDTO>> findByEstado(
			@PathVariable EstadoTaller estado,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(tallerService.findByEstado(estado, pageable));
	}

	@GetMapping("/categoria/{categoria}")
	public ResponseEntity<List<TallerDTO>> findByCategoria(@PathVariable String categoria) {
		return ResponseEntity.ok(tallerService.findByCategoria(categoria));
	}

	@GetMapping("/search")
	public ResponseEntity<List<TallerDTO>> findByNombre(@RequestParam String nombre) {
		return ResponseEntity.ok(tallerService.findByNombre(nombre));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TallerDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(tallerService.findById(id));
	}

	@GetMapping("/{id}/completo")
	public ResponseEntity<TallerCompletoDTO> findByIdCompleto(@PathVariable Long id) {
		return ResponseEntity.ok(tallerService.findByIdCompleto(id));
	}

	@PostMapping
	public ResponseEntity<TallerDTO> create(@Valid @RequestBody TallerCreateDTO dto) {
		TallerDTO created = tallerService.create(dto);
		return ResponseEntity.created(URI.create("/api/talleres/" + created.getId()))
				.body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<TallerDTO> update(
			@PathVariable Long id,
			@Valid @RequestBody TallerCreateDTO dto) {
		return ResponseEntity.ok(tallerService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		tallerService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/horarios")
	public ResponseEntity<List<HorarioDTO>> obtenerHorarios(@PathVariable Long id) {
		return ResponseEntity.ok(tallerService.obtenerHorarios(id));
	}

	@PostMapping("/{id}/horarios")
	public ResponseEntity<HorarioDTO> agregarHorario(
			@PathVariable Long id,
			@Valid @RequestBody HorarioDTO horarioDTO) {
		HorarioDTO created = tallerService.agregarHorario(id, horarioDTO);
		return ResponseEntity.created(URI.create("/api/talleres/" + id + "/horarios/" + created.getId()))
				.body(created);
	}

	@GetMapping("/{id}/disponibilidad")
	public ResponseEntity<Map<String, Object>> verificarDisponibilidad(@PathVariable Long id) {
		return ResponseEntity.ok(tallerService.verificarDisponibilidad(id));
	}

	@GetMapping("/{id}/instructores")
	public ResponseEntity<List<Map<String, Object>>> obtenerInstructores(@PathVariable Long id) {
		return ResponseEntity.ok(tallerService.obtenerInstructoresPorTaller(id));
	}

	@GetMapping("/{id}/alumnos")
	public ResponseEntity<List<Map<String, Object>>> obtenerAlumnos(@PathVariable Long id) {
		return ResponseEntity.ok(tallerService.obtenerAlumnosPorTaller(id));
	}
}
