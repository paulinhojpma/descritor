package br.edu.ifpb.descritor.bean;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.jena.rdf.model.Model;

import com.sun.faces.context.flash.ELFlash;

import br.edu.ifpb.descritor.gerador.GeradorMetadadosDescritivos;

@ManagedBean
public class EscolheMetadados {
	private GeradorMetadadosDescritivos gerador;
	private Map<String, List<String>> palpites;
	private List<String> pulin;
	
	@PostConstruct
	private void init(){
		
			Model model = (Model) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("model");
			 this.gerador=new GeradorMetadadosDescritivos(model);
			
		
		FacesContext.getCurrentInstance().getExternalContext().getFlash().clear();
		this.palpites=gerador.gerarPalpites();
		 pulin=palpites.get("br.edu.ifpb.descritor.metadados.Keyword");
		 
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

	public List<String> getPulin() {
		return pulin;
	}

	
}
