package com.mestanza.serviciotaller.services;

import com.mestanza.serviciotaller.clients.AlumnoClient;
import com.mestanza.serviciotaller.clients.InstructorClient;
import com.mestanza.serviciotaller.dto.*;
import com.mestanza.serviciotaller.entities.Horario;
import com.mestanza.serviciotaller.entities.Taller;
import com.mestanza.serviciotaller.enums.EstadoTaller;
import com.mestanza.serviciotaller.repositories.HorarioRepository;
import com.mestanza.serviciotaller.repositories.TallerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TallerService {

	private final TallerRepository tallerRepository;
	private final HorarioRepository horarioRepository;
	private final InstructorClient instructorClient;
	private final AlumnoClient alumnoClient;

	@Transactional(readOnly = true)
	public Page<TallerDTO> findAll(Pageable pageable) {
		log.debug("Listando todos los talleres");
		return tallerRepository.findAll(pageable)
				.map(this::toDTO);
	}

	@Transactional(readOnly = true)
	public Page<TallerDTO> findByEstado(EstadoTaller estado, Pageable pageable) {
		log.debug("Listando talleres con estado: {}", estado);
		return tallerRepository.findByEstado(estado, pageable)
				.map(this::toDTO);
	}

	@Transactional(readOnly = true)
	public List<TallerDTO> findByCategoria(String categoria) {
		log.debug("Buscando talleres por categoría: {}", categoria);
		return tallerRepository.findByCategoriaContainingIgnoreCase(categoria)
				.stream()
				.map(this::toDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<TallerDTO> findByNombre(String nombre) {
		log.debug("Buscando talleres por nombre: {}", nombre);
		return tallerRepository.findByNombreContainingIgnoreCase(nombre)
				.stream()
				.map(this::toDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public TallerDTO findById(Long id) {
		log.debug("Buscando taller con ID: {}", id);
		Taller taller = tallerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + id));
		return toDTO(taller);
	}

	@Transactional(readOnly = true)
	public TallerCompletoDTO findByIdCompleto(Long id) {
		log.debug("Buscando taller completo con ID: {}", id);
		Taller taller = tallerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + id));

		List<HorarioDTO> horarios = horarioRepository.findByTallerId(id)
				.stream()
				.map(this::toHorarioDTO)
				.collect(Collectors.toList());

		List<Map<String, Object>> instructores = new ArrayList<>();
		List<Map<String, Object>> alumnos = new ArrayList<>();

		log.info("Taller compuesto cargado: {} con {} horarios", id, horarios.size());

		return TallerCompletoDTO.builder()
				.id(taller.getId())
				.nombre(taller.getNombre())
				.descripcion(taller.getDescripcion())
				.categoria(taller.getCategoria())
				.duracionHoras(taller.getDuracionHoras())
				.cupoMaximo(taller.getCupoMaximo())
				.cuposDisponibles(calcularCuposDisponibles(id))
				.costo(taller.getCosto())
				.estado(taller.getEstado())
				.fechaCreacion(taller.getFechaCreacion())
				.horarios(horarios)
				.instructores(instructores)
				.alumnos(alumnos)
				.build();
	}

	@Transactional
	public TallerDTO create(TallerCreateDTO dto) {
		log.info("Creando nuevo taller: {}", dto.getNombre());

		Taller taller = Taller.builder()
				.nombre(dto.getNombre())
				.descripcion(dto.getDescripcion())
				.categoria(dto.getCategoria())
				.duracionHoras(dto.getDuracionHoras())
				.cupoMaximo(dto.getCupoMaximo())
				.costo(dto.getCosto())
				.estado(EstadoTaller.ACTIVO)
				.build();

		Taller saved = tallerRepository.save(taller);
		log.info("Taller creado con ID: {}", saved.getId());
		return toDTO(saved);
	}

	@Transactional
	public TallerDTO update(Long id, TallerCreateDTO dto) {
		log.info("Actualizando taller con ID: {}", id);

		Taller taller = tallerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + id));

		taller.setNombre(dto.getNombre());
		taller.setDescripcion(dto.getDescripcion());
		taller.setCategoria(dto.getCategoria());
		taller.setDuracionHoras(dto.getDuracionHoras());
		taller.setCupoMaximo(dto.getCupoMaximo());
		taller.setCosto(dto.getCosto());

		Taller updated = tallerRepository.save(taller);
		log.info("Taller actualizado: {}", updated.getId());
		return toDTO(updated);
	}

	@Transactional
	public void delete(Long id) {
		log.info("Eliminando taller con ID: {}", id);

		Taller taller = tallerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + id));

		taller.setEstado(EstadoTaller.INACTIVO);
		tallerRepository.save(taller);

		horarioRepository.deleteByTallerId(id);
		log.info("Taller eliminado (lógico): {}", id);
	}

	@Transactional(readOnly = true)
	public List<HorarioDTO> obtenerHorarios(Long tallerId) {
		log.debug("Obteniendo horarios del taller: {}", tallerId);
		return horarioRepository.findByTallerId(tallerId)
				.stream()
				.map(this::toHorarioDTO)
				.collect(Collectors.toList());
	}

	@Transactional
	public HorarioDTO agregarHorario(Long tallerId, HorarioDTO horarioDTO) {
		log.info("Agregando horario al taller: {}", tallerId);

		tallerRepository.findById(tallerId)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + tallerId));

		Horario horario = Horario.builder()
				.tallerId(tallerId)
				.diaSemana(horarioDTO.getDiaSemana())
				.horaInicio(horarioDTO.getHoraInicio())
				.horaFin(horarioDTO.getHoraFin())
				.aula(horarioDTO.getAula())
				.build();

		Horario saved = horarioRepository.save(horario);
		return toHorarioDTO(saved);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> verificarDisponibilidad(Long tallerId) {
		log.debug("Verificando disponibilidad del taller: {}", tallerId);
		Taller taller = tallerRepository.findById(tallerId)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + tallerId));

		int cuposDisponibles = calcularCuposDisponibles(tallerId);
		boolean disponible = cuposDisponibles > 0 && taller.getEstado() == EstadoTaller.ACTIVO;

		return Map.of(
				"tallerId", tallerId,
				"cupoMaximo", taller.getCupoMaximo(),
				"cuposDisponibles", cuposDisponibles,
				"disponible", disponible,
				"estado", taller.getEstado().name()
		);
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> obtenerInstructoresPorTaller(Long tallerId) {
		log.debug("Obteniendo instructores del taller: {}", tallerId);
		tallerRepository.findById(tallerId)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + tallerId));

		return new ArrayList<>();
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> obtenerAlumnosPorTaller(Long tallerId) {
		log.debug("Obteniendo alumnos del taller: {}", tallerId);
		tallerRepository.findById(tallerId)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + tallerId));

		return new ArrayList<>();
	}

	private int calcularCuposDisponibles(Long tallerId) {
		Taller taller = tallerRepository.findById(tallerId)
				.orElseThrow(() -> new RuntimeException("Taller no encontrado con ID: " + tallerId));

		try {
			Map<String, Object> alumno = alumnoClient.obtenerAlumno(tallerId);
			int inscritos = alumno != null ? 1 : 0;
			return Math.max(0, taller.getCupoMaximo() - inscritos);
		} catch (Exception e) {
			log.warn("No se pudo obtener cantidad de alumnos: {}", e.getMessage());
			return taller.getCupoMaximo();
		}
	}

	private TallerDTO toDTO(Taller taller) {
		return TallerDTO.builder()
				.id(taller.getId())
				.nombre(taller.getNombre())
				.descripcion(taller.getDescripcion())
				.categoria(taller.getCategoria())
				.duracionHoras(taller.getDuracionHoras())
				.cupoMaximo(taller.getCupoMaximo())
				.costo(taller.getCosto())
				.estado(taller.getEstado())
				.fechaCreacion(taller.getFechaCreacion())
				.build();
	}

	private HorarioDTO toHorarioDTO(Horario horario) {
		return HorarioDTO.builder()
				.id(horario.getId())
				.tallerId(horario.getTallerId())
				.diaSemana(horario.getDiaSemana())
				.horaInicio(horario.getHoraInicio())
				.horaFin(horario.getHoraFin())
				.aula(horario.getAula())
				.build();
	}
}
