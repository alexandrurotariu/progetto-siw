package it.uniroma3.siw.progetto.model;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Messaggio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, length = 200)
	private String testo;
	
	@ManyToOne
	private Utente destinatario;

	
	public Messaggio() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public Utente getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(Utente utente) {
		this.destinatario = utente;
	}

	public boolean equals(Object o) {
		Messaggio messaggio = (Messaggio)o;
		return this.id.equals(messaggio.getId());
		
	}

}
