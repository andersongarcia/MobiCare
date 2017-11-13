package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.cuidador.util.DatetimeHelper;

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

    private String fotoUri;

    private String instrucaoUri;

    public Remedio() {}  // Needed for Firebase


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

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    public String getInstrucaoUri() {
        return instrucaoUri;
    }

    public void setInstrucaoUri(String instrucaoUri) {
        this.instrucaoUri = instrucaoUri;
    }

    @Exclude
    public String calculaProximoHorario() {
        if(repeticao == 0)
            return "";

        Calendar ultimoHorario = DatetimeHelper.getThisTimeToday(horario);
        ultimoHorario.add(Calendar.HOUR, repeticao);

        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        String proximoHorario = s.format(ultimoHorario.getTime());

        return proximoHorario;
    }
}
