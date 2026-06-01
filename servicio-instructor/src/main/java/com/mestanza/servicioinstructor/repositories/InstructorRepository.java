package com.mestanza.servicioinstructor.repositories;

import com.mestanza.servicioinstructor.entities.Instructor;
import com.mestanza.servicioinstructor.enums.EstadoInstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

	Page<Instructor> findByEstado(EstadoInstructor estado, Pageable pageable);

	List<Instructor> findByEspecialidadContainingIgnoreCase(String especialidad);

	Optional<Instructor> findByEmail(String email);

	boolean existsByEmail(String email);
}
