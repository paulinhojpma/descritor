

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Literal;


public class Teste {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Model model = ModelFactory.createDefaultModel() ;
		
			
			try {
				//model.read(new File("D:\\Paulinho\\Dropbox\\Grupo de Pesquisa SIDE\\Datasets\\People\\Zé Ramalho Dbpedia.rdf").toURL().toString());
				model.read(new File("Zé Ramalho Dbpedia.rdf").toURL().toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    Map<String, Integer> keyWords=new HashMap<String, Integer>();
		    //retirar owl:Onthology
			String queryString = "PREFIX owl:    <http://www.w3.org/2002/07/owl#>"
					+ "Select ?pulin (count(?pulin) as ?ct) "
					+ "where { ?x a ?pulin. filter  exists{ ?z ?y ?x }."
					+ "filter not exists{?x a owl:Ontology}. "
					+ "filter (!regex (str(?pulin), \"Person\", \"i\"))."
					+ "filter(!regex (str(?pulin), \"Thing\", \"i\"))  } group by ?pulin";
		
			 Query query = QueryFactory.create(queryString);
			 
			  try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
				    ResultSet results = (ResultSet) qexec.execSelect() ;
				   int i=0;
						for ( ; results.hasNext() ; )
						{	i++;
						  QuerySolution soln = ((ResultSet) results).nextSolution() ;
						  RDFNode n = soln.get("pulin") ; // "x" is a variable in the query  
						  Literal l=  soln.getLiteral("ct");
						  String[] lista;
						 if(n!=null){
							 if(n.toString().contains("#")){
								   lista=n.toString().split("#");  
							  }
							  else{
								  lista=n.toString().split("/"); 
							  }
							  
							  Integer c=Integer.parseInt(l.getLexicalForm().toString()) ;
							  String word= lista[lista.length-1];
							  Pattern p = Pattern.compile("[^0-9]*");
							  Matcher m= p.matcher(word);
							  if(m.find()){
								  keyWords.put(m.group(), c*2);
							  }else{
								  keyWords.put(word, c*2); 
							  }
							  
							 
						 }
						 
						 
						}
						// System.out.println(i);
					
				  }  
			  
			 queryString = "PREFIX owl:    <http://www.w3.org/2002/07/owl#>"
			 		+ " Select ?pulin (count(?pulin) as ?ct)"
			 		+ " where { ?x a ?pulin. filter not exists{ ?z ?y ?x }. "
			 		+ "filter not exists{?x a owl:Ontology}."
			 		+ " filter(!regex (str(?pulin), \"Person\", \"i\")). "
			 		+ "filter(!regex (str(?pulin), \"Thing\", \"i\")) } group by ?pulin";
				
				 query = QueryFactory.create(queryString);
				 
				  try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
					    ResultSet results = (ResultSet) qexec.execSelect() ;
					   int i=0;
					   if(results!=null){
							for ( ; results.hasNext() ; ){	
								i++;
							  QuerySolution soln = ((ResultSet) results).nextSolution() ;
							  RDFNode n = soln.get("pulin") ; // "x" is a variable in the query  
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
								  String word= lista[lista.length-1];
								  Pattern p = Pattern.compile("[^0-9]*");
								  Matcher m= p.matcher(word);
								  if(m.find()){
									  word=m.group();
								  }
								  
								  if(keyWords.containsKey(word)){
									  keyWords.put(word, keyWords.get(word)+c*5);
								  }else{
									  keyWords.put(word, +c*5);
								  }
							 }
							  
							  
							 
							}
					   }
						
							 //System.out.println(i);
						
					  } 
				 
				  
				  keyWords=sortHashMapByValues(keyWords);
				  queryString =" SELECT ?o (count(?o) as ?ct) WHERE { ?s ?p ?o. filter (regex (str(?p), \"label\", \"i\")) } group by ?o";
						
				  query = QueryFactory.create(queryString);
				  
				  Map<String, Integer> temp=new HashMap<String,Integer>();
				  try (QueryExecution qexec = QueryExecutionFactory.create(query, model)){
					  ResultSet results = (ResultSet) qexec.execSelect(); 
					  for ( ; results.hasNext() ; ){
						  QuerySolution soln = ((ResultSet) results).nextSolution() ;
						  RDFNode n = soln.get("o");   
						  Literal l=  soln.getLiteral("ct");
						  //String[] lista;
						  if(n!=null){
							  temp.put(n.toString(), Integer.parseInt(l.getLexicalForm()));
						  }
					  }
				  }
				 
				  temp=sortHashMapByValues(temp);
				  
				  System.out.println("LABELS");
				  
				  for(String i: temp.keySet()){
					  System.out.println(i +" - "+temp.get(i) );
				  }
				  
				  System.out.println("\nCONCEPTS");
				  for(String i:keyWords.keySet()){
					  System.out.println(i +" - "+keyWords.get(i) );
				  }
	}
	
	public static LinkedHashMap<String,Integer> sortHashMapByValues(
	        Map<String, Integer> keyWords) {
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
}
