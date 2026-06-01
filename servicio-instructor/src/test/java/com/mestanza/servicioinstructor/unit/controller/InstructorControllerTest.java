package com.mestanza.servicioinstructor.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mestanza.servicioinstructor.controllers.InstructorController;
import com.mestanza.servicioinstructor.dto.InstructorCreateDTO;
import com.mestanza.servicioinstructor.dto.InstructorDTO;
import com.mestanza.servicioinstructor.enums.EstadoInstructor;
import com.mestanza.servicioinstructor.services.InstructorService;
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

@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private InstructorService instructorService;

	private InstructorDTO instructorDTO;
	private InstructorCreateDTO createDTO;

	@BeforeEach
	void setUp() {
		instructorDTO = InstructorDTO.builder()
				.id(1L)
				.nombre("Juan")
				.apellido("Pérez")
				.email("juan.perez@email.com")
				.especialidad("Java")
				.estado(EstadoInstructor.ACTIVO)
				.build();

		createDTO = InstructorCreateDTO.builder()
				.nombre("Juan")
				.apellido("Pérez")
				.email("juan.perez@email.com")
				.especialidad("Java")
				.fechaContratacion(LocalDate.now())
				.build();
	}

	@Test
	void testFindAll() throws Exception {
		Page<InstructorDTO> page = new PageImpl<>(List.of(instructorDTO));
		when(instructorService.findAll(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/api/instructores"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].nombre").value("Juan"));
	}

	@Test
	void testFindById() throws Exception {
		when(instructorService.findById(1L)).thenReturn(instructorDTO);

		mockMvc.perform(get("/api/instructores/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Juan"))
				.andExpect(jsonPath("$.email").value("juan.perez@email.com"));
	}

	@Test
	void testCreate() throws Exception {
		when(instructorService.create(any(InstructorCreateDTO.class))).thenReturn(instructorDTO);

		mockMvc.perform(post("/api/instructores")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createDTO)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombre").value("Juan"));
	}

	@Test
	void testUpdate() throws Exception {
		when(instructorService.update(eq(1L), any(InstructorCreateDTO.class))).thenReturn(instructorDTO);

		mockMvc.perform(put("/api/instructores/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Juan"));
	}

	@Test
	void testDelete() throws Exception {
		mockMvc.perform(delete("/api/instructores/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	void testFindByEspecialidad() throws Exception {
		when(instructorService.findByEspecialidad("Java")).thenReturn(List.of(instructorDTO));

		mockMvc.perform(get("/api/instructores/search")
						.param("especialidad", "Java"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].especialidad").value("Java"));
	}
}
