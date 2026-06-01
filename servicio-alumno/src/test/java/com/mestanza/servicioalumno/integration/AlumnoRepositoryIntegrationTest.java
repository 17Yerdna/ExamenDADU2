package com.mestanza.servicioalumno.integration;

import com.mestanza.servicioalumno.entities.Alumno;
import com.mestanza.servicioalumno.repositories.AlumnoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AlumnoRepositoryIntegrationTest {

	@Autowired
	private AlumnoRepository alumnoRepository;

	@Test
	void testSaveAndFindById() {
		Alumno alumno = Alumno.builder()
				.nombre("Carlos")
				.apellido("López")
				.email("carlos.lopez@email.com")
				.fechaNacimiento(LocalDate.of(1999, 5, 15))
				.estado("ACTIVO")
				.build();

		Alumno saved = alumnoRepository.save(alumno);

		assertNotNull(saved.getId());
		Optional<Alumno> found = alumnoRepository.findById(saved.getId());
		assertTrue(found.isPresent());
		assertEquals("Carlos", found.get().getNombre());
	}

	@Test
	void testFindByEmail() {
		Alumno alumno = Alumno.builder()
				.nombre("Ana")
				.apellido("Martínez")
				.email("ana.martinez@email.com")
				.build();

		alumnoRepository.save(alumno);

		Optional<Alumno> found = alumnoRepository.findByEmail("ana.martinez@email.com");
		assertTrue(found.isPresent());
		assertEquals("Ana", found.get().getNombre());
	}

	@Test
	void testExistsByEmail() {
		Alumno alumno = Alumno.builder()
				.nombre("Luis")
				.apellido("Torres")
				.email("luis.torres@email.com")
				.build();

		alumnoRepository.save(alumno);

		assertTrue(alumnoRepository.existsByEmail("luis.torres@email.com"));
		assertFalse(alumnoRepository.existsByEmail("noexiste@email.com"));
	}

	@Test
	void testFindByEstado() {
		Alumno active = Alumno.builder()
				.nombre("Activo")
				.apellido("Test")
				.email("activo@email.com")
				.estado("ACTIVO")
				.build();

		Alumno inactive = Alumno.builder()
				.nombre("Inactivo")
				.apellido("Test")
				.email("inactivo@email.com")
				.estado("INACTIVO")
				.build();

		alumnoRepository.saveAll(java.util.List.of(active, inactive));

		Page<Alumno> activeAlumnos = alumnoRepository.findByEstado("ACTIVO", PageRequest.of(0, 10));

		assertEquals(1, activeAlumnos.getTotalElements());
		assertEquals("Activo", activeAlumnos.getContent().get(0).getNombre());
	}
}
