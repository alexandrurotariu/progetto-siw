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
import it.uniroma3.siw.progetto.controller.validator.TagValidator;
import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.model.Tag;
import it.uniroma3.siw.progetto.model.Task;
import it.uniroma3.siw.progetto.service.ProgettoService;
import it.uniroma3.siw.progetto.service.TagService;
import it.uniroma3.siw.progetto.service.TaskService;

@Controller
public class TagController {

	@Autowired
	ProgettoService progettoService;

	@Autowired
	TagValidator tagValidator;

	@Autowired
	TagService tagService;

	@Autowired
	TaskService taskService;

	@Autowired 
	DatiSessione datiSessione;
	
	public TagController() {
	}

	@RequestMapping(value = "/home/progettiCreati/{progettoId}/creaTag", method = RequestMethod.GET)
	public String vediCreaTag(@PathVariable Long progettoId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		Tag tag = new Tag();
		model.addAttribute("tagForm", tag);
		model.addAttribute("progetto", progetto);  //per id della pagina
		return "creaTag";
	}

	@RequestMapping(value = "/home/progettiCreati/{progettoId}/creaTag", method = RequestMethod.POST)
	public String confermaCreaTag(@Valid@ModelAttribute("tagForm") Tag tagForm, BindingResult tagResult,
			@PathVariable Long progettoId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		model.addAttribute("progetto", progetto);  //per id della pagina
		this.tagValidator.validate(tagForm, tagResult);
		if(!tagResult.hasErrors()) {
			tagForm.setProgetto(progetto);
			this.tagService.salva(tagForm);
			model.addAttribute("tag", tagForm);  //per rinominarlo	
			model.addAttribute("tasks", new ArrayList<>());  //inizialmente non ha alcun task associato
			return "tagCreato";
		}
		return "creaTag";
	}

	@RequestMapping(value = "/home/progettiCreati/{progettoId}/tag{tagId}", method = RequestMethod.GET)
	public String vediTagCreato(@PathVariable Long progettoId, @PathVariable Long tagId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		Tag tag = this.tagService.trovaPerId(tagId);
		model.addAttribute("tag", tag);  
		model.addAttribute("progetto", progetto); //per id della pagina
		List<Task> tasks = this.taskService.trovaPerTags(tag);
		model.addAttribute("tasks", tasks);
		return "tagCreato";
	}

	@RequestMapping(value = "/home/progettiCreati/{progettoId}/{tagId}/modificaTag" , method = RequestMethod.GET)
	public String vediModificaTag(@PathVariable Long progettoId, @PathVariable Long tagId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		Tag tagForm = this.tagService.trovaPerId(tagId);
		model.addAttribute("tagForm", tagForm);
		model.addAttribute("progetto", progetto);  //per id della pagina
		return "modificaTag";
	}

	@RequestMapping(value = "/home/progettiCreati/{progettoId}/{tagId}/modificaTag" , method = RequestMethod.POST)
	public String confermaModificaTag(@Valid@ModelAttribute("tagForm") Tag tagForm, BindingResult tagResult,
			@PathVariable Long progettoId, @PathVariable Long tagId, Model model) {
		Tag tagCorrente = this.tagService.trovaPerId(tagId);
		this.tagValidator.validate(tagForm, tagResult);
		if(!tagResult.hasErrors()) {
			tagCorrente.setNome(tagForm.getNome());
			tagCorrente.setColore(tagForm.getColore());
			tagCorrente.setDescrizione(tagForm.getDescrizione());
			this.tagService.salva(tagCorrente);
			return "redirect:/home/progettiCreati/{progettoId}/tag{tagId}";
		}
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		model.addAttribute("progetto", progetto);  //per id della pagina
		return "modificaTag";
	}

	@RequestMapping(value = "/home/progettiCreati/{progettoId}/{tagId}/cancellaTag", method = RequestMethod.GET)
	public String vediCancellaTag(@PathVariable Long progettoId, @PathVariable Long tagId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		Tag tag = this.tagService.trovaPerId(tagId);
		model.addAttribute("tag", tag);
		model.addAttribute("progetto", progetto);  //per id della pagina
		return "cancellaTag";
	}

	@RequestMapping(value = "/home/progettiCreati/{progettoId}/{tagId}/cancellaTag", method = RequestMethod.POST)
	public String confermaCancellaTag(@PathVariable Long progettoId, @PathVariable Long tagId, Model model) {	
		Tag tag = this.tagService.trovaPerId(tagId);
		List<Task> tasks = this.taskService.trovaPerTags(tag);
		for(Task t: tasks) {  //cancello da tutti i task che usano quel tag, il tag stesso
			t.deleteTags(tag);
			this.taskService.salva(t);
		}
		this.tagService.cancellaPerId(tagId);  //infine posso cancellare il tag
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		model.addAttribute("progetto", progetto);
		return "redirect:/home/progettiCreati/{progettoId}";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}/aggiungiTagAlTask"}, method = RequestMethod.GET)
	public String vediAggiungiTagAlTask(@PathVariable Long progettoId, @PathVariable Long taskId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		Task task = this.taskService.trovaPerId(taskId);
		List<Tag> tagsDelProgetto = this.tagService.trovaPerProgetto(progetto);
		List<Tag> tagsDelTask = this.tagService.trovaPerTasks(task);
		model.addAttribute("progetto", progetto);  //per id della pagina
		model.addAttribute("task", task);
		for(Tag t: tagsDelTask) {  //mostrare solo i tag non ancora aggiunti al task
			if(tagsDelProgetto.contains(t))
				tagsDelProgetto.remove(t);
		}
		model.addAttribute("tagsDelProgetto", tagsDelProgetto);
		return "aggiungiTagAlTask";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}/aggiungiTagAlTask"}, method = RequestMethod.POST)
	public String confermaAggiungiTagAlTask(@RequestParam("tags") List<Tag> tags, @PathVariable Long progettoId, 
			@PathVariable Long taskId, Model model) {
		Task task = this.taskService.trovaPerId(taskId);
		//assegna il tag al task
		for(Tag t : tags) {
			task.addTags(t);
		}
		this.taskService.salva(task);
		return"redirect:/home/progettiCreati/{progettoId}/{taskId}";
	}

	@RequestMapping(value = {"/home/progettiCreati/{progettoId}/{taskId}/{tagId}/cancellaTagDalTask"}, method = RequestMethod.GET)
	public String cancellaTagDalTask(@PathVariable Long progettoId, @PathVariable Long taskId, 
			@PathVariable Long tagId, Model model) {
		Progetto progetto = this.progettoService.trovaPerId(progettoId);
		//verifica i permessi
		if(!progetto.getProprietario().equals(this.datiSessione.getUtenteLoggato())) {
			return "redirect:/home";
		}
		Tag tag = this.tagService.trovaPerId(tagId);
		Task task = this.taskService.trovaPerId(taskId);
		task.deleteTags(tag);
		this.taskService.salva(task);
		return"redirect:/home/progettiCreati/{progettoId}/{taskId}";	
	}

}
