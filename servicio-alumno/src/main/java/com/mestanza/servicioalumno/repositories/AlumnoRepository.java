package com.mestanza.servicioalumno.repositories;

import com.mestanza.servicioalumno.entities.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

	Page<Alumno> findByEstado(String estado, Pageable pageable);

	Optional<Alumno> findByEmail(String email);

	boolean existsByEmail(String email);
}
