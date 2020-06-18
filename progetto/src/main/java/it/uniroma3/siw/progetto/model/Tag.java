package it.uniroma3.siw.progetto.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(nullable = false, length = 50)
	private String colore;

	@Column(nullable = false, length = 200)
	private String descrizione;
	
	@ManyToMany(mappedBy = "tags")
	private List<Task> tasks;
	
	@ManyToOne
	private Progetto progetto;

	public Tag() {
		this.tasks = new ArrayList<Task>();
	}

	public Tag(String nome, String colore, String descrizione, Progetto progetto) {
		this();
		this.nome = nome;
		this.colore = colore;
		this.descrizione = descrizione;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getColore() {
		return colore;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public Progetto getProgetto() {
		return progetto;
	}

	public void setProgetto(Progetto progetto) {
		this.progetto = progetto;
	}

	public boolean equals(Object o) {
		Tag tag = (Tag)o;
		return this.getId().equals(tag.getId());
	}
	
	public int hashCode() {
		return this.getId().hashCode();
	}

}
