package com.ratacheski.minhasfinancas.model.repository;

import com.ratacheski.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void deveVerificarAExistenciaDeUmEmail() {
        //cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        //ação
        boolean resultado = usuarioRepository.existsByEmail("usuario@teste.com");

        //verificação
        Assertions.assertThat(resultado).isTrue();
    }

    @Test
    void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {
        //ação
        boolean resultado = usuarioRepository.existsByEmail("usuario@email.com");
        //verificação
        Assertions.assertThat(resultado).isFalse();
    }

    @Test
    void devePersistirUmUsuarioNaBaseDeDados() {
        //cenario
        Usuario usuario = criarUsuario();

                //acao
                Usuario usuarioSalvo = usuarioRepository.save(usuario);

        //verificacao
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    void deveBuscarUmUsuarioPorEmail() {
        //cenario
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //acao
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail("usuario@teste.com");

        //verificacao
        Assertions.assertThat(optionalUsuario.isPresent()).isTrue();

    }

    @Test
    void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
        //acao
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail("usuario@teste.com");

        //verificacao
        Assertions.assertThat(optionalUsuario.isPresent()).isFalse();

    }

    public static Usuario criarUsuario() {
        return Usuario.builder()
                .nome("Usuario")
                .email("usuario@teste.com")
                .senha("123")
                .build();
    }
}