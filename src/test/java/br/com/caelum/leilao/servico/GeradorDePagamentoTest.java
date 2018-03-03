package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeradorDePagamentoTest {

    @Mock
    RepositorioDeLeiloes repositorioDeLeiloes;

    @Mock
    RepositorioDePagamentos repositorioDePagamentos;

    Avaliador avaliador = new Avaliador();

    @Test
    public void deveGerarPagamento(){

        ArgumentCaptor<Pagamento> capturadorDeArgumento = ArgumentCaptor.forClass(Pagamento.class);

        Leilao leilao = new CriadorDeLeilao()
                .para("Playtation 4 Plus")
                .lance(new Usuario("Jeanzinho do Ingá"), 2500.00)
                .lance(new Usuario("Olívinha do Ká"), 3500.00)
                .constroi();

        when(repositorioDeLeiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        GeradorDePagamento geradorDePagamento = new GeradorDePagamento(repositorioDeLeiloes, repositorioDePagamentos, avaliador);
        geradorDePagamento.gerar();

        verify(repositorioDePagamentos).salvar(capturadorDeArgumento.capture());

        Pagamento pagamentoSalvo = capturadorDeArgumento.getValue();
        assertEquals(3500.00, pagamentoSalvo.getValor(), 0.001);
    }


}
