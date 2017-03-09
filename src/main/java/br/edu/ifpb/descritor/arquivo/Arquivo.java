package br.edu.ifpb.descritor.arquivo;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.jena.rdf.model.Model;

public class Arquivo implements Serializable{
	
	private Model model;
	
	private String filename;
	
	private String formato;
	
	private InputStream in;

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Arquivo(Model model, String filename, String formato, InputStream in) {
		super();
		this.model = model;
		this.filename = filename;
		this.formato = formato;
		this.in = in;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	

}
