package com.mestanza.serviciotaller.repositories;

import com.mestanza.serviciotaller.entities.Taller;
import com.mestanza.serviciotaller.enums.EstadoTaller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TallerRepository extends JpaRepository<Taller, Long> {

	Page<Taller> findByEstado(EstadoTaller estado, Pageable pageable);

	List<Taller> findByCategoriaContainingIgnoreCase(String categoria);

	List<Taller> findByNombreContainingIgnoreCase(String nombre);
}
