package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

/**
 * Classe que representa um contato
 *
 * @author Anderson Canale Garcia
 */
public class Contato implements Serializable {

    private String id;

    private String telefone;

    private String nome;

    private String fotoUri;

    public Contato(){}

    public Contato(String nome, String telefone) {
        this.id = telefone;
        this.nome = nome;
        this.telefone = telefone;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }
}

