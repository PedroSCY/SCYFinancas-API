package com.Project.SCYFinancas.service;

import com.Project.SCYFinancas.model.entity.Usuario;

public interface UsuarioService {
	
	public Usuario autenticar(String email, String senha);
	
	public Usuario salvarUsuario(Usuario usuario);
	
	public void validarEmail(String Email);
	
}
