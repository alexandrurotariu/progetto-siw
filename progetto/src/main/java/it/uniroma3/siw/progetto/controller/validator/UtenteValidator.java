package it.uniroma3.siw.progetto.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import it.uniroma3.siw.progetto.model.Utente;

@Component
public class UtenteValidator implements Validator {
	

	@Override
	public void validate(Object o, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "obbligatorio");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cognome", "obbligatorio");
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Utente.class.equals(clazz);
	}

}
