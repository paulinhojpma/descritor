package br.edu.ifpb.descritor.metadados;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.crypto.KeySelectorException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

public class Keyword implements MetadadoDescritivo {
	private Model model;
	private Map<String, Integer> keyWords=new HashMap<String, Integer>();
	private Map<String, Integer> labels=new HashMap<String, Integer>();
	private List<String> listaLabels = new ArrayList();
	private List<String> palpites=new ArrayList<String>();
	public Keyword(Model model){
		this.model=model;
	}
	
	
	@Override
	public List<String> gerarPalpite() {
		//List<String> palpites=new ArrayList<String>();
		
		System.out.println("\n-------------Resultado Quantidade/peso------------\n");
		//faz consulta sobre os node pai
		Query query = QueryFactory.create(getQuery(1));
		executaConsulta(query, 10);		
		
		//faz consulta nos nodes filhos
		query = QueryFactory.create(getQuery(2));
		executaConsulta(query, 5);		
		
		//faz consulta nos labels
		query = QueryFactory.create(getQuery(3));
		executaConsulta(query, 3);
		
		labels=sortHashMapByValues(labels);
		System.out.println("Labels size"+ labels.size());
		keyWords=sortHashMapByValues(keyWords);
		
		listaLabels();
		System.out.println("\n-------------Resultado dos palpites ---------------\n");
		if(keyWords.size()>5){
	
			
			List<String>keys=new  ArrayList<String>(keyWords.keySet());
			for(int i=0;i<5;i++){
				palpites.add(keys.get(i));
				System.out.println(keys.get(i));
			}
		}else{
			List<String>keys=new  ArrayList<String>(keyWords.keySet());
			for(int i=0;i<keyWords.size();i++){
				palpites.add(keys.get(i));
				System.out.println(keys.get(i));
			}
		}
		return palpites;
	}
	
	//gera a query da consulta;
	private String getQuery(int i){
		String query="";
		switch (i){
		//TO DO colocar a expressão regular nas consultas sparql
		
		
		case 1:
			query= "PREFIX owl:    <http://www.w3.org/2002/07/owl#>"
					+ "Select ?concept (count(?concept) as ?ct) "
					+ "where { ?x a ?concept. filter  exists{ ?z ?y ?x }."
					+ "filter not exists{?x a owl:Ontology}. "
					+ "filter (!regex (str(?concept), \"Person\", \"i\"))."
					+ "filter(!regex (str(?concept), \"Thing\", \"i\"))  } group by ?concept";
			break;
		case 2:
			query= "PREFIX owl:    <http://www.w3.org/2002/07/owl#>"
					+ "Select ?concept (count(?concept) as ?ct) "
					+ "where { ?x a ?concept. filter not exists{ ?z ?y ?x }."
					+ "filter not exists{?x a owl:Ontology}. "
					+ "filter (!regex (str(?concept), \"Person\", \"i\"))."
					+ "filter(!regex (str(?concept), \"Thing\", \"i\")) } group by ?concept";
			break;
		case 3:
			query =" SELECT ?concept (count(?concept) as ?ct) "
					+ "WHERE { ?s ?p ?concept. "
					+ "filter (regex (str(?p), \"label\", \"i\"))."
					+ "filter(LANG(?concept) = \"\" || LANGMATCHES(LANG(?concept), \"en\")) } group by ?concept";
			
		}
		
		return query;
	}
	
	//realiza a consulta
	private Map<String, Integer> executaConsulta(Query query, int peso){
		
		 try (QueryExecution qexec = QueryExecutionFactory.create(query, model)){
			 ResultSet results = (ResultSet) qexec.execSelect();
			 
			 
			 for ( ; results.hasNext() ; ){	
					
				  QuerySolution soln = ((ResultSet) results).nextSolution() ;
				  RDFNode n = soln.get("concept") ; // "x" is a variable in the query  
				  Literal l=  soln.getLiteral("ct");
				  String[] lista;
				 if(n!=null){
					 if( n.toString().contains("#") ){
						   lista=n.toString().split("#");  
						   
					  }
					  else{
						  lista=n.toString().split("/"); 
					  }
					  
					  int c=Integer.parseInt(l.getLexicalForm().toString()) ;
					  String word= matcheRE(lista[lista.length-1]);
					
					  if(word.split(" ").length<3){
						  if(peso == 3){
							  labels.put(word, c);
							  System.out.println(word+"| count -- "+c+"|" );
						  }else{
							  if(keyWords.containsKey(word)){
								  keyWords.put(word, keyWords.get(word)+c*peso);
							  }else{
								  keyWords.put(word, c*peso);
							  }
							  System.out.println(word+"| count -- "+c+"| peso --"+peso+ " |total -- "+c*peso);
						  }
						 
					  }
					 
				 }
		 }
			 
		 }
		return keyWords;
		
	
	}
	private String matcheRE(String word){
		Pattern p = Pattern.compile("^[^0-9,@]*");
		  Matcher m= p.matcher(word);
		  if(m.find()){
			  word=m.group();
		  }
		return word;
	}
	
	private static LinkedHashMap<String,Integer> sortHashMapByValues( Map<String, Integer> keyWords) {
	    List<String> mapKeys = new ArrayList<>(keyWords.keySet());
	    List<Integer> mapValues = new ArrayList<>(keyWords.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);
	    Collections.reverse(mapValues);
	    Collections.reverse(mapKeys);

	    LinkedHashMap<String, Integer> sortedMap =
	        new LinkedHashMap<>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	    	Integer val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            Integer comp1 = keyWords.get(key);
	            Integer comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	   
	    return sortedMap;
	}
	
	public void listaLabels(){
		
		if(labels.size()>5){
			 
			
			List<String>lista=new  ArrayList<String>(labels.keySet());
			for(int i=0;i<5;i++){
				listaLabels.add(lista.get(i));
				
			}
		}else{
			List<String>lista=new  ArrayList<String>(labels.keySet());
			for(int i=0;i<labels.size();i++){
				listaLabels.add(lista.get(i));
			}
		}
		System.out.println("listaLabels size " + listaLabels.size());
	}


	public List<String> getListaLabels() {
		return listaLabels;
	}


	public void setListaLabels(List<String> listaLabels) {
		this.listaLabels = listaLabels;
	}


	public List<String> getPalpites() {
		return palpites;
	}


	public void setPalpites(List<String> palpites) {
		this.palpites = palpites;
	}

}
