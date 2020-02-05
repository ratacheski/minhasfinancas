package com.ratacheski.minhasfinancas.model.repository;

import com.ratacheski.minhasfinancas.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository <Lancamento,Long> {
}
