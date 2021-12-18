package com.Project.SCYFinancas.service;

import java.util.Optional;

import com.Project.SCYFinancas.model.entity.Usuario;

public interface UsuarioService {
	
	public Usuario autenticar(String email, String senha);
	
	public Usuario salvarUsuario(Usuario usuario);
	
	public void validarEmail(String Email);
	
	public Optional<Usuario> obterPorId(Long id);
	
}
