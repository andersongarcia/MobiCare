package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    private Semana semana;

    private int codigoAlarme;

    private String fotoUri;

    public Programa(){
        this.semana = new Semana();
    }


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

    public Semana getSemana() {
        return semana;
    }

    public void setSemana(Semana semana) {
        this.semana = semana;
    }

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    public Calendar obterProximaExibicao() {
        String array[];
        array = horario.split(":");
        int hora = Integer.parseInt(array[0]);
        int minuto = Integer.parseInt(array[1]);

        Calendar agora = new GregorianCalendar();
        agora.setTimeInMillis(System.currentTimeMillis());

        for(int i=0; i<7; i++){
            if(semana.diaEstaSelecionado(agora.get(Calendar.DAY_OF_WEEK))){
                Calendar agenda = new GregorianCalendar();
                agenda.add(Calendar.DAY_OF_YEAR, agora.get(Calendar.DAY_OF_YEAR));
                agenda.set(Calendar.HOUR_OF_DAY, hora);
                agenda.set(Calendar.MINUTE, minuto);
                agenda.set(Calendar.SECOND, 0);
                agenda.set(Calendar.MILLISECOND, 0);
                agenda.set(Calendar.DATE, agora.get(Calendar.DATE));
                agenda.set(Calendar.MONTH, agora.get(Calendar.MONTH));
                if(agenda.after(agora)) {
                    return agenda;
                }else {
                    agenda.add(Calendar.DATE, 1);
                }
            }
        }

        return null;
    }
}
