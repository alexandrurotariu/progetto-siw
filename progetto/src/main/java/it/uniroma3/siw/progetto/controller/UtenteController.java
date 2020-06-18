package it.uniroma3.siw.progetto.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.progetto.controller.sessione.DatiSessione;
import it.uniroma3.siw.progetto.controller.validator.CredenzialiValidator;
import it.uniroma3.siw.progetto.controller.validator.MessaggioValidator;
import it.uniroma3.siw.progetto.controller.validator.UtenteValidator;
import it.uniroma3.siw.progetto.model.Credenziali;
import it.uniroma3.siw.progetto.model.Messaggio;
import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.model.Task;
import it.uniroma3.siw.progetto.model.Utente;
import it.uniroma3.siw.progetto.service.CommentoService;
import it.uniroma3.siw.progetto.service.CredenzialiService;
import it.uniroma3.siw.progetto.service.MessaggioService;
import it.uniroma3.siw.progetto.service.ProgettoService;
import it.uniroma3.siw.progetto.service.TaskService;


@Controller
public class UtenteController {

	@Autowired
	private DatiSessione datiSessione;

	@Autowired
	private UtenteValidator utenteValidator;

	@Autowired
	private CredenzialiValidator credenzialiValidator;

	@Autowired
	private CredenzialiService credenzialiService;

	@Autowired
	private ProgettoService progettoService;

	@Autowired
	private CommentoService commentoService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private MessaggioValidator messaggioValidator;

	@Autowired
	private MessaggioService messaggioService;
	
	@Autowired
	protected PasswordEncoder passwordEncoder;

	//@Autowired
	//private OAuth2AuthorizedClientService authorizedClientService;

	public UtenteController() {
	}

	@RequestMapping(value = {"/home"}, method = RequestMethod.GET)
	public String home(Model model /*OAuth2AuthenticationToken authentication*/) {
		/*OAuth2AuthorizedClient client = authorizedClientService
			      .loadAuthorizedClient(
			        authentication.getAuthorizedClientRegistrationId(), 
			          authentication.getName());*/
		Utente utente = datiSessione.getUtenteLoggato();

		//model.addAttribute("utente", client.getPrincipalName());
		model.addAttribute("utente", utente);
		return "home";
	}

	@RequestMapping(value = {"/admin"}, method = RequestMethod.GET)
	public String admin(Model model) {
		Utente utente = datiSessione.getUtenteLoggato();
		model.addAttribute("utente", utente);
		//stampa lista di tutti gli utenti (tranne di me stesso)
		List<Credenziali> credenziali = this.credenzialiService.tutteCredenziali();
		credenziali.remove(this.datiSessione.getCredenzialiLoggate());
		model.addAttribute("credenziali", credenziali);
		return "admin";
	}

	@RequestMapping(value = {"/home/profilo"}, method = RequestMethod.GET)
	public String vediProfilo(Model model) {
		Utente utente = datiSessione.getUtenteLoggato();
		Credenziali credenziali = datiSessione.getCredenzialiLoggate();
		model.addAttribute("utente", utente);
		model.addAttribute("credenziali", credenziali);
		//stampa anche i messaggi ricevuti dagli admin
		List<Messaggio> messaggi = this.messaggioService.trovaPerDestinatario(utente);
		model.addAttribute("messaggi", messaggi);
		return "profilo";
	}

	@RequestMapping(value = {"/home/profilo/modificaProfilo"}, method = RequestMethod.GET)
	public String vediModificaProfilo(Model model) {
		Utente utenteForm = datiSessione.getUtenteLoggato();
		Credenziali credenzialiForm = datiSessione.getCredenzialiLoggate();
		model.addAttribute("utenteForm", utenteForm);
		model.addAttribute("credenzialiForm", credenzialiForm);
		return "modificaProfilo";
	}

	@RequestMapping(value = {"/home/profilo/modificaProfilo"}, method = RequestMethod.POST)
	public String salvaModificaProfilo(@Valid@ModelAttribute("utenteForm") Utente utente, BindingResult utenteResult,
			@Valid@ModelAttribute("credenzialiForm") Credenziali cred, BindingResult credResult, Model model) {
		this.utenteValidator.validate(utente, utenteResult);
		this.credenzialiValidator.validateNuovo(cred, credResult);
		if(!utenteResult.hasErrors() && !credResult.hasErrors()) {
			Credenziali credCorrenti = this.datiSessione.getCredenzialiLoggate();
			Utente utenteCorrente = this.datiSessione.getUtenteLoggato();
			credCorrenti.setUsername(cred.getUsername());
			credCorrenti.setPassword(this.passwordEncoder.encode(cred.getPassword()));
			utenteCorrente.setNome(utente.getNome());
			utenteCorrente.setCognome(utente.getCognome());
			credCorrenti.setPassword(this.passwordEncoder.encode(cred.getPassword()));
			this.credenzialiService.salva(credCorrenti);
			model.addAttribute("utente", utenteCorrente);
			model.addAttribute("credenziali", credCorrenti);
			return "profilo";
		}
		return "modificaProfilo";
	}

