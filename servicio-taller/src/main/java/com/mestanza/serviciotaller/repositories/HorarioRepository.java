package com.mestanza.serviciotaller.repositories;

import com.mestanza.serviciotaller.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

	List<Horario> findByTallerId(Long tallerId);

	void deleteByTallerId(Long tallerId);
}
