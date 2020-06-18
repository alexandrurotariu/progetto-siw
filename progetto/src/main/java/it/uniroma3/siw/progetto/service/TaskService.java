package it.uniroma3.siw.progetto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.model.Tag;
import it.uniroma3.siw.progetto.model.Task;
import it.uniroma3.siw.progetto.model.Utente;
import it.uniroma3.siw.progetto.repository.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Transactional
	public Task salva(Task task) {
		return this.taskRepository.save(task);
	}

	
	public Task trovaPerId(Long id) {
		Optional<Task> task = this.taskRepository.findById(id);
		return task.orElse(null);
	}

	@Transactional
	public void cancellaPerId(Long id) {
		this.taskRepository.deleteById(id);
	}
	
	
	public List<Task> trovaPerProgetto(Progetto progetto){
		return this.taskRepository.findByProgetto(progetto);
	}

	
	public List<Task> trovaPerUtenteAssegnato(Utente utente){
		return this.taskRepository.findByUtenteAssegnato(utente);
	}

	
	public List<Task> trovaPerTags(Tag tag){
		return this.taskRepository.findByTags(tag);
	}

	
	public boolean esisteTaskInProgetto(Task task, Progetto progetto) {
		List<Task> tasks = this.taskRepository.findByProgetto(progetto);
		for(Task t : tasks) {
			if(task.getNome().equals(t.getNome()))
				return true;
		}
		return false;
	}
}
