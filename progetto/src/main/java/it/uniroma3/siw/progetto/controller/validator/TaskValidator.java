package it.uniroma3.siw.progetto.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.model.Task;
import it.uniroma3.siw.progetto.service.TaskService;

@Component
public class TaskValidator implements Validator{

	@Autowired
	TaskService taskService;


	public void validateTask(Task task, Progetto progetto, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "obbligatorio");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "descrizione", "obbligatorio");
		if(this.taskService.esisteTaskInProgetto(task, progetto))
			errors.rejectValue("nome", "duplicato");
	}

	public void validateModifica(Task taskForm, Task taskCorrente, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "obbligatorio");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "descrizione", "obbligatorio");
		if(!taskCorrente.getNome().equals(taskForm.getNome())) {
			if(this.taskService.esisteTaskInProgetto(taskForm,taskCorrente.getProgetto()))
				errors.rejectValue("nome", "duplicato");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Task.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub

	}


}
