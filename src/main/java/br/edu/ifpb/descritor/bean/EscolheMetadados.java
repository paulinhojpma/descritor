package br.edu.ifpb.descritor.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.jena.rdf.model.Model;

import com.sun.faces.context.flash.ELFlash;

import br.edu.ifpb.descritor.arquivo.Arquivo;
import br.edu.ifpb.descritor.gerador.GeradorMetadadosDescritivos;
import br.edu.ifpb.descritor.metadados.Keyword;
import br.edu.ifpb.descritor.metadados.Theme;

@ManagedBean
@ViewScoped
public class EscolheMetadados {
	private GeradorMetadadosDescritivos gerador;
	private Map<String, List<String>> palpites;
	private List<String> keyword= new ArrayList<String>();
	private String tema="";
	private List<String> geral;
	private List<String> sugestKeyword = new ArrayList<String>();
	private List<String> sugestTema = new ArrayList<String>();
	private Arquivo dataset;
	
	
	
	@PostConstruct
	private void init(){
		
			Model model = (Model) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("model");
			dataset = (Arquivo) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("dataset");
			//FacesContext.getCurrentInstance().getExternalContext().getFlash().remove("model");
			
			if(model != null){
				this.gerador=new GeradorMetadadosDescritivos(model);
				
				
				//FacesContext.getCurrentInstance().getExternalContext().getFlash().clear();
				this.palpites=gerador.gerarPalpites();
				 keyword=palpites.get("br.edu.ifpb.descritor.metadados.Keyword");
				 inicializarArrayVazios();
				 
				 sugestKeyword=((Keyword)gerador.getMetadados().get(0)).getListaLabels();
				 sugestTema=palpites.get("br.edu.ifpb.descritor.metadados.Theme");
				 if(sugestKeyword.isEmpty()){
					 sugestKeyword.add("");
				 }
				 if(sugestTema.isEmpty()){
					 sugestTema.add("");
				 }
			}else{
				System.out.println("VOLTOU AO POSTCONT");
			}
			 
		 
	}
	
	
	public void inicializarArrayVazios(){
		if(keyword.size()<5){
			for(int i=keyword.size();i<5;i++){
				keyword.add("");
			}
		}
		
	}
	public String gerarArquivo(){	
		String uri="";
		Map<String, List<String>> arquivo = new HashMap<String,List<String>>();
		List<String> temas= new ArrayList<String>();
		temas.add(getTema());
		
		if(!sugestTema.get(0).equals("")){
			uri= ((Theme)gerador.getMetadados().get(1)).getUri().get(getTema());
			temas.add(uri);
		}
		arquivo.put("keyword", getKeyword());
		arquivo.put("theme",  temas);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("arquivo", arquivo);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("dataset", dataset);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		return "/geraarquivo?faces-redirect=true";
	}
		
	
	
	
	
	
	
	public String msg(){
		return "ola";
	}

	public GeradorMetadadosDescritivos getGerador() {
		return gerador;
	}

	public void setGerador(GeradorMetadadosDescritivos gerador) {
		this.gerador = gerador;
	}

	public Map<String, List<String>> getPalpites() {
		return palpites;
	}

	public void setPalpites(Map<String, List<String>> palpites) {
		this.palpites = palpites;
	}

	public List<String> getKeyword() {
		return keyword;
	}

	public List<String> getSugestKeyword() {
		return sugestKeyword;
	}

	public void setSugestKeyword(List<String> sugestKeyword) {
		this.sugestKeyword = sugestKeyword;
	}

	
	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
	}

	public String getTema() {
		return tema;
	}

	public void setTema(String tema) {
		this.tema = tema;
	}

	public List<String> getSugestTema() {
		return sugestTema;
	}

	public void setSugestTema(List<String> sugestTema) {
		this.sugestTema = sugestTema;
	}

	
}
