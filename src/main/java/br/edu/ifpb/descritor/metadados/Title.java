package br.edu.ifpb.descritor.metadados;

import java.util.ArrayList;
import java.util.List;

public class Title implements MetadadoDescritivo {
	
	private String title;
	@Override
	public List<String> gerarPalpite() {
		List<String> list = new ArrayList<String>();
		list.add(title);
		return list;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

}
