package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;

import br.com.caelum.leilao.dominio.Leilao;

public class EncerradorDeLeilao {

	private final RepositorioDeLeiloes dao;
	private final EnviadorDeEmail enviadorDeEmail;
	private int total = 0;

	public EncerradorDeLeilao(RepositorioDeLeiloes leilaoDao, EnviadorDeEmail enviadorDeEmail) {
		dao = leilaoDao;
		this.enviadorDeEmail = enviadorDeEmail;
	}

    public void encerra() {
		List<Leilao> todosLeiloesCorrentes = dao.correntes();

		for (Leilao leilao : todosLeiloesCorrentes) {
			try {
				if (comecouSemanaPassada(leilao)) {
					leilao.encerra();
					total++;
					dao.atualiza(leilao);
					enviadorDeEmail.envia(leilao);
				}
			} catch (Exception e){
				System.err.println(e);
			}
		}
	}

	private boolean comecouSemanaPassada(Leilao leilao) {
		return diasEntre(leilao.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalEncerrados() {
		return total;
	}
}
