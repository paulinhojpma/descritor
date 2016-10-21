package br.edu.ifpb.descritor.gerador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

import br.edu.ifpb.descritor.metadados.Keyword;
import br.edu.ifpb.descritor.metadados.MetadadoDescritivo;

public class GeradorMetadadosDescritivos {
	private Model model;
   
	private List<MetadadoDescritivo> metadados;
	
	public GeradorMetadadosDescritivos(Model model){
		this.model=model;
		metadados=new ArrayList<MetadadoDescritivo>();
		addMetadadoDescritivo(new Keyword(this.model));
		Map<String, List<String>> palpites=gerarPalpites();
		
		
			
			
		
		
		
	}
	
	public Map<String, List<String>> gerarPalpites(){
		Map<String, List<String>> palpites=new HashMap<String, List<String>>();
		for(MetadadoDescritivo metadado: metadados){
			System.out.println(metadado.getClass().getName());
			palpites.put(metadado.getClass().getName(), metadado.gerarPalpite());
		}
		
		return palpites;
	}
	 public Model getModel() {
			return model;
		}
		public void setModel(Model model) {
			this.model = model;
		}
		
	public void addMetadadoDescritivo(MetadadoDescritivo metadado){
		metadados.add(metadado);
	}
	public void removerMetadadoDescritivo(MetadadoDescritivo metadado){
		metadados.remove(metadado);
	}
}
