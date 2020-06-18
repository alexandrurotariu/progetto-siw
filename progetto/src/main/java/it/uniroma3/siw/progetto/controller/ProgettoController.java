package it.uniroma3.siw.progetto.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.progetto.controller.sessione.DatiSessione;
import it.uniroma3.siw.progetto.controller.validator.CredenzialiValidator;
import it.uniroma3.siw.progetto.controller.validator.ProgettoValidator;
import it.uniroma3.siw.progetto.model.Credenziali;
import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.model.Tag;
import it.uniroma3.siw.progetto.model.Task;
import it.uniroma3.siw.progetto.model.Utente;
import it.uniroma3.siw.progetto.service.CredenzialiService;
import it.uniroma3.siw.progetto.service.ProgettoService;
import it.uniroma3.siw.progetto.service.TagService;
import it.uniroma3.siw.progetto.service.TaskService;
import it.uniroma3.siw.progetto.service.UtenteService;

@Controller
public class ProgettoController {

	@Autowired
	private DatiSessione datiSessione;

	@Autowired
	private ProgettoService progettoService;

	@Autowired
	private ProgettoValidator progettoValidator;

	@Autowired
	private CredenzialiValidator credenzialiValidator;

	@Autowired
	private CredenzialiService credenzialiService;
	
	@Autowired
	private UtenteService utenteService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private TagService tagService;

	public  ProgettoController() {
	}

	@RequestMapping(value = {"/home/progettiCreati"}, method = RequestMethod.GET)
	public String progettiCreati(Model model) {
		Utente utente = datiSessione.getUtenteLoggato();
		List<Progetto> progettiCreati = this.progettoService.progettiCreatiDa(utente);
		model.addAttribute("progettiCreati", progettiCreati);
		return "progettiCreati";
	}

	@RequestMapping(value = {"/home/creaProgetto"}, method = RequestMethod.GET)
	public String creaProgetto(Model model) {
		Progetto progetto = new Progetto();
		model.addAttribute("progettoForm",progetto);
		return "creaProgetto";
	}

