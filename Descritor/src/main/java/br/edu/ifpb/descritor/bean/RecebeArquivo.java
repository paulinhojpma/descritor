package br.edu.ifpb.descritor.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.sun.faces.context.flash.ELFlash;

import br.edu.ifpb.descritor.gerador.GeradorMetadadosDescritivos;

@ManagedBean
public class RecebeArquivo {
	private Part fluxo;
	
	private String content;
	private Model model;
	private GeradorMetadadosDescritivos gerador;
	
	public String upload() {
		
		try {
			model= ModelFactory.createDefaultModel();
			
			InputStream in=fluxo.getInputStream();
			//System.out.println(fluxo.getContentType());
			RDFDataMgr.read(model, in, getExtensao(fluxo));
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erro: Arquivo não pode ser lido");
		}
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put("model", model);
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
}
