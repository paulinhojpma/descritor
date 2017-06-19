package br.edu.ifpb.descritor.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	private Date issued;
	private String contactPoint;
	private String spatialCoverage;
	private String language;
	private String accrualPeriodicity;
	private String tempTema;
	private List<String> geral;
	private List<String> sugestKeyword = new ArrayList<String>();
	private List<String> sugestTema = new ArrayList<String>();
	private Arquivo dataset;
	private String sugestTitle;
	private String sugestDistribution;
	
	
	
	@PostConstruct
	private void init(){
			issued = new Date();
			Model model = (Model) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("model");
			dataset = (Arquivo) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("dataset");
			//FacesContext.getCurrentInstance().getExternalContext().getFlash().remove("model");
			
			if(model != null){
				this.gerador=new GeradorMetadadosDescritivos(model);
				
				gerador.setArquivo(dataset);
				//FacesContext.getCurrentInstance().getExternalContext().getFlash().clear();
				this.palpites=gerador.gerarPalpites();
				 keyword=palpites.get("br.edu.ifpb.descritor.metadados.Keyword");
				 inicializarArrayVazios();
				 
				 sugestKeyword=((Keyword)gerador.getMetadados().get(0)).getListaLabels();
				 sugestTema=palpites.get("br.edu.ifpb.descritor.metadados.Theme");
				 sugestTitle = palpites.get("br.edu.ifpb.descritor.metadados.Title").get(0);
				 sugestDistribution = palpites.get("br.edu.ifpb.descritor.metadados.Distribution").get(0);
				 
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
		List<String> title = new ArrayList<String>();
		List<String> issueds= new ArrayList<String>();
		List<String> contactPoints= new ArrayList<String>();
		List<String> converages= new ArrayList<String>();
		List<String> languages= new ArrayList<String>();
		List<String> periods = new ArrayList<String>();
		title.add(sugestTitle);
		List<String> distribution = new ArrayList<String>();
		distribution.add(sugestDistribution);
		temas.add(getTema());
		 SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String data = format.format(issued);
		issueds.add(data);
		contactPoints.add(contactPoint);
		converages.add(spatialCoverage);
		languages.add(language);
		periods.add(accrualPeriodicity);
		if(!sugestTema.get(0).equals("")){
			uri= ((Theme)gerador.getMetadados().get(1)).getUri().get(getTema());
			temas.add(uri);
		}
		arquivo.put("keyword", getKeyword());
		arquivo.put("theme",  temas);
		arquivo.put("title",title);
		arquivo.put("distribution", distribution);
		arquivo.put("issued", issueds);
		arquivo.put("contactPoint", contactPoints);
		arquivo.put("spatialCoverage", converages);
		arquivo.put("language", languages);
		arquivo.put("accrualPeriodicity", periods);
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


	public String getSugestTitle() {
		return sugestTitle;
	}


	public void setSugestTitle(String sugestTitle) {
		this.sugestTitle = sugestTitle;
	}


	public String getSugestDistribution() {
		return sugestDistribution;
	}


	public void setSugestDistribution(String sugestDistribution) {
		this.sugestDistribution = sugestDistribution;
	}
	
	public void eventEscolheTema(){
		
		this.tema = this.tempTema;
	}


	public String getTempTema() {
		return tempTema;
	}


	public void setTempTema(String tempTema) {
		this.tempTema = tempTema;
	}


	public Date getIssued() {
		return issued;
	}


	public void setIssued(Date issued) {
		this.issued = issued;
	}


	public String getContactPoint() {
		return contactPoint;
	}


	public void setContactPoint(String contactPoint) {
		this.contactPoint = contactPoint;
	}


	public String getSpatialCoverage() {
		return spatialCoverage;
	}


	public void setSpatialCoverage(String spatialCoverage) {
		this.spatialCoverage = spatialCoverage;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getAccrualPeriodicity() {
		return accrualPeriodicity;
	}


	public void setAccrualPeriodicity(String accrualPeriodicity) {
		this.accrualPeriodicity = accrualPeriodicity;
	}

	
}
