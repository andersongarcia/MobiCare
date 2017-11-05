package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Classe de seleção dos dias da semana
 *
 * @author Anderson Canale Garcia
 */
public class Semana implements Serializable {
    private boolean domingo;
    private boolean segunda;
    private boolean terca;
    private boolean quarta;
    private boolean quinta;
    private boolean sexta;
    private boolean sabado;

    public Semana()
    {
        domingo = false;
        segunda = false;
        terca = false;
        quarta = false;
        quinta = false;
        sexta = false;
        sabado = false;
    }

    public boolean isDomingo() {
        return domingo;
    }

    public void setDomingo(boolean domingo) {
        this.domingo = domingo;
    }

    public boolean isSegunda() {
        return segunda;
    }

    public void setSegunda(boolean segunda) {
        this.segunda = segunda;
    }

    public boolean isTerca() {
        return terca;
    }

    public void setTerca(boolean terca) {
        this.terca = terca;
    }

    public boolean isQuarta() {
        return quarta;
    }

    public void setQuarta(boolean quarta) {
        this.quarta = quarta;
    }

    public boolean isQuinta() {
        return quinta;
    }

    public void setQuinta(boolean quinta) {
        this.quinta = quinta;
    }

    public boolean isSexta() {
        return sexta;
    }

    public void setSexta(boolean sexta) {
        this.sexta = sexta;
    }

    public boolean isSabado() {
        return sabado;
    }

    public void setSabado(boolean sabado) {
        this.sabado = sabado;
    }

    public boolean diaEstaSelecionado(int dia) {
        switch (dia){
            case Calendar.SUNDAY:
                return domingo;
            case Calendar.MONDAY:
                return segunda;
            case Calendar.TUESDAY:
                return domingo;
            case Calendar.WEDNESDAY:
                return segunda;
            case Calendar.THURSDAY:
                return domingo;
            case Calendar.FRIDAY:
                return segunda;
            case Calendar.SATURDAY:
                return segunda;
        }
        return false;
    }
}