	@RequestMapping(value = {"/home/creaProgetto"}, method = RequestMethod.POST)
	public String verificaProgetto(@Valid@ModelAttribute("progettoForm") Progetto progetto, BindingResult progettoResult,
			Model model) {
		this.progettoValidator.validate(progetto, progettoResult);
		if(!progettoResult.hasErrors()) {
			progetto.setProprietario(this.datiSessione.getUtenteLoggato());
			this.progettoService.salva(progetto);
			model.addAttribute("progetto", progetto);
			model.addAttribute("tasks", new ArrayList<>());
			model.addAttribute("tags", new ArrayList<>());
			return "progetto";
		}
		return "creaProgetto";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}"}, method = RequestMethod.GET)
	public String vediProgetto(@PathVariable Long progettoId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		model.addAttribute("progetto", progetto);
		List<Utente> utentiVisibili = this.utenteService.utentiVedonoProgetto(progetto);
		List<Credenziali> credenzialiVisibili = this.credenzialiService.trovaCredenzialiUtenti(utentiVisibili);
		model.addAttribute("credenzialiVisibili", credenzialiVisibili);
		List<Task> tasks = this.taskService.trovaPerProgetto(progetto);
		model.addAttribute("tasks", tasks);
		List<Tag> tags = this.tagService.trovaPerProgetto(progetto);
		model.addAttribute("tags", tags);
		return "progetto";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/modificaProgetto"}, method = RequestMethod.GET)
	public String vediModificaProgetto(@PathVariable Long progettoId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		model.addAttribute("progettoForm", progetto);
		return "modificaProgetto";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/modificaProgetto"}, method = RequestMethod.POST)
	public String confermaModificaProgetto(@PathVariable Long progettoId,
			@Valid@ModelAttribute("progettoForm") Progetto progetto, BindingResult progettoResult, Model model) {
		Progetto progettoCorr = this.progettoService.trovaPerId(progettoId);
		this.progettoValidator.validateNuovo(progetto, progettoCorr, progettoResult);
		if(!progettoResult.hasErrors()) {
			progettoCorr.setNome(progetto.getNome());
			this.progettoService.salva(progettoCorr);
			return "redirect:/home/progettiCreati/{progettoId}";
		}
		progetto.setId(progettoId);  //per id la pagina
		progetto.setDataInizio(progettoCorr.getDataInizio());
		return "modificaProgetto";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/cancellaProgetto"}, method = RequestMethod.GET)
	public String cancellaProgetto(@PathVariable Long progettoId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		model.addAttribute("progetto", progetto);
		return "cancellaProgetto";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/cancellaProgetto"}, method = RequestMethod.POST)
	public String confermaCancellaProgetto(@PathVariable Long progettoId, Model model) {
		this.progettoService.cancellaPerId(progettoId);
		return "redirect:/home/progettiCreati";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/condividiProgetto"}, method = RequestMethod.GET)
	public String condividiProgetto(@PathVariable Long progettoId, Model model) {
		Credenziali credenziali = new Credenziali();
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		model.addAttribute("credenzialiForm", credenziali);
		model.addAttribute("progetto", progetto);  //per id la pagina
		return "condividiProgetto";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/condividiProgetto"}, method = RequestMethod.POST)
	public String confermaCondividiProgetto(@PathVariable Long progettoId,
			@Valid@ModelAttribute("credenzialiForm") Credenziali cred, BindingResult credenzialiResult, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		List<Utente> utentiVisibili = this.utenteService.utentiVedonoProgetto(progetto);
		List<Credenziali> credenzialiVisibili = this.credenzialiService.trovaCredenzialiUtenti(utentiVisibili);
		this.credenzialiValidator.validateEsiste(cred, credenzialiResult, credenzialiVisibili);
		if(!credenzialiResult.hasErrors()) {
			Utente utente = this.credenzialiService.trovaUtentePerUsername(cred);
			this.progettoService.condividiProgetto(progetto, utente);
			return "redirect:/home/progettiCreati/{progettoId}";
		}
		model.addAttribute("progetto", progetto);  //per id della pagina
		return "condividiProgetto";
	}

	@RequestMapping(value = {"/home/progettiVisibili"}, method = RequestMethod.GET)
	public String vediProgettiVisibili(Model model) {
		Utente utente = datiSessione.getUtenteLoggato();
		List<Progetto> progettiVisibili = this.progettoService.progettiVisibiliDa(utente);
		model.addAttribute("progettiVisibili", progettiVisibili);
		return "progettiVisibili";
	}

	@RequestMapping(value = {"/home/progettiVisibili/{progettoId}"}, method = RequestMethod.GET)
	public String vediProgettoVisibile(@PathVariable Long progettoId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!this.progettoService.progettiVisibiliDa(this.datiSessione.getUtenteLoggato()).contains(progetto)) {
			return "redirect:/home";
		}
		model.addAttribute("progetto", progetto);
		//chi ha visibilità del progetto
		List<Utente> utentiVisibili = this.utenteService.utentiVedonoProgetto(progetto);
		List<Credenziali> credenzialiVisibili = this.credenzialiService.trovaCredenzialiUtenti(utentiVisibili);
		model.addAttribute("credenzialiVisibili", credenzialiVisibili);
		//chi è il proprietario
		Utente proprietario = progetto.getProprietario();
		Credenziali credProprietario = this.credenzialiService.trovaCredenzialiUtente(proprietario);
		model.addAttribute("credenzialiProprietario", credProprietario);
		//quali task mi sono stati assegnati
		List<Task> tasksAssegnatiAMe = this.taskService.trovaPerUtenteAssegnato(this.datiSessione.getUtenteLoggato());
		model.addAttribute("tasksAssegnatiAMe", tasksAssegnatiAMe);
		//gli altri task del progetto (non assegnati a me)
		List<Task> tasksTotali = this.taskService.trovaPerProgetto(progetto);
		for(Task t : tasksAssegnatiAMe) {
			if(tasksTotali.contains(t))
					tasksTotali.remove(t);
		}
		model.addAttribute("tasksAssegnatiAdAltri", tasksTotali);
		return "progettoVisibile";
	}

}
