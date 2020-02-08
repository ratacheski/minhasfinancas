package com.ratacheski.minhasfinancas.api.resource;

import com.ratacheski.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.ratacheski.minhasfinancas.api.dto.LancamentoDTO;
import com.ratacheski.minhasfinancas.exception.RegraNegocioException;
import com.ratacheski.minhasfinancas.model.entity.Lancamento;
import com.ratacheski.minhasfinancas.model.entity.Usuario;
import com.ratacheski.minhasfinancas.model.enums.StatusLancamento;
import com.ratacheski.minhasfinancas.model.enums.TipoLancamento;
import com.ratacheski.minhasfinancas.service.LancamentoService;
import com.ratacheski.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento lancamento = converteLancamentoDTOParaLancamento(dto);
            lancamento = lancamentoService.salvarLancamento(lancamento);
            return new ResponseEntity(lancamento, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long idLancamento, @RequestBody LancamentoDTO dto) {
        return lancamentoService.obterPorId(idLancamento).map(entidade -> {
            try {
                Lancamento lancamento = converteLancamentoDTOParaLancamento(dto);
                lancamento.setId(entidade.getId());
                lancamentoService.atualizarLancamento(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long idLancamento, @RequestBody AtualizaStatusDTO dto) {
        return lancamentoService.obterPorId(idLancamento).map(lancamento -> {
            StatusLancamento statusLancamento = StatusLancamento.valueOf(dto.getStatus());
            if (statusLancamento == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status lançado. Envie um status Válido.");
            } else {
                try {
                    lancamento.setStatusLancamento(statusLancamento);
                    lancamentoService.atualizarLancamento(lancamento);
                    return ResponseEntity.ok(lancamento);
                } catch (RegraNegocioException e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }

        }).orElseGet(() ->
                new ResponseEntity<>("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity removerLancamento(@PathVariable("id") Long idLancamento) {
        return lancamentoService.obterPorId(idLancamento).map(lancamento -> {
            lancamentoService.removerLancamento(lancamento);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity buscarLancamento(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario) {
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        Optional<Usuario> retorno = usuarioService.obterPorId(idUsuario);
        if (retorno.isEmpty()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o id informado.");
        } else {
            lancamentoFiltro.setUsuario(retorno.get());
        }

        List<Lancamento> lancamentos = lancamentoService.buscarLancamentos(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    private Lancamento converteLancamentoDTOParaLancamento(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());
        lancamento.setUsuario(usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado")));
        if (dto.getTipo() != null)
            lancamento.setTipoLancamento(TipoLancamento.valueOf(dto.getTipo()));
        if (dto.getStatus() != null)
            lancamento.setStatusLancamento(StatusLancamento.valueOf(dto.getStatus()));
        return lancamento;
    }

}
