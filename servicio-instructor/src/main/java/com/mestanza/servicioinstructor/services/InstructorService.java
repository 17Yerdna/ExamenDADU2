package com.mestanza.servicioinstructor.services;

import com.mestanza.servicioinstructor.dto.InstructorCreateDTO;
import com.mestanza.servicioinstructor.dto.InstructorDTO;
import com.mestanza.servicioinstructor.entities.Instructor;
import com.mestanza.servicioinstructor.enums.EstadoInstructor;
import com.mestanza.servicioinstructor.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstructorService {

	private final InstructorRepository instructorRepository;

	@Transactional(readOnly = true)
	public Page<InstructorDTO> findAll(Pageable pageable) {
		log.debug("Listando todos los instructores");
		return instructorRepository.findAll(pageable)
				.map(this::toDTO);
	}

	@Transactional(readOnly = true)
	public Page<InstructorDTO> findByEstado(EstadoInstructor estado, Pageable pageable) {
		log.debug("Listando instructores con estado: {}", estado);
		return instructorRepository.findByEstado(estado, pageable)
				.map(this::toDTO);
	}

	@Transactional(readOnly = true)
	public InstructorDTO findById(Long id) {
		log.debug("Buscando instructor con ID: {}", id);
		Instructor instructor = instructorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Instructor no encontrado con ID: " + id));
		return toDTO(instructor);
	}

	@Transactional(readOnly = true)
	public List<InstructorDTO> findByEspecialidad(String especialidad) {
		log.debug("Buscando instructores por especialidad: {}", especialidad);
		return instructorRepository.findByEspecialidadContainingIgnoreCase(especialidad)
				.stream()
				.map(this::toDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public InstructorDTO create(InstructorCreateDTO dto) {
		log.info("Creando nuevo instructor: {}", dto.getEmail());

		if (instructorRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Ya existe un instructor con el email: " + dto.getEmail());
		}

		Instructor instructor = Instructor.builder()
				.nombre(dto.getNombre())
				.apellido(dto.getApellido())
				.email(dto.getEmail())
				.telefono(dto.getTelefono())
				.especialidad(dto.getEspecialidad())
				.fechaContratacion(dto.getFechaContratacion())
				.estado(EstadoInstructor.ACTIVO)
				.build();

		Instructor saved = instructorRepository.save(instructor);
		log.info("Instructor creado con ID: {}", saved.getId());
		return toDTO(saved);
	}

	@Transactional
	public InstructorDTO update(Long id, InstructorCreateDTO dto) {
		log.info("Actualizando instructor con ID: {}", id);

		Instructor instructor = instructorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Instructor no encontrado con ID: " + id));

		instructor.setNombre(dto.getNombre());
		instructor.setApellido(dto.getApellido());
		instructor.setTelefono(dto.getTelefono());
		instructor.setEspecialidad(dto.getEspecialidad());
		instructor.setFechaContratacion(dto.getFechaContratacion());

		Instructor updated = instructorRepository.save(instructor);
		log.info("Instructor actualizado: {}", updated.getId());
		return toDTO(updated);
	}

	@Transactional
	public void delete(Long id) {
		log.info("Eliminando instructor con ID: {}", id);

		Instructor instructor = instructorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Instructor no encontrado con ID: " + id));

		instructor.setEstado(EstadoInstructor.INACTIVO);
		instructorRepository.save(instructor);
		log.info("Instructor eliminado (lógico): {}", id);
	}

	private InstructorDTO toDTO(Instructor instructor) {
		return InstructorDTO.builder()
				.id(instructor.getId())
				.nombre(instructor.getNombre())
				.apellido(instructor.getApellido())
				.email(instructor.getEmail())
				.telefono(instructor.getTelefono())
				.especialidad(instructor.getEspecialidad())
				.fechaContratacion(instructor.getFechaContratacion())
				.estado(instructor.getEstado())
				.build();
	}
}
