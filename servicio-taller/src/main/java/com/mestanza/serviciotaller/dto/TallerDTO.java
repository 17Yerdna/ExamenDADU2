package com.mestanza.serviciotaller.dto;

import com.mestanza.serviciotaller.enums.EstadoTaller;
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
public class TallerDTO {
	private Long id;
	private String nombre;
	private String descripcion;
	private String categoria;
	private Integer duracionHoras;
	private Integer cupoMaximo;
	private BigDecimal costo;
	private EstadoTaller estado;
	private LocalDateTime fechaCreacion;
}
