package com.mestanza.servicioinstructor.dto;

import com.mestanza.servicioinstructor.enums.EstadoInstructor;
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
public class InstructorDTO {
	private Long id;
	private String nombre;
	private String apellido;
	private String email;
	private String telefono;
	private String especialidad;
	private LocalDate fechaContratacion;
	private EstadoInstructor estado;
}
