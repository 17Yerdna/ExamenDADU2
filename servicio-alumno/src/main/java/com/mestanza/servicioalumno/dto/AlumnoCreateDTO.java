package com.mestanza.servicioalumno.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlumnoCreateDTO {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede superar 100 caracteres")
	private String nombre;

	@NotBlank(message = "El apellido es obligatorio")
	@Size(max = 100, message = "El apellido no puede superar 100 caracteres")
	private String apellido;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email debe tener un formato válido")
	@Size(max = 150, message = "El email no puede superar 150 caracteres")
	private String email;

	@Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
	private String telefono;

	private LocalDate fechaNacimiento;
}
