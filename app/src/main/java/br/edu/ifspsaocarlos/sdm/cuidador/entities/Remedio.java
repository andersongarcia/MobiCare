package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

/**
 * Classe que representa um remédio
 *
 * @author Anderson Canale Garcia
 */
public class Remedio implements Serializable {

    private String id;

    private String nome;

    private String horarios;

    private String dose;


    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }
}
