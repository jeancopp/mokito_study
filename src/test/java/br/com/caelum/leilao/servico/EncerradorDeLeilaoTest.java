package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EncerradorDeLeilaoTest {

    final CriadorDeLeilao criadorDeLeilao = new CriadorDeLeilao();
    Calendar dataAntiga;
    Calendar ontem;

    @Before
    public void mock(){
        dataAntiga = Calendar.getInstance();
        dataAntiga.set(1999, 07,26);

        ontem = Calendar.getInstance();
        ontem.add(Calendar.DATE, -1);
    }

    @Mock
    RepositorioDeLeilao leilaoDao;

    @Mock 
    EnviadorDeEmail enviadorDeEmail;
    @Test
    public void deveEncerrarLeilaoCorretamente(){
        Leilao leilao1 = criadorDeLeilao.naData(dataAntiga).para("Leilão 1").constroi();
        Leilao leilao2 = criadorDeLeilao.naData(dataAntiga).para("Leilão 2").constroi();
        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        when(leilaoDao.correntes()).thenReturn(leiloesAntigos);
        EncerradorDeLeilao encerrador = criarEncerrador();
        encerrador.encerra();

        assertEquals(2, encerrador.getTotalEncerrados());
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }

    @Test
    public void  naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {
        Leilao leilao1 = criadorDeLeilao.naData(ontem).para("Leilão 1").constroi();
        Leilao leilao2 = criadorDeLeilao.naData(ontem).para("Leilão 2").constroi();
        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        when(leilaoDao.correntes()).thenReturn(leiloesAntigos);
        EncerradorDeLeilao encerrador = criarEncerrador();
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());

        verify(leilaoDao, never()).atualiza(leilao1);
        verify(leilaoDao, never()).atualiza(leilao2);
    }

    @Test
    public void encerradorDeveNaoRealizarNada(){
        when(leilaoDao.correntes()).thenReturn(new ArrayList<>());
        EncerradorDeLeilao encerrador = criarEncerrador();
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
    }

    @Test
    public void deveAtualizarLeiloesEncerrados(){
        Leilao leilao1 = criadorDeLeilao.naData(dataAntiga).para("Leilão 1").constroi();
        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1));
        EncerradorDeLeilao encerrador = criarEncerrador();

        encerrador.encerra();
        verify(leilaoDao, times(1)).atualiza(leilao1);
    }

    @Test
    public void verificaSeMetodosSaoChamadosEmOrdemCorreta(){
        Leilao leilao1 = criadorDeLeilao.naData(dataAntiga).para("Leilão 1").constroi();
        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1));
        InOrder inOrder = inOrder(leilaoDao, enviadorDeEmail);

        EncerradorDeLeilao encerrador = criarEncerrador();

        encerrador.encerra();

        verify(leilaoDao, times(1)).atualiza(leilao1);
        inOrder.verify(leilaoDao, times(1)).atualiza(leilao1);
        inOrder.verify(enviadorDeEmail, times(1)).envia(leilao1);

    }


    private EncerradorDeLeilao criarEncerrador() {
        return new EncerradorDeLeilao(leilaoDao, enviadorDeEmail);
    }

}
