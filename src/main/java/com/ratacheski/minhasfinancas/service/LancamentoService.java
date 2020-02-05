package com.ratacheski.minhasfinancas.service;

import com.ratacheski.minhasfinancas.model.entity.Lancamento;
import com.ratacheski.minhasfinancas.model.enums.StatusLancamento;

import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Lancamento salvarLancamento(Lancamento lancamento);

    Lancamento atualizarLancamento(Lancamento lancamento);

    void removerLancamento(Lancamento lancamento);

    List<Lancamento> buscarLancamentos(Lancamento lancamentoFiltro);

    void atualizarStatusLancamento(Lancamento lancamento, StatusLancamento statusLancamento);

    void validarLancamento(Lancamento lancamento);

    Optional<Lancamento> obterPorId(Long idLancamento);
}
