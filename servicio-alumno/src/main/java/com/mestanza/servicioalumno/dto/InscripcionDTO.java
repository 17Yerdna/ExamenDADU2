package com.mestanza.servicioalumno.dto;

import com.mestanza.servicioalumno.enums.EstadoInscripcion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionDTO {
	private Long id;
	private Long alumnoId;
	private Long tallerId;
	private LocalDateTime fechaInscripcion;
	private EstadoInscripcion estado;
	private BigDecimal calificacion;
}
