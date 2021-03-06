package com.Project.SCYFinancas.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project.SCYFinancas.api.dto.AtualizaStatusDTO;
import com.Project.SCYFinancas.api.dto.LancamentoDTO;
import com.Project.SCYFinancas.exceptions.RegraDeNegocioException;
import com.Project.SCYFinancas.model.entity.Lancamento;
import com.Project.SCYFinancas.model.entity.Usuario;
import com.Project.SCYFinancas.model.enums.StatusLancamento;
import com.Project.SCYFinancas.model.enums.TipoLancamento;
import com.Project.SCYFinancas.service.LancamentoService;
import com.Project.SCYFinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam( value = "descricao", required = false) String descricao,
			@RequestParam( value = "mes", required = false) Integer mes,
			@RequestParam( value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario
			) {
		Lancamento filtro = new Lancamento();
		filtro.setDescricao(descricao);
		filtro.setMes(mes);
		filtro.setAno(ano);
		
		Optional<Usuario> usuario =usuarioService.obterPorId(idUsuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("N??o foi possivel realizar as consulta. Usuario n??o encontrado para o ID informado.");
		}else {
			filtro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(filtro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody LancamentoDTO tdo) {
		try {
			Lancamento entidade = converter(tdo);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraDeNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	private ResponseEntity atualizar(@ PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return  service.obterPorId(id).map( entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraDeNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lan??amento n??o encontrado no bando de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id ,@RequestBody AtualizaStatusDTO dto ) {
		return  service.obterPorId(id).map( entity -> {
			try {
				StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
				if(statusSelecionado == null) {
					return ResponseEntity.badRequest().body("N??o foi possivel atualizar o status do lan??amento, envie um status valido.");
				}
				entity.setStatus(statusSelecionado );
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraDeNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lan??amento n??o encontrado no bando de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar( @PathVariable("id") Long id) {
		return service.obterPorId(id).map(entidade -> {
			service.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> 
			new ResponseEntity("Lan??amento n??o encontrado no bando de dados.", HttpStatus.BAD_REQUEST));
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
				.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraDeNegocioException("Usuario n??o encontrado para o ID informado." ));
		
		lancamento.setUsuario(usuario);

		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		
		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		return lancamento;
	}
	
}
