package br.edu.ifpb.descritor.bean;

import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Model;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import br.edu.ifpb.descritor.arquivo.Arquivo;

@ManagedBean
@ViewScoped 
public class GeraArquivo {
	private Map<String, List<String>> arquivo;
	private Arquivo dataset;
	private String conteudo;
	private Model novoModel;
	private Model antigoModel;
	private String langArquivo;
	
	
	@PostConstruct
	public void init(){
		arquivo =  (Map) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("arquivo");
		dataset = (Arquivo) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("dataset");
		novoModel = ModelFactory.createDefaultModel();
		adcionaVocabularios();
		criarMetadadosDescritivos();
		conteudo = gerarConteudo(arquivo);
		
	}

	private String gerarConteudo(Map<String, List<String>> arquivo){
		System.out.println("FORMATO RETORNADO ---"+ dataset.getFormato());
		
		if(dataset.getFormato().equals("rdf")){
			langArquivo = "RDF/XML-ABBREV"; // also try "N-TRIPLE" and "TURTLE"
		}else{
			langArquivo = "TURTLE"; // also try "N-TRIPLE" and "TURTLE"
			
		}
		
		StringWriter out = new StringWriter();
		novoModel.write(out, langArquivo);
		String result = out.toString();
		
		return result;
	}
	public void changeRadioEvent(){
		System.out.println("Entrou no evento do RADIO");
		System.out.println("Tipo de arquivo ---" + langArquivo);
		StringWriter out = new StringWriter();
		novoModel.write(out, langArquivo);
		conteudo = out.toString();
		System.out.println(conteudo);
	}
	public void downloadFile(){
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
		
		String fileName= "";
		if(this.langArquivo.equals("TURTLE")){
			fileName= "Descritivo"+dataset.getFilename()+".ttl";
			
		}else{
			fileName= "Descritivo"+dataset.getFilename()+".rdf";
		}
		
	
		
		 ec.responseReset();
		
			 ec.setResponseContentType("application/rdf+xml");
		
		 
		 ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		 try {
			OutputStream  out = ec.getResponseOutputStream();
			novoModel.write(out, langArquivo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 fc.responseComplete();
		
	}
	private void adcionaVocabularios(){
		novoModel.setNsPrefix("dcat", "http://vocab.deri.ie/dcat#");
		novoModel.setNsPrefix("dct", "http://purl.org/dc/terms/");
		//novoModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	}
	
	private void criarMetadadosDescritivos(){
		Resource nodeRaiz;
		String uri = "";
				if(dataset.getFilename().startsWith("http")){
					nodeRaiz = novoModel.createResource(dataset.getFilename());
				}else{
					nodeRaiz = novoModel.createResource("http://www.example.com/"+dataset.getFilename());
				}
		 
		
		Property propriedade= novoModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#"+ "type");
		Resource nodo = novoModel.createResource("http://vocab.deri.ie/dcat#Dataset");
		nodeRaiz.addProperty(propriedade, nodo);

		propriedade = novoModel.createProperty("http://vocab.deri.ie/dcat#keywords");
		List<String> keywords = arquivo.get("keyword");
		for(String keyword: keywords){
			if(!keyword.equals("")){
				nodeRaiz.addProperty(propriedade, keyword);
			}
			
		}
		
		if(arquivo.get("theme").size()>1){
			String prefix = arquivo.get("theme").get(0).split("[:]")[0];
			String vocab = "";
			if(arquivo.get("theme").get(1).contains("#")){
				vocab = arquivo.get("theme").get(1).split("#")[0];
			}else{
				vocab = arquivo.get("theme").get(1).substring(0, arquivo.get("theme").get(1).lastIndexOf("/"))+"/";
				
			}
			
			novoModel.setNsPrefix(prefix, vocab);
			nodo = novoModel.createResource(arquivo.get("theme").get(1));
			propriedade = novoModel.createProperty("http://vocab.deri.ie/dcat#theme");
			nodeRaiz.addProperty(propriedade, nodo);
		}else{
			if(!arquivo.get("theme").get(0).equals("") ){
				propriedade = novoModel.createProperty("http://vocab.deri.ie/dcat#theme");
				nodeRaiz.addProperty(propriedade, arquivo.get("theme").get(0));
			}
		}
		propriedade = novoModel.createProperty("http://purl.org/dc/terms/"+ "title");
		nodeRaiz.addProperty(propriedade, arquivo.get("title").get(0));
		propriedade = novoModel.createProperty("http://vocab.deri.ie/dcat#distribution");
		nodeRaiz.addProperty(propriedade, arquivo.get("distribution").get(0));
		if(!arquivo.get("issued").isEmpty() && !arquivo.get("issued").get(0).equals("") && arquivo.get("issued").get(0) != null){
			
			
			
			propriedade = novoModel.createProperty("http://purl.org/dc/terms/"+ "issued");
			nodeRaiz.addProperty(propriedade, arquivo.get("issued").get(0) );
		}
		if(!arquivo.get("contactPoint").isEmpty() && !arquivo.get("contactPoint").get(0).equals("") && arquivo.get("contactPoint").get(0) != null){
			propriedade = novoModel.createProperty("http://vocab.deri.ie/dcat#"+ "contactPoint");
			nodeRaiz.addProperty(propriedade, arquivo.get("contactPoint").get(0));
		}
		if(!arquivo.get("spatialCoverage").isEmpty() && !arquivo.get("spatialCoverage").get(0).equals("") && arquivo.get("spatialCoverage").get(0) !=null){
			propriedade = novoModel.createProperty("http://purl.org/dc/terms/"+ "spatialCoverage");
			nodeRaiz.addProperty(propriedade, arquivo.get("spatialCoverage").get(0));
		}
		if(!arquivo.get("language").isEmpty() && !arquivo.get("language").get(0).equals("") && arquivo.get("language").get(0) != null){
			propriedade = novoModel.createProperty("http://purl.org/dc/terms/"+ "language");
			nodeRaiz.addProperty(propriedade, arquivo.get("language").get(0));
		}
		if(!arquivo.get("accrualPeriodicity").isEmpty() && !arquivo.get("accrualPeriodicity").get(0).equals("") && arquivo.get("accrualPeriodicity").get(0) !=null){
			propriedade = novoModel.createProperty("http://purl.org/dc/terms/"+ "accrualPeriodicity");
			nodeRaiz.addProperty(propriedade, arquivo.get("accrualPeriodicity").get(0));
		}
		
		
		//http://purl.org/dc/terms/ 
		//nodeRaiz.addProperty(propriedade, "pulin");
		//nodeRaiz.addProperty(propriedade, arg1)
	}
	public String getConteudo() {
		return conteudo;
	}


	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getLangArquivo() {
		return langArquivo;
	}

	public void setLangArquivo(String langArquivo) {
		this.langArquivo = langArquivo;
	}

	
}
