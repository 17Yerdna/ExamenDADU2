package com.mestanza.servicioalumno.repositories;

import com.mestanza.servicioalumno.entities.Inscripcion;
import com.mestanza.servicioalumno.enums.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

	List<Inscripcion> findByAlumnoId(Long alumnoId);

	List<Inscripcion> findByTallerId(Long tallerId);

	Optional<Inscripcion> findByAlumnoIdAndTallerId(Long alumnoId, Long tallerId);

	boolean existsByAlumnoIdAndTallerId(Long alumnoId, Long tallerId);

	List<Inscripcion> findByAlumnoIdAndEstado(Long alumnoId, EstadoInscripcion estado);
}
