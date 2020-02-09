package com.ratacheski.minhasfinancas.service.bean;

import com.ratacheski.minhasfinancas.exception.RegraNegocioException;
import com.ratacheski.minhasfinancas.model.entity.Lancamento;
import com.ratacheski.minhasfinancas.model.enums.StatusLancamento;
import com.ratacheski.minhasfinancas.model.enums.TipoLancamento;
import com.ratacheski.minhasfinancas.model.repository.LancamentoRepository;
import com.ratacheski.minhasfinancas.service.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceBean implements LancamentoService {

    private LancamentoRepository lancamentoRepository;

    public LancamentoServiceBean(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    @Override
    @Transactional
    public Lancamento salvarLancamento(Lancamento lancamento) {
        validarLancamento(lancamento);
        lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizarLancamento(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validarLancamento(lancamento);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public void removerLancamento(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        lancamentoRepository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscarLancamentos(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return lancamentoRepository.findAll(example);
    }

    @Override
    public void atualizarStatusLancamento(Lancamento lancamento, StatusLancamento statusLancamento) {
        lancamento.setStatusLancamento(statusLancamento);
        atualizarLancamento(lancamento);
    }

    @Override
    public void validarLancamento(Lancamento lancamento) {
        if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
            throw new RegraNegocioException("Informe uma descrição válida.");
        }

        if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
            throw new RegraNegocioException("Informe um mês válido.");
        }

        if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
            throw new RegraNegocioException("Informe um ano válido.");
        }

        if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
            throw new RegraNegocioException("Informe um usuário.");
        }

        if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
            throw new RegraNegocioException("Informe um valor válido positivo.");
        }

        if (lancamento.getTipoLancamento() == null) {
            throw new RegraNegocioException("Informe um tipo de lançamento.");
        }
    }

    @Override
    public Optional<Lancamento> obterPorId(Long idLancamento) {
        return lancamentoRepository.findById(idLancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long idUsuario) {
        BigDecimal receitas = lancamentoRepository.obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.RECEITA);
        BigDecimal despesas = lancamentoRepository.obterSaldoPorTipoLancamentoEUsuario(idUsuario, TipoLancamento.DESPESA);

        if (receitas == null)
            receitas = BigDecimal.ZERO;
        if (despesas == null)
            despesas = BigDecimal.ZERO;

        return receitas.subtract(despesas);
    }
}
