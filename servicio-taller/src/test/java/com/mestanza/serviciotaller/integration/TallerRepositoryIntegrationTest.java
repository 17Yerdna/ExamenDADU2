package com.mestanza.serviciotaller.integration;

import com.mestanza.serviciotaller.entities.Taller;
import com.mestanza.serviciotaller.enums.EstadoTaller;
import com.mestanza.serviciotaller.repositories.TallerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TallerRepositoryIntegrationTest {

	@Autowired
	private TallerRepository tallerRepository;

	@Test
	void testSaveAndFindById() {
		Taller taller = Taller.builder()
				.nombre("Python Básico")
				.descripcion("Curso introductorio de Python")
				.categoria("Programación")
				.duracionHoras(20)
				.cupoMaximo(25)
				.costo(new BigDecimal("300.00"))
				.estado(EstadoTaller.ACTIVO)
				.build();

		Taller saved = tallerRepository.save(taller);

		assertNotNull(saved.getId());
		Optional<Taller> found = tallerRepository.findById(saved.getId());
		assertTrue(found.isPresent());
		assertEquals("Python Básico", found.get().getNombre());
	}

	@Test
	void testFindByCategoria() {
		Taller taller1 = Taller.builder()
				.nombre("Java Básico")
				.categoria("Programación")
				.cupoMaximo(30)
				.build();

		Taller taller2 = Taller.builder()
				.nombre("Java Avanzado")
				.categoria("Programación")
				.cupoMaximo(20)
				.build();

		tallerRepository.saveAll(List.of(taller1, taller2));

		List<Taller> found = tallerRepository.findByCategoriaContainingIgnoreCase("programación");
		assertEquals(2, found.size());
	}

	@Test
	void testFindByNombre() {
		Taller taller = Taller.builder()
				.nombre("Spring Boot")
				.categoria("Framework")
				.cupoMaximo(25)
				.build();

		tallerRepository.save(taller);

		List<Taller> found = tallerRepository.findByNombreContainingIgnoreCase("spring");
		assertEquals(1, found.size());
		assertEquals("Spring Boot", found.get(0).getNombre());
	}

	@Test
	void testFindByEstado() {
		Taller active = Taller.builder()
				.nombre("Activo")
				.cupoMaximo(30)
				.estado(EstadoTaller.ACTIVO)
				.build();

		Taller inactive = Taller.builder()
				.nombre("Inactivo")
				.cupoMaximo(30)
				.estado(EstadoTaller.INACTIVO)
				.build();

		tallerRepository.saveAll(List.of(active, inactive));

		Page<Taller> activeTalleres = tallerRepository.findByEstado(EstadoTaller.ACTIVO, PageRequest.of(0, 10));

		assertEquals(1, activeTalleres.getTotalElements());
		assertEquals("Activo", activeTalleres.getContent().get(0).getNombre());
	}

	@Test
	void testDelete() {
		Taller taller = Taller.builder()
				.nombre("Eliminar")
				.cupoMaximo(30)
				.build();

		Taller saved = tallerRepository.save(taller);
		assertNotNull(saved.getId());

		tallerRepository.delete(saved);

		Optional<Taller> found = tallerRepository.findById(saved.getId());
		assertFalse(found.isPresent());
	}
}
