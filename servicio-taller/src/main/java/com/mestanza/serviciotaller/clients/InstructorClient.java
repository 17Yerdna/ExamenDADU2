package com.mestanza.serviciotaller.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "servicio-instructor", path = "/api/instructores")
public interface InstructorClient {

	@GetMapping("/{id}")
	Map<String, Object> obtenerInstructor(@PathVariable("id") Long instructorId);

	@GetMapping("/search")
	List<Map<String, Object>> buscarInstructores(@PathVariable("especialidad") String especialidad);
}
