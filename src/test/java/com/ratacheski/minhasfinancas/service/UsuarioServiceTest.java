package com.ratacheski.minhasfinancas.service;

import com.ratacheski.minhasfinancas.exception.ErroAutenticacaoException;
import com.ratacheski.minhasfinancas.exception.RegraNegocioException;
import com.ratacheski.minhasfinancas.model.entity.Usuario;
import com.ratacheski.minhasfinancas.model.repository.UsuarioRepository;
import com.ratacheski.minhasfinancas.service.bean.UsuarioServiceBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UsuarioServiceTest {
    @SpyBean
    UsuarioServiceBean usuarioServiceBean;
    @MockBean
    UsuarioRepository usuarioRepository;


    @Test
    void deveValidarEmail() {
        //cenario
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        //acao
        usuarioServiceBean.validarEmail("usuario@teste.com");
    }

    @Test
    void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        //cenario
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
        //acao
        Assertions.assertThrows(RegraNegocioException.class, () -> usuarioServiceBean.validarEmail("usuario@teste.com"));
    }

    @Test
    void deveAutenticarUmUsuarioComSucesso() {
        //cenario
        String email = "email@email.com";
        String senha = "senha";
        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //acao
        Usuario result = usuarioServiceBean.autenticarUsuario(email, senha);

        //verificacao
        Assertions.assertNotNull(result);
    }

    @Test
    void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
        //cenario
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        //acao
        Assertions.
                assertEquals(Assertions.assertThrows
                        (ErroAutenticacaoException.class, () -> usuarioServiceBean.
                                autenticarUsuario("email@email.com", "senha")
                        )
                        .getMessage(), "Usuário não encontrado para o email informado.");
    }

    @Test
    void deveLancarErroQuandoSenhaNaoCorresponder() {
        //cenario
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("teste@teste.com").id(1L).senha("senha").build();
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //acao
        Assertions.
                assertEquals(Assertions.assertThrows
                        (ErroAutenticacaoException.class, () -> usuarioServiceBean.
                                autenticarUsuario("teste@teste.com", "senhaIncorreta")
                        )
                        .getMessage(), "Senha Inválida.");
    }

    @Test
    void deveSalvarUmUsuario() {
        //cenario
        Mockito.doNothing().when(usuarioServiceBean).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();
        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        //acao
        Usuario usuarioSalvo = usuarioServiceBean.salvarUsuario(new Usuario());
        //verificacao
        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(usuarioSalvo.getId(), 1L);
        Assertions.assertEquals(usuarioSalvo.getNome(), "nome");
        Assertions.assertEquals(usuarioSalvo.getEmail(), "email@email.com");
        Assertions.assertEquals(usuarioSalvo.getSenha(), "senha");
    }

    @Test
    void naoDeveSalvarUsuarioComEmailJaCadastrado() {
        //cenario
        String email = "email@existente.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(usuarioServiceBean).validarEmail(email);

        //acao
        Assertions.assertThrows(RegraNegocioException.class, () -> usuarioServiceBean.salvarUsuario(usuario));

        //verificacao
        Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);
    }
}