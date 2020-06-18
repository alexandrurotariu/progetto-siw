package it.uniroma3.siw.progetto.controller.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import it.uniroma3.siw.progetto.controller.sessione.DatiSessione;
import it.uniroma3.siw.progetto.model.Credenziali;
import it.uniroma3.siw.progetto.service.CredenzialiService;

@Component
public class CredenzialiValidator implements Validator {

	@Autowired
	private CredenzialiService credenzialiService;

	@Autowired
	DatiSessione datiSessione;

	@Override
	public void validate(Object o, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "obbligatorio");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "obbligatorio");
		if(this.credenzialiService.esistonoCredenziali((Credenziali)o))
			errors.rejectValue("username", "duplicato");
	}

	public void validateNuovo(Credenziali credNuove, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "obbligatorio");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "necessario");
		if(!this.datiSessione.getCredenzialiLoggate().getUsername().equals(credNuove.getUsername())){
			if(this.credenzialiService.esistonoCredenziali(credNuove)) 
				errors.rejectValue("username", "duplicato");
		}
	}

	//per condividere
	public void validateEsiste(Credenziali credenziali, Errors errors, List<Credenziali> tutteCredenziali) {
		Credenziali complete = credenzialiService.trovaPerUsername(credenziali.getUsername());
		if(credenziali.getUsername().trim().isEmpty())
			errors.rejectValue("username", "obbligatorio");
		else if(this.datiSessione.getCredenzialiLoggate().getUsername().equals(credenziali.getUsername()))
			errors.rejectValue("username", "impossibile");
		else if(tutteCredenziali.contains(complete))
			errors.rejectValue("username", "giausato");
		else if(!credenzialiService.esistonoCredenziali(credenziali))
			errors.rejectValue("username", "inesistente");

	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Credenziali.class.equals(clazz);
	}

}
