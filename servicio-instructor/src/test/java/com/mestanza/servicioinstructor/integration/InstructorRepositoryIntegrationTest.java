package com.mestanza.servicioinstructor.integration;

import com.mestanza.servicioinstructor.entities.Instructor;
import com.mestanza.servicioinstructor.enums.EstadoInstructor;
import com.mestanza.servicioinstructor.repositories.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InstructorRepositoryIntegrationTest {

	@Autowired
	private InstructorRepository instructorRepository;

	@Test
	void testSaveAndFindById() {
		Instructor instructor = Instructor.builder()
				.nombre("María")
				.apellido("García")
				.email("maria.garcia@email.com")
				.especialidad("Python")
				.fechaContratacion(LocalDate.now())
				.estado(EstadoInstructor.ACTIVO)
				.build();

		Instructor saved = instructorRepository.save(instructor);

		assertNotNull(saved.getId());
		Optional<Instructor> found = instructorRepository.findById(saved.getId());
		assertTrue(found.isPresent());
		assertEquals("María", found.get().getNombre());
		assertEquals("maria.garcia@email.com", found.get().getEmail());
	}

	@Test
	void testFindByEmail() {
		Instructor instructor = Instructor.builder()
				.nombre("Carlos")
				.apellido("López")
				.email("carlos.lopez@email.com")
				.especialidad("Spring Boot")
				.build();

		instructorRepository.save(instructor);

		Optional<Instructor> found = instructorRepository.findByEmail("carlos.lopez@email.com");
		assertTrue(found.isPresent());
		assertEquals("Carlos", found.get().getNombre());
	}

	@Test
	void testExistsByEmail() {
		Instructor instructor = Instructor.builder()
				.nombre("Ana")
				.apellido("Martínez")
				.email("ana.martinez@email.com")
				.build();

		instructorRepository.save(instructor);

		assertTrue(instructorRepository.existsByEmail("ana.martinez@email.com"));
		assertFalse(instructorRepository.existsByEmail("noexiste@email.com"));
	}

	@Test
	void testFindByEspecialidadContainingIgnoreCase() {
		Instructor instructor1 = Instructor.builder()
				.nombre("Pedro")
				.apellido("Sánchez")
				.email("pedro@email.com")
				.especialidad("Java Spring")
				.build();

		Instructor instructor2 = Instructor.builder()
				.nombre("Luis")
				.apellido("Torres")
				.email("luis@email.com")
				.especialidad("Java EE")
				.build();

		instructorRepository.saveAll(List.of(instructor1, instructor2));

		List<Instructor> found = instructorRepository.findByEspecialidadContainingIgnoreCase("java");
		assertEquals(2, found.size());
	}

	@Test
	void testFindByEstado() {
		Instructor active = Instructor.builder()
				.nombre("Activo")
				.apellido("Test")
				.email("activo@email.com")
				.estado(EstadoInstructor.ACTIVO)
				.build();

		Instructor inactive = Instructor.builder()
				.nombre("Inactivo")
				.apellido("Test")
				.email("inactivo@email.com")
				.estado(EstadoInstructor.INACTIVO)
				.build();

		instructorRepository.saveAll(List.of(active, inactive));

		Page<Instructor> activeInstructors = instructorRepository.findByEstado(
				EstadoInstructor.ACTIVO, PageRequest.of(0, 10));

		assertEquals(1, activeInstructors.getTotalElements());
		assertEquals("Activo", activeInstructors.getContent().get(0).getNombre());
	}

	@Test
	void testDelete() {
		Instructor instructor = Instructor.builder()
				.nombre("Eliminar")
				.apellido("Test")
				.email("eliminar@email.com")
				.build();

		Instructor saved = instructorRepository.save(instructor);
		assertNotNull(saved.getId());

		instructorRepository.delete(saved);

		Optional<Instructor> found = instructorRepository.findById(saved.getId());
		assertFalse(found.isPresent());
	}
}
