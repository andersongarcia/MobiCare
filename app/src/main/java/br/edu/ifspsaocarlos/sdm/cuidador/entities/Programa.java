package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Classe que representa um Programa de TV
 *
 * @author Anderson Canale Garcia
 */
public class Programa extends RealmObject implements Serializable {

    @PrimaryKey
    private String nome;

    private String horarios;

    private String link;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String dose) {
        this.link = link;
    }
}
