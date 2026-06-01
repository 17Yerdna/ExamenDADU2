package com.mestanza.serviciotaller.unit.service;

import com.mestanza.serviciotaller.clients.AlumnoClient;
import com.mestanza.serviciotaller.clients.InstructorClient;
import com.mestanza.serviciotaller.dto.TallerCreateDTO;
import com.mestanza.serviciotaller.dto.TallerDTO;
import com.mestanza.serviciotaller.entities.Taller;
import com.mestanza.serviciotaller.enums.EstadoTaller;
import com.mestanza.serviciotaller.repositories.HorarioRepository;
import com.mestanza.serviciotaller.repositories.TallerRepository;
import com.mestanza.serviciotaller.services.TallerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TallerServiceTest {

	@Mock
	private TallerRepository tallerRepository;

	@Mock
	private HorarioRepository horarioRepository;

	@Mock
	private InstructorClient instructorClient;

	@Mock
	private AlumnoClient alumnoClient;

	@InjectMocks
	private TallerService tallerService;

	private Taller taller;
	private TallerCreateDTO createDTO;

	@BeforeEach
	void setUp() {
		taller = Taller.builder()
				.id(1L)
				.nombre("Java Avanzado")
				.descripcion("Curso de Java avanzado")
				.categoria("Programación")
				.duracionHoras(40)
				.cupoMaximo(30)
				.costo(new BigDecimal("500.00"))
				.estado(EstadoTaller.ACTIVO)
				.build();

		createDTO = TallerCreateDTO.builder()
				.nombre("Java Avanzado")
				.descripcion("Curso de Java avanzado")
				.categoria("Programación")
				.duracionHoras(40)
				.cupoMaximo(30)
				.costo(new BigDecimal("500.00"))
				.build();
	}

	@Test
	void testFindAll() {
		Page<Taller> page = new PageImpl<>(List.of(taller));
		when(tallerRepository.findAll(any(PageRequest.class))).thenReturn(page);

		Page<TallerDTO> result = tallerService.findAll(PageRequest.of(0, 10));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Java Avanzado", result.getContent().get(0).getNombre());
	}

	@Test
	void testFindById_Success() {
		when(tallerRepository.findById(1L)).thenReturn(Optional.of(taller));

		TallerDTO result = tallerService.findById(1L);

		assertNotNull(result);
		assertEquals("Java Avanzado", result.getNombre());
	}

	@Test
	void testFindById_NotFound() {
		when(tallerRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> tallerService.findById(999L));
	}

	@Test
	void testCreate_Success() {
		when(tallerRepository.save(any(Taller.class))).thenReturn(taller);

		TallerDTO result = tallerService.create(createDTO);

		assertNotNull(result);
		assertEquals("Java Avanzado", result.getNombre());
		assertEquals(EstadoTaller.ACTIVO, result.getEstado());
	}

	@Test
	void testUpdate_Success() {
		when(tallerRepository.findById(1L)).thenReturn(Optional.of(taller));
		when(tallerRepository.save(any(Taller.class))).thenReturn(taller);

		TallerCreateDTO updateDTO = TallerCreateDTO.builder()
				.nombre("Java Avanzado v2")
				.cupoMaximo(40)
				.build();

		TallerDTO result = tallerService.update(1L, updateDTO);

		assertNotNull(result);
		assertEquals("Java Avanzado v2", result.getNombre());
	}

	@Test
	void testDelete_Success() {
		when(tallerRepository.findById(1L)).thenReturn(Optional.of(taller));
		when(tallerRepository.save(any(Taller.class))).thenReturn(taller);

		tallerService.delete(1L);

		assertEquals(EstadoTaller.INACTIVO, taller.getEstado());
	}

	@Test
	void testVerificarDisponibilidad() {
		when(tallerRepository.findById(1L)).thenReturn(Optional.of(taller));
		when(alumnoClient.obtenerAlumno(1L)).thenReturn(List.of());

		Map<String, Object> result = tallerService.verificarDisponibilidad(1L);

		assertNotNull(result);
		assertTrue((Boolean) result.get("disponible"));
		assertEquals(30, result.get("cuposDisponibles"));
	}
}
