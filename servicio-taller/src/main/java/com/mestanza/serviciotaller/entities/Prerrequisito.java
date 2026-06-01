package com.mestanza.serviciotaller.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prerrequisitos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prerrequisito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long tallerId;

	@Column(nullable = false)
	private Long tallerPrerrequisitoId;
}
