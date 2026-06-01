package com.mestanza.servicioalumno.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mestanza.servicioalumno.controllers.AlumnoController;
import com.mestanza.servicioalumno.dto.AlumnoCreateDTO;
import com.mestanza.servicioalumno.dto.AlumnoDTO;
import com.mestanza.servicioalumno.services.AlumnoService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlumnoController.class)
class AlumnoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AlumnoService alumnoService;

	private AlumnoDTO alumnoDTO;
	private AlumnoCreateDTO createDTO;

	@BeforeEach
	void setUp() {
		alumnoDTO = AlumnoDTO.builder()
				.id(1L)
				.nombre("María")
				.apellido("García")
				.email("maria.garcia@email.com")
				.estado("ACTIVO")
				.build();

		createDTO = AlumnoCreateDTO.builder()
				.nombre("María")
				.apellido("García")
				.email("maria.garcia@email.com")
				.fechaNacimiento(LocalDate.of(2000, 1, 1))
				.build();
	}

	@Test
	void testFindAll() throws Exception {
		Page<AlumnoDTO> page = new PageImpl<>(List.of(alumnoDTO));
		when(alumnoService.findAll(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/alumnos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].nombre").value("María"));
	}

	@Test
	void testFindById() throws Exception {
		when(alumnoService.findById(1L)).thenReturn(alumnoDTO);

		mockMvc.perform(get("/api/alumnos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("María"));
	}

	@Test
	void testCreate() throws Exception {
		when(alumnoService.create(any(AlumnoCreateDTO.class))).thenReturn(alumnoDTO);

		mockMvc.perform(post("/api/alumnos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createDTO)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombre").value("María"));
	}

	@Test
	void testUpdate() throws Exception {
		when(alumnoService.update(eq(1L), any(AlumnoCreateDTO.class))).thenReturn(alumnoDTO);

		mockMvc.perform(put("/api/alumnos/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createDTO)))
				.andExpect(status().isOk());
	}

	@Test
	void testDelete() throws Exception {
		mockMvc.perform(delete("/api/alumnos/1"))
				.andExpect(status().isNoContent());
	}
}
