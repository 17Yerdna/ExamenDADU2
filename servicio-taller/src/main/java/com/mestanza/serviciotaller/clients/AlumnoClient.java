package com.mestanza.serviciotaller.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "servicio-alumno", path = "/api/alumnos")
public interface AlumnoClient {

	@GetMapping("/{id}")
	Map<String, Object> obtenerAlumno(@PathVariable("id") Long alumnoId);
}
