package com.mestanza.servicioinstructor.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "servicio-taller", path = "/api/talleres")
public interface TallerClient {

	@GetMapping("/{id}/instructores")
	List<Map<String, Object>> obtenerInstructoresPorTaller(@PathVariable("id") Long tallerId);
}
