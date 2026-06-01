package com.mestanza.serviciotaller.repositories;

import com.mestanza.serviciotaller.entities.Prerrequisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrerrequisitoRepository extends JpaRepository<Prerrequisito, Long> {

	List<Prerrequisito> findByTallerId(Long tallerId);

	void deleteByTallerId(Long tallerId);
}
