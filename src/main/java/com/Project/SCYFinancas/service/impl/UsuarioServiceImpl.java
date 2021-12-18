package com.Project.SCYFinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Project.SCYFinancas.exceptions.ErroAutenticacao;
import com.Project.SCYFinancas.exceptions.RegraDeNegocioException;
import com.Project.SCYFinancas.model.entity.Usuario;
import com.Project.SCYFinancas.model.repository.UsuarioRepository;
import com.Project.SCYFinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;
	
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuario não encontrado");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha incorreta");
		}
		
		return usuario.get();
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());;
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String Email) {
		boolean existe = repository.existsByEmail(Email);
		if(existe) {
			throw new RegraDeNegocioException("Já existe um usuario cadastrado com este email");
		} 
	}

}
