package it.uniroma3.siw.progetto.controller.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import it.uniroma3.siw.progetto.controller.sessione.DatiSessione;
import it.uniroma3.siw.progetto.model.Progetto;
import it.uniroma3.siw.progetto.service.ProgettoService;

@Component
public class ProgettoValidator implements Validator {

	@Autowired
	ProgettoService progettoService;

	@Autowired
	DatiSessione datiSessione;

	@Override
	public void validate(Object o, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "obbligatorio");
		if(this.progettoService.esisteProgettoNome((Progetto)o,datiSessione.getUtenteLoggato()))
			errors.rejectValue("nome", "duplicato");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Progetto.class.equals(clazz);
	}

	public void validateNuovo(Progetto progetto, Progetto progettoCorr, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "obbligatorio");
		if(!progetto.getNome().equals(progettoCorr.getNome())) {
			if(this.progettoService.esisteProgettoNome(progetto, datiSessione.getUtenteLoggato()))
				errors.rejectValue("nome", "duplicato");
		}
	}

}
