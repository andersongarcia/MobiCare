package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Classe que representa um contato
 *
 * @author Anderson Canale Garcia
 */
public class Contato extends RealmObject implements Serializable {

    @PrimaryKey
    private String telefone;

    private String nome;

    public String getNome() {

        return nome;
    }

    public void setNome(String nome) {

        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}

