package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

/**
 * Classe que representa um Programa de TV
 *
 * @author Anderson Canale Garcia
 */
public class Programa implements Serializable {

    private String id;

    private String nome;

    private String horario;

    private String link;

    private int repeticao;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
}