	@RequestMapping(value = {"/admin/credenziali{credId}"}, method = RequestMethod.GET)
	public String vediUtente(@PathVariable Long credId, Model model) {
		Credenziali credenziali = this.credenzialiService.trovaPerId(credId);
		model.addAttribute("credenziali", credenziali);
		Utente utente = credenziali.getUtente();
		List<Progetto> progetti = this.progettoService.progettiCreatiDa(utente);
		model.addAttribute("progetti", progetti);
		model.addAttribute("messaggioForm", new Messaggio());
		return "utente";
	}

	@RequestMapping(value = {"/admin/credenziali{credId}/cancellaUtente"}, method = RequestMethod.GET)
	public String vediCancellaUtente(@PathVariable Long credId, Model model) {
		Credenziali credenziali = this.credenzialiService.trovaPerId(credId);
		model.addAttribute("credenziali", credenziali);
		return "cancellaUtente";
	}

	@RequestMapping(value = {"/admin/credenziali{credId}/cancellaUtente"}, method = RequestMethod.POST)
	public String confermaCancellaUtente(@PathVariable Long credId, Model model) {
		Credenziali credenziali = this.credenzialiService.trovaPerId(credId);
		Utente utente = credenziali.getUtente();
		//cancella tutti i commenti scritti dall'utente
		this.commentoService.cancellaPerUtente(utente);
		//rimuovi l'utente dai task che gli sono stati assegnati
		List<Task> tasks = this.taskService.trovaPerUtenteAssegnato(utente);
		for(Task t : tasks) {
			t.setUtenteAssegnato(null);
			this.taskService.salva(t);
		}
		//cancella l'utente da tutte le liste di utenti visibili di progetti condivisi con lui
		List<Progetto> progettiVisibili = this.progettoService.progettiVisibiliDa(utente);
		for(Progetto p : progettiVisibili) {
			p.getUtentiVisibili().remove(utente);
			this.progettoService.salva(p);
		}
		//cancella le credenziali
		this.credenzialiService.cancellaPerId(credId);
		return "redirect:/admin";
	}

	@RequestMapping(value = {"/admin/credenziali{credId}/modificaRuolo"}, method = RequestMethod.POST)
	public String vediModificaRuolo(@RequestParam("ruolo") String ruolo, @PathVariable Long credId, Model model) {
		Credenziali credenziali = this.credenzialiService.trovaPerId(credId);
		credenziali.setRuolo(ruolo);
		this.credenzialiService.salva(credenziali);
		return "redirect:/admin/credenziali{credId}";
	}

	@RequestMapping(value = {"/admin/credenziali{credId}/{progettoId}/cancellaProgetto"}, method = RequestMethod.GET)
	public String vediCancellaProgettoDaAdmin(@PathVariable Long credId, @PathVariable Long progettoId, Model model) {
		Credenziali credenziali = this.credenzialiService.trovaPerId(credId);
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		model.addAttribute("credenziali", credenziali);
		model.addAttribute("progetto", progetto);
		return "cancellaProgettoDaAdmin";
	}

	@RequestMapping(value = {"/admin/credenziali{credId}/{progettoId}/cancellaProgetto"}, method = RequestMethod.POST)
	public String confermaCancellaProgettoDaAdmin(@PathVariable Long credId, @PathVariable Long progettoId, Model model) {
		this.progettoService.cancellaPerId(progettoId);
		return "redirect:/admin/credenziali{credId}";
	}

	@RequestMapping(value = {"/admin/credenziali{credId}/creaMessaggio"}, method = RequestMethod.POST)
	public String creaMessaggio(@Valid@ModelAttribute("messaggioForm") Messaggio messaggioForm, BindingResult messaggioResult,
			@PathVariable Long credId, Model model) {
		Credenziali credenziali = this.credenzialiService.trovaPerId(credId);
		this.messaggioValidator.validate(messaggioForm, messaggioResult);
		if(!messaggioResult.hasErrors()) {
			messaggioForm.setDestinatario(credenziali.getUtente());
			this.messaggioService.salva(messaggioForm);
			return "redirect:/admin/credenziali{credId}";
		}
		model.addAttribute("credenziali", credenziali);
		return "utente";
	}
}
