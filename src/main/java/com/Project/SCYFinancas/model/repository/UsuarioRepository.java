package com.Project.SCYFinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Project.SCYFinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public boolean existsByEmail(String email);
	
	public Optional<Usuario> findByEmail(String email);
}
