package com.mestanza.serviciotaller.entities;

import com.mestanza.serviciotaller.enums.EstadoTaller;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "talleres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Taller {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 150, message = "El nombre no puede superar 150 caracteres")
	@Column(nullable = false)
	private String nombre;

	@Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Size(max = 100, message = "La categoría no puede superar 100 caracteres")
	private String categoria;

	@Positive(message = "La duración debe ser positiva")
	private Integer duracionHoras;

	@Positive(message = "El cupo máximo debe ser positivo")
	@Column(nullable = false)
	private Integer cupoMaximo;

	private BigDecimal costo;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private EstadoTaller estado = EstadoTaller.ACTIVO;

	@Builder.Default
	private LocalDateTime fechaCreacion = LocalDateTime.now();

	private LocalDateTime fechaActualizacion;

	@Version
	@Column(nullable = false)
	private Long version;

	@PrePersist
	protected void onCreate() {
		fechaCreacion = LocalDateTime.now();
		fechaActualizacion = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		fechaActualizacion = LocalDateTime.now();
	}
}
