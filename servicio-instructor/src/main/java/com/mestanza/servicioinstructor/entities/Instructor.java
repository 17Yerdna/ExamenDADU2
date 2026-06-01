package com.mestanza.servicioinstructor.entities;

import com.mestanza.servicioinstructor.enums.EstadoInstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "instructores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede superar 100 caracteres")
	@Column(nullable = false)
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio")
	@Size(max = 100, message = "El apellido no puede superar 100 caracteres")
	@Column(nullable = false)
	private String apellido;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email debe tener un formato válido")
	@Size(max = 150, message = "El email no puede superar 150 caracteres")
	@Column(nullable = false, unique = true)
	private String email;

	@Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
	private String telefono;

	@Size(max = 100, message = "La especialidad no puede superar 100 caracteres")
	private String especialidad;

	private LocalDate fechaContratacion;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private EstadoInstructor estado = EstadoInstructor.ACTIVO;

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
