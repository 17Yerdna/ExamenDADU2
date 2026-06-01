package com.mestanza.servicioalumno.unit.service;

import com.mestanza.servicioalumno.clients.TallerClient;
import com.mestanza.servicioalumno.dto.AlumnoCreateDTO;
import com.mestanza.servicioalumno.dto.AlumnoDTO;
import com.mestanza.servicioalumno.entities.Alumno;
import com.mestanza.servicioalumno.repositories.AlumnoRepository;
import com.mestanza.servicioalumno.services.AlumnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

	@Mock
	private AlumnoRepository alumnoRepository;

	@Mock
	private TallerClient tallerClient;

	@InjectMocks
	private AlumnoService alumnoService;

	private Alumno alumno;
	private AlumnoCreateDTO createDTO;

	@BeforeEach
	void setUp() {
		alumno = Alumno.builder()
				.id(1L)
				.nombre("María")
				.apellido("García")
				.email("maria.garcia@email.com")
				.telefono("999888777")
				.fechaNacimiento(LocalDate.of(2000, 1, 1))
				.estado("ACTIVO")
				.build();

		createDTO = AlumnoCreateDTO.builder()
				.nombre("María")
				.apellido("García")
				.email("maria.garcia@email.com")
				.telefono("999888777")
				.fechaNacimiento(LocalDate.of(2000, 1, 1))
				.build();
	}

	@Test
	void testFindAll() {
		Page<Alumno> page = new PageImpl<>(List.of(alumno));
		when(alumnoRepository.findAll(any(PageRequest.class))).thenReturn(page);

		Page<AlumnoDTO> result = alumnoService.findAll(PageRequest.of(0, 10));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("María", result.getContent().get(0).getNombre());
	}

	@Test
	void testFindById_Success() {
		when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));

		AlumnoDTO result = alumnoService.findById(1L);

		assertNotNull(result);
		assertEquals("María", result.getNombre());
	}

	@Test
	void testFindById_NotFound() {
		when(alumnoRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> alumnoService.findById(999L));
	}

	@Test
	void testCreate_Success() {
		when(alumnoRepository.existsByEmail("maria.garcia@email.com")).thenReturn(false);
		when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno);

		AlumnoDTO result = alumnoService.create(createDTO);

		assertNotNull(result);
		assertEquals("María", result.getNombre());
		assertEquals("ACTIVO", result.getEstado());
	}

	@Test
	void testCreate_DuplicateEmail() {
		when(alumnoRepository.existsByEmail("maria.garcia@email.com")).thenReturn(true);

		assertThrows(RuntimeException.class, () -> alumnoService.create(createDTO));
	}

	@Test
	void testUpdate_Success() {
		when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
		when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno);

		AlumnoCreateDTO updateDTO = AlumnoCreateDTO.builder()
				.nombre("María Elena")
				.apellido("García")
				.email("maria.garcia@email.com")
				.build();

		AlumnoDTO result = alumnoService.update(1L, updateDTO);

		assertNotNull(result);
		assertEquals("María Elena", result.getNombre());
	}

	@Test
	void testDelete_Success() {
		when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
		when(alumnoRepository.save(any(Alumno.class))).thenReturn(alumno);

		alumnoService.delete(1L);

		assertEquals("INACTIVO", alumno.getEstado());
	}
}
