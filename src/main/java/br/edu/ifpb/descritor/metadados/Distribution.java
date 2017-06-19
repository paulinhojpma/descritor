package br.edu.ifpb.descritor.metadados;

import java.util.ArrayList;
import java.util.List;

public class Distribution implements MetadadoDescritivo{
	private String extensao;
	
	@Override
	public List<String> gerarPalpite() {
		List<String> lista= new ArrayList<String>();
		lista.add(extensao);
		return lista;
	}

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}
	
}
