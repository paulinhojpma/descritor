package br.edu.ifpb.descritor.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.sun.faces.context.flash.ELFlash;

import br.edu.ifpb.descritor.arquivo.Arquivo;
import br.edu.ifpb.descritor.gerador.GeradorMetadadosDescritivos;

@ManagedBean
@ViewScoped 
public class RecebeArquivo {
	private Part fluxo;
	private String uri="";
	private File file;
	private String extensao;
	private String content;
	private String nomeArquivo;
	private Model model;
	private GeradorMetadadosDescritivos gerador;
	private Arquivo arquivo;
	
	
	
	public String upload() {
		InputStream in = null;
		
		model= ModelFactory.createDefaultModel();
		if(!uri.isEmpty()){
			try{
				//ver depois isso pois não gera extensões
				 //in = new java.net.URL(URL).openStream();
				// RDFDataMgr.read(model, in, Lang.TURTLE);
				 in = new URL(uri).openStream();
				//model.read();	
				 System.out.println("URI----- "+ uri);
					System.out.println("TIPO DE ARQUIVO----- "+ extensao);
					nomeArquivo = urlNomeArquivo(uri);
				 RDFDataMgr.read(model, in, getExtensaoUri());
				System.out.println("URI----- "+ uri);
				System.out.println("TIPO DE ARQUIVO----- "+ extensao);
						
			}catch(Exception e){
				System.out.println(e.getCause());
				System.out.println(e.getMessage());
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "URL informada inválida."));
			}
					
		}else{
			try{
				in=fluxo.getInputStream();
				
				extensao = fluxo.getSubmittedFileName().substring(fluxo.getSubmittedFileName().lastIndexOf(".")+1);
				
				System.out.println("TIPO DE ARQUIVO----- "+ extensao);
				
				nomeArquivo = fluxo.getSubmittedFileName().split("[.]")[0];
				RDFDataMgr.read(model, in, getExtensao(fluxo));
			}catch(Exception e){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Arquivo inserido inválido."));
			}
			
			
		}
		
		if(extensao.equals("rdf") || extensao.equals("ttl")){
			
				System.out.println("Nome do arquivo -------" +nomeArquivo);
				System.out.println("Nome do tipo de  arquivo -------" + extensao);
			
			
			arquivo = new Arquivo(model, nomeArquivo, extensao, in);
			
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("model", model);
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("dataset", arquivo);
			return "/escolhemetadados?faces-redirect=true";			
		
		}else{
			System.out.println("TIPO DE ARQUIVO----- "+ extensao);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Arquivo inserido inválido."));
			return "/index?faces-redirect=true";
		}
		
	  }
	
	
	private Lang getExtensao(Part flu){
		Lang  tipo=null;
		String arquivo=flu.getSubmittedFileName();
		String[] extensao=arquivo.split("[.]");
		
		
		arquivo=extensao[extensao.length-1];
		switch (arquivo){
		case "rdf":
			tipo= Lang.RDFXML;
			break;
		case "ttl":
			tipo= Lang.TURTLE;
			break;
		}
		return tipo;
			
	}
	
	private Lang getExtensaoUri(){
		
		if(extensao.equals("rdf")){
			return  Lang.RDFXML;
		}else{
			return Lang.TURTLE;
		}
		
	}
		
	private String urlNomeArquivo(String url){
		return url.substring(url.lastIndexOf("/"));
	}
	public void validarArquivo(FacesContext ctx, UIComponent comp, Object value) {
		List<FacesMessage> msgs = new ArrayList<FacesMessage>();
		Part file = (Part)value;
	
		if (!"application/rdf+xml".equals(( file).getContentType()) || !"application/x-turtle".equals(( file).getContentType())) {
			msgs.add(new FacesMessage("Não é um arquivo rdf válido."));
		}
		if (!msgs.isEmpty()) {
			msgs.add(new FacesMessage("Nenhum arquivo carregado."));
		}
	}


	public Part getFluxo() {
		return fluxo;
	}


	public void setFluxo(Part fluxo) {
		this.fluxo = fluxo;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}


	public String getExtensao() {
		return extensao;
	}


	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}
}
