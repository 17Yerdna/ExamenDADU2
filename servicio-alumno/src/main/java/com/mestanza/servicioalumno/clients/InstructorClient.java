package com.mestanza.servicioalumno.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "servicio-instructor", path = "/api/instructores")
public interface InstructorClient {

	@GetMapping("/{id}")
	Map<String, Object> obtenerInstructor(@PathVariable("id") Long instructorId);
}
