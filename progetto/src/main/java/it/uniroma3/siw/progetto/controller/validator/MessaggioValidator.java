package it.uniroma3.siw.progetto.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import it.uniroma3.siw.progetto.model.Messaggio;

@Component
public class MessaggioValidator implements Validator{


	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "testo", "obbligatorio");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Messaggio.class.equals(clazz);
	}

	
}
