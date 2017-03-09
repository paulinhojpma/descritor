package br.edu.ifpb.descritor.bean;

import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
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
		/*String conteudo = "";
				for(String key: arquivo.keySet()){
					for(String valores: arquivo.get(key)){
						conteudo+=valores+"\n";
					}
				}
		*/
		
		
		if(dataset.getFormato().equals(".rdf")){
			langArquivo = "RDF/XML-ABBREV"; // also try "N-TRIPLE" and "TURTLE"
		}else{
			langArquivo = "TURTLE"; // also try "N-TRIPLE" and "TURTLE"
			
		}
		
		StringWriter out = new StringWriter();
		novoModel.write(out, langArquivo);
		String result = out.toString();
		
		return result;
	}
	
	public void downloadFile(){
		FacesContext fc = FacesContext.getCurrentInstance();
	    ExternalContext ec = fc.getExternalContext();
		
		String fileName= "Descritivo"+dataset.getFilename();
		//FileWriter out = null;
		/*try {
			 out = new FileWriter(fileName);
			 novoModel.write( out, langArquivo );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("DEU ERRO NA GERAÇÃO DO ARQUIVO");
		}
		finally {
			   try {
			       out.close();
			   }
			   catch (IOException closeException) {
			       // ignore
			   }
			}*/
		
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
			if(!arquivo.get("theme").get(0).equals("")){
				propriedade = novoModel.createProperty("http://vocab.deri.ie/dcat#theme");
				nodeRaiz.addProperty(propriedade, arquivo.get("theme").get(0));
			}
		}
		
		
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
