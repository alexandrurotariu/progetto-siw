package it.uniroma3.siw.progetto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.model.Utente;
import it.uniroma3.siw.progetto.repository.ProgettoRepository;

@Service
public class ProgettoService {

	@Autowired
	private ProgettoRepository progettoRepository;

	@Transactional
	public Progetto salva(Progetto progetto) {
		return this.progettoRepository.save(progetto);
	}

	@Transactional
	public void cancellaPerId(Long id) {
		this.progettoRepository.deleteById(id);
	}
	
	
	public List<Progetto> progettiCreatiDa(Utente utente) {
		return this.progettoRepository.findByProprietario(utente);
	}
	
	
	public List<Progetto> progettiVisibiliDa(Utente utente) {
		return this.progettoRepository.findByUtentiVisibili(utente);
	}

	
	public boolean esisteProgettoNome(Progetto progetto, Utente utente) {
		List<Progetto> progetti = this.progettiCreatiDa(utente);
		for(Progetto p : progetti) {
			if(p.getNome().equals(progetto.getNome()))
				return true;
		}
		return false;
	}

	
	public Progetto trovaPerId(Long id) {
		Optional<Progetto> progetto = this.progettoRepository.findById(id);
		return progetto.orElse(null);
	}
	
	@Transactional
	public Progetto condividiProgetto(Progetto progetto, Utente utente) {
		progetto.addUtentiVisibili(utente);
		return this.salva(progetto);
	}
	

}
