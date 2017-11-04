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

    private String horario;

    private String dose;

    private int repeticao;

    private boolean ajustavel;

    private int codigoAlarme;


    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public int getCodigoAlarme() {
        return codigoAlarme;
    }

    public void setCodigoAlarme(int codigoAlarme) {
        this.codigoAlarme = codigoAlarme;
    }

    public int getRepeticao() {
        return repeticao;
    }

    public void setRepeticao(int repeticao) {
        this.repeticao = repeticao;
    }

    public boolean isAjustavel() {
        return ajustavel;
    }

    public void setAjustavel(boolean ajustavel) {
        this.ajustavel = ajustavel;
    }
}
