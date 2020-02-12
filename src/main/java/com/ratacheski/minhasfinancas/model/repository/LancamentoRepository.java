package com.ratacheski.minhasfinancas.model.repository;

import com.ratacheski.minhasfinancas.model.entity.Lancamento;
import com.ratacheski.minhasfinancas.model.enums.StatusLancamento;
import com.ratacheski.minhasfinancas.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = "select sum(l.valor) from Lancamento l join l.usuario u " +
            "where u.id = :idUsuario and l.tipoLancamento = :tipoLancamento and l.statusLancamento = :statusLancamento " +
            "group by u")
    BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
            @Param("idUsuario") Long idUsuario,
            @Param("tipoLancamento") TipoLancamento tipoLancamento,
            @Param("statusLancamento") StatusLancamento statusLancamento
    );
}
