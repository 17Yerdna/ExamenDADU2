package com.mestanza.serviciotaller.dto;

import com.mestanza.serviciotaller.enums.EstadoTaller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TallerCompletoDTO {
	private Long id;
	private String nombre;
	private String descripcion;
	private String categoria;
	private Integer duracionHoras;
	private Integer cupoMaximo;
	private Integer cuposDisponibles;
	private BigDecimal costo;
	private EstadoTaller estado;
	private LocalDateTime fechaCreacion;
	private List<HorarioDTO> horarios;
	private List<Map<String, Object>> instructores;
	private List<Map<String, Object>> alumnos;
}
