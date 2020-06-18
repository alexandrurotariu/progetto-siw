package it.uniroma3.siw.progetto.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.uniroma3.siw.progetto.service.ProgettoService;
import it.uniroma3.siw.progetto.service.UtenteService;

import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class ProgettoTest {

	@Autowired
	ProgettoService progettoService;

	@Autowired
	UtenteService utenteService;

	@BeforeEach

	@Test
	void testVediProgetti() {
		Utente utente1 = new Utente("mario", "rossi");
		Progetto progetto1 = new Progetto("progetto1");
		Progetto progetto2 = new Progetto("progetto2");
		progetto1.setProprietario(utente1);
		progetto2.setProprietario(utente1);
		this.utenteService.salva(utente1);
		this.progettoService.salva(progetto1);
		this.progettoService.salva(progetto2);
		assertEquals(2,this.progettoService.progettiCreatiDa(utente1).size());
	}

	@Test
	void testProgettiCondivisi(){
		Utente utente1 = new Utente("mario", "rossi");
		this.utenteService.salva(utente1);
		Utente utente2 = new Utente("paolo", "bianchi");
		this.utenteService.salva(utente2);
		Progetto p = new Progetto("progetto1");
		p.setProprietario(utente2);
		this.progettoService.salva(p);
		this.progettoService.condividiProgetto(p, utente1);
		assertEquals(1, progettoService.progettiVisibiliDa(utente1).size());
		assertEquals(p, progettoService.progettiVisibiliDa(utente1).get(0));
		assertEquals(0, progettoService.progettiVisibiliDa(utente2).size());


	}

}
