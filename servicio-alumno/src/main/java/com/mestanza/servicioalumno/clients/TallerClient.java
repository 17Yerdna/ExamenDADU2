package com.mestanza.servicioalumno.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "servicio-taller", path = "/api/talleres")
public interface TallerClient {

	@GetMapping("/{id}")
	Map<String, Object> obtenerTaller(@PathVariable("id") Long tallerId);

	@GetMapping("/{id}/disponibilidad")
	Map<String, Object> verificarDisponibilidad(@PathVariable("id") Long tallerId);
}
