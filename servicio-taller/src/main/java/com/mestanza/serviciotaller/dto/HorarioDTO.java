package com.mestanza.serviciotaller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDTO {
	private Long id;
	private Long tallerId;
	private String diaSemana;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private String aula;
}
