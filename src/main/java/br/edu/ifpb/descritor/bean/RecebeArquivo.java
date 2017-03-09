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
		
		try {
			model= ModelFactory.createDefaultModel();
			if(!uri.isEmpty()){
				//ver depois isso pois n�o gera extens�es
				 //in = new java.net.URL(URL).openStream();
				// RDFDataMgr.read(model, in, Lang.TURTLE);
				 in = new URL(uri).openStream();
				model.read(uri);				
				extensao = uri.substring(uri.lastIndexOf("."));
				nomeArquivo = urlNomeArquivo(uri);
				
			}else{
				in=fluxo.getInputStream();
				
				extensao = fluxo.getSubmittedFileName().substring(fluxo.getSubmittedFileName().lastIndexOf("."));
				System.out.println("TIPO DE ARQUIVO----- "+ extensao);
				nomeArquivo = fluxo.getSubmittedFileName();
				RDFDataMgr.read(model, in, getExtensao(fluxo));
				
			}
			System.out.println("Nome do arquivo -------" +fluxo.getSubmittedFileName());
			System.out.println("Nome do tipo de  arquivo -------" +fluxo.getName());
			 	
				//System.out.println(fluxo.getContentType());
				
			
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erro: Arquivo n�o pode ser lido");
		}
		arquivo = new Arquivo(model, nomeArquivo, extensao, in);
		
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("model", model);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("dataset", arquivo);
		return "/escolhemetadados?faces-redirect=true";
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
		
	private String urlNomeArquivo(String url){
		return url.substring(url.lastIndexOf("/"));
	}
	public void validarArquivo(FacesContext ctx, UIComponent comp, Object value) {
		List<FacesMessage> msgs = new ArrayList<FacesMessage>();
		Part file = (Part)value;
	
		if (!"application/rdf+xml".equals(( file).getContentType()) || !"application/x-turtle".equals(( file).getContentType())) {
			msgs.add(new FacesMessage("N�o � um arquivo rdf v�lido."));
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
}
