package it.uniroma3.siw.progetto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.progetto.model.Credenziali;
import it.uniroma3.siw.progetto.model.Utente;
import it.uniroma3.siw.progetto.repository.CredenzialiRepository;

@Service
public class CredenzialiService {

	@Autowired
	protected CredenzialiRepository credenzialiRepository;
	
	@Transactional
	public Credenziali salva(Credenziali credenziali) {
		return this.credenzialiRepository.save(credenziali);
	}
	
	
	public Credenziali trovaPerUsername(String username) {
		Optional <Credenziali> credenziali = this.credenzialiRepository.findByUsername(username);
		return credenziali.orElse(null);
	}
	
	
	public boolean esistonoCredenziali(Credenziali cred) {
		Credenziali credenziali = this.trovaPerUsername(cred.getUsername());
		if(credenziali!=null)
			return true;
		return false;
	}
	
	@Transactional
	public void cancellaPerId(Long id) {
		this.credenzialiRepository.deleteById(id);
	}
	
	
	public List<Credenziali> tutteCredenziali(){
		List<Credenziali> credenziali = new ArrayList<>();
		Iterable<Credenziali> iterable = this.credenzialiRepository.findAll();
		for(Credenziali c : iterable)
			credenziali.add(c);
		return  credenziali;
	}
	
	
	public Credenziali trovaPerId(Long id) {
		Optional<Credenziali> credenziali = this.credenzialiRepository.findById(id);
		return credenziali.orElse(null);
	}
	
	
	public Utente trovaUtentePerUsername(Credenziali cred) {
		Credenziali credenziali = this.trovaPerUsername(cred.getUsername());
		if(credenziali!=null)
			return credenziali.getUtente();
		else 
			return null;
	}
	
	
	public Credenziali trovaCredenzialiUtente(Utente utente) {
		Optional<Credenziali> cred = this.credenzialiRepository.findByUtente(utente);
		return cred.orElse(null);
	}
	
	
	public List<Credenziali> trovaCredenzialiUtenti(List<Utente> utenti){
		List<Credenziali> credenziali = new ArrayList<>();
		for(Utente utente : utenti) {
			Credenziali cred = this.trovaCredenzialiUtente(utente);
			if(cred != null)
				credenziali.add(cred);
		}
		return credenziali;
	}
		
}
