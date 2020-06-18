package it.uniroma3.siw.progetto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.uniroma3.siw.progetto.model.Credenziali;
import it.uniroma3.siw.progetto.model.Utente;
import it.uniroma3.siw.progetto.repository.CredenzialiRepository;
import it.uniroma3.siw.progetto.repository.ProgettoRepository;
import it.uniroma3.siw.progetto.repository.UtenteRepository;
import it.uniroma3.siw.progetto.service.CredenzialiService;
import it.uniroma3.siw.progetto.service.ProgettoService;
import it.uniroma3.siw.progetto.service.UtenteService;

@SpringBootTest
@RunWith(SpringRunner.class)
class ProgettoApplicationTests {

	@Autowired
	CredenzialiService credenzialiService;
	
	@Autowired
	UtenteService utenteService;
	
	@Autowired
	ProgettoService progettoService;
	
	@Autowired
	CredenzialiRepository credenzialiRepository;
	
	@Autowired
	ProgettoRepository progettoRepository;
	
	@Autowired
	UtenteRepository utenteRepository;

	
	@BeforeEach
	public void remove() {
		this.credenzialiRepository.deleteAll();
	}
	
	@Test
	void test1() {
		Utente utente1 = new Utente("mario", "rossi");
		Credenziali credenziali1 = new Credenziali("mariorossi", "123");
		credenziali1.setUtente(utente1);
		this.credenzialiService.salva(credenziali1);
		assertEquals(credenziali1, this.credenzialiService.tutteCredenziali().get(0));
		assertEquals(1, this.utenteRepository.count());
		
		//Utente utente1agg = utente1;
		utente1.setNome("maria");
		this.utenteService.salva(utente1);
		assertEquals("maria", this.credenzialiService.trovaPerId(credenziali1.getId()).getUtente().getNome());
		assertEquals(utente1.getId(), this.credenzialiService.trovaPerId(credenziali1.getId()).getUtente().getId());
		assertEquals(1, this.utenteRepository.count());
		assertEquals(1, this.credenzialiRepository.count());
		
		credenziali1.setUsername("mariarossi");
		utente1.setCognome("verdi");
		this.credenzialiService.salva(credenziali1);
		assertEquals("mariarossi", this.credenzialiService.trovaPerId(credenziali1.getId()).getUsername());
		assertEquals("verdi", this.credenzialiService.trovaPerId(credenziali1.getId()).getUtente().getCognome());
		assertEquals(1, this.utenteRepository.count());
		assertEquals(1, this.credenzialiRepository.count());
	}
	
	

}
