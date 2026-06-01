package com.mestanza.serviciotaller.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long tallerId;

	@NotBlank(message = "El día de la semana es obligatorio")
	private String diaSemana;

	@NotNull(message = "La hora de inicio es obligatoria")
	private LocalTime horaInicio;

	@NotNull(message = "La hora de fin es obligatoria")
	private LocalTime horaFin;

	@Size(max = 50, message = "El aula no puede superar 50 caracteres")
	private String aula;

	@Builder.Default
	private LocalDateTime fechaCreacion = LocalDateTime.now();

	@PrePersist
	protected void onCreate() {
		fechaCreacion = LocalDateTime.now();
	}
}
