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
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.progetto.controller.sessione.DatiSessione;
import it.uniroma3.siw.progetto.controller.validator.TaskValidator;
import it.uniroma3.siw.progetto.model.Commento;
import it.uniroma3.siw.progetto.model.Credenziali;
import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.model.Tag;
import it.uniroma3.siw.progetto.model.Task;
import it.uniroma3.siw.progetto.model.Utente;
import it.uniroma3.siw.progetto.service.CommentoService;
import it.uniroma3.siw.progetto.service.CredenzialiService;
import it.uniroma3.siw.progetto.service.ProgettoService;
import it.uniroma3.siw.progetto.service.TagService;
import it.uniroma3.siw.progetto.service.TaskService;
import it.uniroma3.siw.progetto.service.UtenteService;

@Controller
public class TaskController {

	@Autowired
	ProgettoService progettoService;

	@Autowired
	TaskValidator taskValidator;

	@Autowired
	TaskService taskService;

	@Autowired
	TagService tagService;

	@Autowired
	UtenteService utenteService;

	@Autowired
	CredenzialiService credenzialiService;

	@Autowired
	CommentoService commentoService;

	@Autowired
	DatiSessione datiSessione;

	public TaskController() {
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/creaTask"}, method = RequestMethod.GET)
	public String vediCreaTask(@PathVariable Long progettoId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		model.addAttribute("progetto", progetto);
		Task taskForm = new Task();
		model.addAttribute("taskForm", taskForm);
		return "creaTask";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/creaTask"}, method = RequestMethod.POST)
	public String creaTask(@PathVariable Long progettoId, 
			@Valid@ModelAttribute("taskForm") Task taskForm, BindingResult taskResult, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		this.taskValidator.validateTask(taskForm, progetto, taskResult);
		if(!taskResult.hasErrors()) {
			taskForm.setProgetto(progetto);
			this.taskService.salva(taskForm);
			model.addAttribute("progetto", progetto);
			model.addAttribute("task", taskForm);  //per rinominarla
			model.addAttribute("credenziali",new Credenziali());
			model.addAttribute("tags", new ArrayList<>());
			model.addAttribute("commenti", new ArrayList<>());
			return "taskCreato";
		}
		model.addAttribute("progetto", progetto);  //per id della pagina
		return "creaTask";
	}

	@RequestMapping(value = {"home/progettiCreati/{progettoId}/{taskId}/cancellaTask"}, method = RequestMethod.GET) 
	public String vediCancellaTask(@PathVariable Long progettoId, @PathVariable Long taskId, Model model) {
		Task task = this.taskService.trovaPerId(taskId);
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		model.addAttribute("task", task);  //per id della pagina
		model.addAttribute("progetto", progetto); //per id della pagina
		Credenziali credenziali = new Credenziali();
		Utente utente = task.getUtenteAssegnato();
		if(utente != null) 
			credenziali = this.credenzialiService.trovaCredenzialiUtente(utente);
		model.addAttribute("credenziali",credenziali);  //per vedere a chi è assegnato il task
		return "cancellaTask";
	}

	@RequestMapping(value = {"home/progettiCreati/{progettoId}/{taskId}/cancellaTask"}, method = RequestMethod.POST) 
	public String confermaCancellaTask(@PathVariable Long progettoId, @PathVariable Long taskId, Model model) {
		this.taskService.cancellaPerId(taskId);
		return "redirect:/home/progettiCreati/{progettoId}";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}"}, method = RequestMethod.GET)
	public String taskCreato(@PathVariable Long progettoId, @PathVariable Long taskId, Model model) {
		Task task = this.taskService.trovaPerId(taskId);
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		model.addAttribute("task", task);
		model.addAttribute("progetto", progetto);
		Credenziali credenziali = new Credenziali();
		Utente utente = task.getUtenteAssegnato();
		if(utente != null) 
			credenziali = this.credenzialiService.trovaCredenzialiUtente(utente);
		model.addAttribute("credenziali",credenziali);
		List<Tag> tags = this.tagService.trovaPerTasks(task);
		model.addAttribute("tags", tags);
		List<Commento> commenti = this.commentoService.trovaPerTask(task.getId());
		model.addAttribute("commenti", commenti);
		return "taskCreato";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}/modificaTask"}, method = RequestMethod.GET)
	public String vediModificaTask(@PathVariable Long progettoId, @PathVariable Long taskId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		Task task = this.taskService.trovaPerId(taskId);
		model.addAttribute("taskForm", task);
		model.addAttribute("progetto", progetto);  //per id della pagina
		return "modificaTask";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}/modificaTask"}, method = RequestMethod.POST)
	public String confermaModificaTask(@PathVariable Long progettoId, @Valid@ModelAttribute("taskForm") Task taskForm, 
			BindingResult taskResult, @PathVariable Long taskId, Model model) {
		Task taskCorrente = this.taskService.trovaPerId(taskId);
		this.taskValidator.validateModifica(taskForm, taskCorrente, taskResult);
		if(!taskResult.hasErrors()) {
			taskCorrente.setNome(taskForm.getNome());
			taskCorrente.setDescrizione(taskForm.getDescrizione());
			this.taskService.salva(taskCorrente);
			return "redirect:/home/progettiCreati/{progettoId}/{taskId}";
		}
		Progetto p = this.progettoService.trovaPerId(progettoId);
		taskForm.setId(taskId);  //per id della pagina
		model.addAttribute("progetto", p);  //per id della pagina
		return "modificaTask";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}/assegnaTask"}, method = RequestMethod.GET)
	public String vediAssegnaTask(@PathVariable Long progettoId, @PathVariable Long taskId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		List<Utente> utentiVisibili = this.utenteService.utentiVedonoProgetto(progetto);
		List<Credenziali> credenzialiVisibili = this.credenzialiService.trovaCredenzialiUtenti(utentiVisibili);
		model.addAttribute("credenzialiVisibili", credenzialiVisibili);
		model.addAttribute("progetto", progetto);  //per id della pagina
		model.addAttribute("task",this.taskService.trovaPerId(taskId));  //per id della pagina
		return "assegnaTask"; 
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}/assegnaTask"}, method = RequestMethod.POST)
	public String confermaAssegnaTask(@PathVariable Long progettoId, @PathVariable Long taskId, 
			@RequestParam("credenziali") Credenziali credenziali, Model model) {
		Task task = this.taskService.trovaPerId(taskId);
		if(credenziali!=null){
			//credenziali hanno solo username
			Credenziali cred = this.credenzialiService.trovaPerUsername(credenziali.getUsername());
			Utente utente = this.credenzialiService.trovaUtentePerUsername(cred);
			task.setUtenteAssegnato(utente);
			this.taskService.salva(task);
			return "redirect:/home/progettiCreati/{progettoId}/{taskId}";
		}
		else {
			task.setUtenteAssegnato(null);
			this.taskService.salva(task);
			return "redirect:/home/progettiCreati/{progettoId}/{taskId}";
		}

	}

	@RequestMapping(value = {"/home/progettiVisibili/{progettoId}/{taskId}"}, method = RequestMethod.GET)
	public String vediTaskAssegnato(@PathVariable Long progettoId, @PathVariable Long taskId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica dei permessi
		if(!this.progettoService.progettiVisibiliDa(this.datiSessione.getUtenteLoggato()).contains(progetto)) {
			return "redirect:/home";
		}
		Task task = this.taskService.trovaPerId(taskId);
		model.addAttribute("task", task);
		model.addAttribute("progetto", progetto);
		//tags associati al task
		List<Tag> tags = this.tagService.trovaPerTasks(task);
		model.addAttribute("tags", tags);
		//commenti del task divisi tra quelli miei(che posso cancellare) e quelli degli altri
		List<Commento> commentiTotali = this.commentoService.trovaPerTask(taskId);
		List<Commento> commentiMiei = new ArrayList<Commento>();
		Utente utente = this.datiSessione.getUtenteLoggato();
		//riempe la lista dei miei commenti
		for(Commento c : commentiTotali) {
			if(utente.equals(c.getUtente())) 
				commentiMiei.add(c);
		}
		//tutti gli altri commenti
		for(Commento c : commentiMiei) {
			commentiTotali.remove(c);
		}
		model.addAttribute("commentiMiei", commentiMiei);
		model.addAttribute("commentiAltri", commentiTotali);
		return "taskAssegnato";
	}

}
