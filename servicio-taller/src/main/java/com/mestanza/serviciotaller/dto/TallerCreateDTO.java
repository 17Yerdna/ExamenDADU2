package com.mestanza.serviciotaller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TallerCreateDTO {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 150, message = "El nombre no puede superar 150 caracteres")
	private String nombre;

	@Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
	private String descripcion;

	@Size(max = 100, message = "La categoría no puede superar 100 caracteres")
	private String categoria;

	@Positive(message = "La duración debe ser positiva")
	private Integer duracionHoras;

	@Positive(message = "El cupo máximo debe ser positivo")
	private Integer cupoMaximo;

	private BigDecimal costo;
}
