package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Classe que representa uma medicacao
 *
 * @author Anderson Canale Garcia
 */
public class Medicacao extends RealmObject implements Serializable {

    @PrimaryKey
    private String nome;

    private String horarios;

    private String dose;


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
