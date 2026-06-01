package com.mestanza.servicioinstructor.unit.service;

import com.mestanza.servicioinstructor.dto.InstructorCreateDTO;
import com.mestanza.servicioinstructor.dto.InstructorDTO;
import com.mestanza.servicioinstructor.entities.Instructor;
import com.mestanza.servicioinstructor.enums.EstadoInstructor;
import com.mestanza.servicioinstructor.repositories.InstructorRepository;
import com.mestanza.servicioinstructor.services.InstructorService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

	@Mock
	private InstructorRepository instructorRepository;

	@InjectMocks
	private InstructorService instructorService;

	private Instructor instructor;
	private InstructorCreateDTO createDTO;

	@BeforeEach
	void setUp() {
		instructor = Instructor.builder()
				.id(1L)
				.nombre("Juan")
				.apellido("Pérez")
				.email("juan.perez@email.com")
				.telefono("999888777")
				.especialidad("Java")
				.fechaContratacion(LocalDate.now())
				.estado(EstadoInstructor.ACTIVO)
				.build();

		createDTO = InstructorCreateDTO.builder()
				.nombre("Juan")
				.apellido("Pérez")
				.email("juan.perez@email.com")
				.telefono("999888777")
				.especialidad("Java")
				.fechaContratacion(LocalDate.now())
				.build();
	}

	@Test
	void testFindAll() {
		Page<Instructor> page = new PageImpl<>(List.of(instructor));
		when(instructorRepository.findAll(any(PageRequest.class))).thenReturn(page);

		Page<InstructorDTO> result = instructorService.findAll(PageRequest.of(0, 10));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Juan", result.getContent().get(0).getNombre());
		verify(instructorRepository, times(1)).findAll(any(PageRequest.class));
	}

	@Test
	void testFindById_Success() {
		when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

		InstructorDTO result = instructorService.findById(1L);

		assertNotNull(result);
		assertEquals("Juan", result.getNombre());
		assertEquals("juan.perez@email.com", result.getEmail());
		verify(instructorRepository, times(1)).findById(1L);
	}

	@Test
	void testFindById_NotFound() {
		when(instructorRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> instructorService.findById(999L));
		verify(instructorRepository, times(1)).findById(999L);
	}

	@Test
	void testCreate_Success() {
		when(instructorRepository.existsByEmail("juan.perez@email.com")).thenReturn(false);
		when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

		InstructorDTO result = instructorService.create(createDTO);

		assertNotNull(result);
		assertEquals("Juan", result.getNombre());
		assertEquals(EstadoInstructor.ACTIVO, result.getEstado());
		verify(instructorRepository, times(1)).save(any(Instructor.class));
	}

	@Test
	void testCreate_DuplicateEmail() {
		when(instructorRepository.existsByEmail("juan.perez@email.com")).thenReturn(true);

		assertThrows(RuntimeException.class, () -> instructorService.create(createDTO));
		verify(instructorRepository, never()).save(any(Instructor.class));
	}

	@Test
	void testUpdate_Success() {
		when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
		when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

		InstructorCreateDTO updateDTO = InstructorCreateDTO.builder()
				.nombre("Juan Carlos")
				.apellido("Pérez")
				.email("juan.perez@email.com")
				.build();

		InstructorDTO result = instructorService.update(1L, updateDTO);

		assertNotNull(result);
		assertEquals("Juan Carlos", result.getNombre());
		verify(instructorRepository, times(1)).save(any(Instructor.class));
	}

	@Test
	void testDelete_Success() {
		when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
		when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

		instructorService.delete(1L);

		assertEquals(EstadoInstructor.INACTIVO, instructor.getEstado());
		verify(instructorRepository, times(1)).save(any(Instructor.class));
	}

	@Test
	void testFindByEspecialidad() {
		when(instructorRepository.findByEspecialidadContainingIgnoreCase("Java"))
				.thenReturn(List.of(instructor));

		List<InstructorDTO> result = instructorService.findByEspecialidad("Java");

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Java", result.get(0).getEspecialidad());
	}
}
