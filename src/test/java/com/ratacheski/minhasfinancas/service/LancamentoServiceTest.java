package com.ratacheski.minhasfinancas.service;

import com.ratacheski.minhasfinancas.exception.RegraNegocioException;
import com.ratacheski.minhasfinancas.model.entity.Lancamento;
import com.ratacheski.minhasfinancas.model.entity.Usuario;
import com.ratacheski.minhasfinancas.model.enums.StatusLancamento;
import com.ratacheski.minhasfinancas.model.repository.LancamentoRepository;
import com.ratacheski.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.ratacheski.minhasfinancas.service.bean.LancamentoServiceBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceBean lancamentoServiceBean;

    @MockBean
    LancamentoRepository lancamentoRepository;

    @Test
    void deveSalvarUmLancamento() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(lancamentoServiceBean).validarLancamento(lancamentoASalvar);
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatusLancamento(StatusLancamento.PENDENTE);
        Mockito.when(lancamentoRepository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = lancamentoServiceBean.salvarLancamento(lancamentoASalvar);
        assertEquals(lancamento.getId(), lancamentoSalvo.getId());
        assertEquals(lancamento.getStatusLancamento(), StatusLancamento.PENDENTE);
    }

    @Test
    void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(lancamentoServiceBean).validarLancamento(lancamentoASalvar);

        assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.salvarLancamento(lancamentoASalvar));
        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    void deveAtualizarUmLancamento() {
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatusLancamento(StatusLancamento.PENDENTE);
        Mockito.doNothing().when(lancamentoServiceBean).validarLancamento(lancamentoSalvo);
        Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        lancamentoServiceBean.atualizarLancamento(lancamentoSalvo);
        Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    void deveLancarErroAoAtualizarLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        assertThrows(NullPointerException.class, () -> lancamentoServiceBean.atualizarLancamento(lancamento));
        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamento);
    }

    @Test
    void deveDeletarUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        lancamentoServiceBean.removerLancamento(lancamento);

        Mockito.verify(lancamentoRepository).delete(lancamento);
    }

    @Test
    void deveLancarErroAoDeletarLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        assertThrows(NullPointerException.class, () -> lancamentoServiceBean.removerLancamento(lancamento));

        Mockito.verify(lancamentoRepository, Mockito.never()).delete(lancamento);
    }

    @Test
    void deveFiltrarLancamentos() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lancamentos = Arrays.asList(lancamento);
        Mockito.when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(lancamentos);
        List<Lancamento> resultado = lancamentoServiceBean.buscarLancamentos(lancamento);

        assertFalse(resultado.isEmpty());
        assertEquals(resultado.size(),1);
        assertTrue(resultado.contains(lancamento));
    }

    @Test
    void deveAtualizarStatusDoLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(lancamentoServiceBean).atualizarLancamento(lancamento);

        lancamentoServiceBean.atualizarStatusLancamento(lancamento, novoStatus);
        assertEquals(lancamento.getStatusLancamento(), novoStatus);
        Mockito.verify(lancamentoServiceBean).atualizarLancamento(lancamento);
    }

    @Test
    void deveObterUmLancamentoPorId() {
        Long id = 1L;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);
        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = lancamentoServiceBean.obterPorId(id);
        assertTrue(resultado.isPresent());
    }

    @Test
    void deveRetornarVazioAoBuscarUmLancamentoPorIdInexistente() {

        Long id = 1L;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);
        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = lancamentoServiceBean.obterPorId(id);
        assertFalse(resultado.isPresent());
    }

    @Test
    void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe uma descrição válida.");
        lancamento.setDescricao("");
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe uma descrição válida.");

        lancamento.setDescricao("Descricao");
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um mês válido.");
        lancamento.setMes(0);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um mês válido.");
        lancamento.setMes(13);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um mês válido.");

        lancamento.setMes(1);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um ano válido.");
        lancamento.setAno(202);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um ano válido.");

        lancamento.setAno(2020);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um usuário.");
        lancamento.setUsuario(new Usuario());
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um usuário.");

        lancamento.setUsuario(Usuario.builder().id(1L).build());
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um valor válido positivo.");
        lancamento.setValor(BigDecimal.ZERO);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um valor válido positivo.");

        lancamento.setValor(BigDecimal.TEN);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoServiceBean.validarLancamento(lancamento));
        assertEquals(exception.getMessage(), "Informe um tipo de lançamento.");
    }
}