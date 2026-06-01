package com.mestanza.serviciotaller.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mestanza.serviciotaller.controllers.TallerController;
import com.mestanza.serviciotaller.dto.TallerCreateDTO;
import com.mestanza.serviciotaller.dto.TallerDTO;
import com.mestanza.serviciotaller.enums.EstadoTaller;
import com.mestanza.serviciotaller.services.TallerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TallerController.class)
class TallerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private TallerService tallerService;

	private TallerDTO tallerDTO;
	private TallerCreateDTO createDTO;

	@BeforeEach
	void setUp() {
		tallerDTO = TallerDTO.builder()
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
	void testFindAll() throws Exception {
		Page<TallerDTO> page = new PageImpl<>(List.of(tallerDTO));
		when(tallerService.findAll(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/talleres"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].nombre").value("Java Avanzado"));
	}

	@Test
	void testFindById() throws Exception {
		when(tallerService.findById(1L)).thenReturn(tallerDTO);

		mockMvc.perform(get("/api/talleres/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Java Avanzado"));
	}

	@Test
	void testCreate() throws Exception {
		when(tallerService.create(any(TallerCreateDTO.class))).thenReturn(tallerDTO);

		mockMvc.perform(post("/api/talleres")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createDTO)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombre").value("Java Avanzado"));
	}

	@Test
	void testUpdate() throws Exception {
		when(tallerService.update(eq(1L), any(TallerCreateDTO.class))).thenReturn(tallerDTO);

		mockMvc.perform(put("/api/talleres/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createDTO)))
				.andExpect(status().isOk());
	}

	@Test
	void testDelete() throws Exception {
		mockMvc.perform(delete("/api/talleres/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	void testFindByCategoria() throws Exception {
		when(tallerService.findByCategoria("Programación")).thenReturn(List.of(tallerDTO));

		mockMvc.perform(get("/api/talleres/categoria/Programación"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].categoria").value("Programación"));
	}
}
