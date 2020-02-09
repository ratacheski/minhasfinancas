package com.ratacheski.minhasfinancas.model.repository;

import com.ratacheski.minhasfinancas.model.entity.Lancamento;
import com.ratacheski.minhasfinancas.model.enums.StatusLancamento;
import com.ratacheski.minhasfinancas.model.enums.TipoLancamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void deveSalvarUmLancamento() {
        Lancamento lancamento = criarLancamento();
        lancamento = lancamentoRepository.save(lancamento);
        assertNotNull(lancamento.getId());
    }

    @Test
    void deveDeletarUmLancamento() {
        Lancamento lancamento = criarEPersistirLancamento();
        entityManager.find(Lancamento.class, lancamento.getId());
        lancamentoRepository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        assertNull(lancamentoInexistente);
    }

    @Test
    void deveAtualizarUmLancamento() {
        Lancamento lancamento = criarEPersistirLancamento();
        lancamento.setDescricao("Teste Atualizado");
        lancamento.setAno(2019);
        lancamento.setStatusLancamento(StatusLancamento.CANCELADO);

        lancamentoRepository.save(lancamento);
        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        assertEquals(lancamentoAtualizado.getAno(), 2019);
        assertEquals(lancamentoAtualizado.getDescricao(), "Teste Atualizado");
        assertEquals(lancamentoAtualizado.getStatusLancamento(), StatusLancamento.CANCELADO);
    }

    @Test
    void deveBuscarUmLancamentoPorId() {
        Lancamento lancamento = criarEPersistirLancamento();
        Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());
        assertTrue(lancamentoEncontrado.isPresent());
    }

    private Lancamento criarEPersistirLancamento() {
        Lancamento lancamento = criarLancamento();
        lancamento = entityManager.persist(lancamento);
        return lancamento;
    }

    public static Lancamento criarLancamento(){
        return Lancamento.builder()
                .ano(2020)
                .mes(1)
                .descricao("Lancamento Qualquer")
                .valor(BigDecimal.TEN)
                .tipoLancamento(TipoLancamento.RECEITA)
                .statusLancamento(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
}