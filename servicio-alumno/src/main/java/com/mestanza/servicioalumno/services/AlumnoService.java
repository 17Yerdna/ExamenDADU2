package com.mestanza.servicioalumno.services;

import com.mestanza.servicioalumno.clients.TallerClient;
import com.mestanza.servicioalumno.dto.AlumnoCreateDTO;
import com.mestanza.servicioalumno.dto.AlumnoDTO;
import com.mestanza.servicioalumno.dto.InscripcionDTO;
import com.mestanza.servicioalumno.entities.Alumno;
import com.mestanza.servicioalumno.entities.Inscripcion;
import com.mestanza.servicioalumno.enums.EstadoInscripcion;
import com.mestanza.servicioalumno.repositories.AlumnoRepository;
import com.mestanza.servicioalumno.repositories.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlumnoService {

	private final AlumnoRepository alumnoRepository;
	private final InscripcionRepository inscripcionRepository;
	private final TallerClient tallerClient;

	@Transactional(readOnly = true)
	public Page<AlumnoDTO> findAll(Pageable pageable) {
		log.debug("Listando todos los alumnos");
		return alumnoRepository.findAll(pageable)
				.map(this::toDTO);
	}

	@Transactional(readOnly = true)
	public Page<AlumnoDTO> findByEstado(String estado, Pageable pageable) {
		log.debug("Listando alumnos con estado: {}", estado);
		return alumnoRepository.findByEstado(estado, pageable)
				.map(this::toDTO);
	}

	@Transactional(readOnly = true)
	public AlumnoDTO findById(Long id) {
		log.debug("Buscando alumno con ID: {}", id);
		Alumno alumno = alumnoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
		return toDTO(alumno);
	}

	@Transactional
	public AlumnoDTO create(AlumnoCreateDTO dto) {
		log.info("Creando nuevo alumno: {}", dto.getEmail());

		if (alumnoRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Ya existe un alumno con el email: " + dto.getEmail());
		}

		Alumno alumno = Alumno.builder()
				.nombre(dto.getNombre())
				.apellido(dto.getApellido())
				.email(dto.getEmail())
				.telefono(dto.getTelefono())
				.fechaNacimiento(dto.getFechaNacimiento())
				.estado("ACTIVO")
				.build();

		Alumno saved = alumnoRepository.save(alumno);
		log.info("Alumno creado con ID: {}", saved.getId());
		return toDTO(saved);
	}

	@Transactional
	public AlumnoDTO update(Long id, AlumnoCreateDTO dto) {
		log.info("Actualizando alumno con ID: {}", id);

		Alumno alumno = alumnoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));

		alumno.setNombre(dto.getNombre());
		alumno.setApellido(dto.getApellido());
		alumno.setTelefono(dto.getTelefono());
		alumno.setFechaNacimiento(dto.getFechaNacimiento());

		Alumno updated = alumnoRepository.save(alumno);
		log.info("Alumno actualizado: {}", updated.getId());
		return toDTO(updated);
	}

	@Transactional
	public void delete(Long id) {
		log.info("Eliminando alumno con ID: {}", id);

		Alumno alumno = alumnoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));

		alumno.setEstado("INACTIVO");
		alumnoRepository.save(alumno);
		log.info("Alumno eliminado (lógico): {}", id);
	}

	@Transactional
	public InscripcionDTO inscribirEnTaller(Long alumnoId, Long tallerId) {
		log.info("Inscribiendo alumno {} en taller {}", alumnoId, tallerId);

		if (inscripcionRepository.existsByAlumnoIdAndTallerId(alumnoId, tallerId)) {
			throw new RuntimeException("El alumno ya está inscrito en este taller");
		}

		try {
			Map<String, Object> disponibilidad = tallerClient.verificarDisponibilidad(tallerId);
			Boolean disponible = (Boolean) disponibilidad.get("disponible");
			if (Boolean.FALSE.equals(disponible)) {
				throw new RuntimeException("El taller no tiene cupos disponibles");
			}
		} catch (Exception e) {
			log.warn("No se pudo verificar disponibilidad del taller: {}", e.getMessage());
		}

		Inscripcion inscripcion = Inscripcion.builder()
				.alumnoId(alumnoId)
				.tallerId(tallerId)
				.estado(EstadoInscripcion.PENDIENTE)
				.build();

		Inscripcion saved = inscripcionRepository.save(inscripcion);
		log.info("Inscripción creada con ID: {}", saved.getId());
		return toInscripcionDTO(saved);
	}

	@Transactional(readOnly = true)
	public List<InscripcionDTO> obtenerInscripciones(Long alumnoId) {
		log.debug("Obteniendo inscripciones del alumno: {}", alumnoId);
		return inscripcionRepository.findByAlumnoId(alumnoId)
				.stream()
				.map(this::toInscripcionDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> obtenerTalleresPorAlumno(Long alumnoId) {
		log.debug("Obteniendo talleres del alumno: {}", alumnoId);
		List<Inscripcion> inscripciones = inscripcionRepository.findByAlumnoId(alumnoId);
		return inscripciones.stream()
				.map(inscripcion -> {
					try {
						return tallerClient.obtenerTaller(inscripcion.getTallerId());
					} catch (Exception e) {
						log.warn("No se pudo obtener taller {}: {}", inscripcion.getTallerId(), e.getMessage());
						Map<String, Object> errorMap = new HashMap<>();
						errorMap.put("id", inscripcion.getTallerId());
						errorMap.put("error", "No disponible");
						return errorMap;
					}
				})
				.collect(Collectors.toList());
	}

	private AlumnoDTO toDTO(Alumno alumno) {
		return AlumnoDTO.builder()
				.id(alumno.getId())
				.nombre(alumno.getNombre())
				.apellido(alumno.getApellido())
				.email(alumno.getEmail())
				.telefono(alumno.getTelefono())
				.fechaNacimiento(alumno.getFechaNacimiento())
				.fechaRegistro(alumno.getFechaRegistro())
				.estado(alumno.getEstado())
				.build();
	}

	private InscripcionDTO toInscripcionDTO(Inscripcion inscripcion) {
		return InscripcionDTO.builder()
				.id(inscripcion.getId())
				.alumnoId(inscripcion.getAlumnoId())
				.tallerId(inscripcion.getTallerId())
				.fechaInscripcion(inscripcion.getFechaInscripcion())
				.estado(inscripcion.getEstado())
				.calificacion(inscripcion.getCalificacion())
				.build();
	}
}
