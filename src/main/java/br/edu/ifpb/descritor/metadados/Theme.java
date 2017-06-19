package br.edu.ifpb.descritor.metadados;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Theme implements MetadadoDescritivo{
		private List<String> temas;
		private List<String> palpites=new ArrayList<String>();
		private Map<String, String> uri= new HashMap<String, String>();
	
	
	public List<String> gerarPalpite(){
		try {
			//System.out.println("TEMA A PESQUISAR ----------"+tema);
			for(String tema: this.temas){
				URL url = new URL("http://lov.okfn.org/dataset/lov/api/v2/term/search?q="+tema+"&type=class");
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				int code = connection.getResponseCode();
				//System.out.println(code);
				//System.out.println(connection.getResponseMessage());

			    String arquivo = geraString(connection.getInputStream());
			    JSONObject my_obj = new JSONObject(arquivo);
			    //System.out.println(my_obj);
			    JSONArray arr = my_obj.getJSONArray("results");
			    System.out.println("tema --------");
			    if(arr.length()>=2){
			    	for(int i=0;i< 2 ;i++){
				    	System.out.println(arr.getJSONObject(i).getJSONArray("prefixedName").get(0));
				    	palpites.add(arr.getJSONObject(i).getJSONArray("prefixedName").get(0).toString());
				    	uri.put(arr.getJSONObject(i).getJSONArray("prefixedName").get(0).toString(), arr.getJSONObject(i).getJSONArray("uri").get(0).toString());
				    	
				    }
			    }
			    
			}	
			
		   
		    
		 
			
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return palpites;
	}
	public List<String> getTemas() {
		return temas;
	}
	public void setTemas(List<String> temas) {
		this.temas = temas;
	}
	
	private String geraString(InputStream input){
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	   // System.out.println(reader.readLine());
	  
	    try {
			while ((line = reader.readLine()) != null) {
				
				
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return sb.toString();

		
	}
	public Map<String, String> getUri() {
		return uri;
	}
	public void setUri(Map<String, String> uri) {
		this.uri = uri;
	}
}
