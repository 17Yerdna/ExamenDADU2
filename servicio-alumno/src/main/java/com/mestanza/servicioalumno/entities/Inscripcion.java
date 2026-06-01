package com.mestanza.servicioalumno.entities;

import com.mestanza.servicioalumno.enums.EstadoInscripcion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long alumnoId;

	@Column(nullable = false)
	private Long tallerId;

	@Builder.Default
	private LocalDateTime fechaInscripcion = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private EstadoInscripcion estado = EstadoInscripcion.PENDIENTE;

	private BigDecimal calificacion;

	@Builder.Default
	private LocalDateTime fechaCreacion = LocalDateTime.now();

	private LocalDateTime fechaActualizacion;

	@PrePersist
	protected void onCreate() {
		fechaCreacion = LocalDateTime.now();
		fechaActualizacion = LocalDateTime.now();
		if (fechaInscripcion == null) {
			fechaInscripcion = LocalDateTime.now();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		fechaActualizacion = LocalDateTime.now();
	}
}
