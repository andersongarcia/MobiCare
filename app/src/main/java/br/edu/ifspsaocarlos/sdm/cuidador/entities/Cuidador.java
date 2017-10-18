package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

/**
 * Classe bean que representa um cuidador
 *
 * @author Anderson Canale Garcia
 */

public class Cuidador implements Serializable {

    private Contato contato;

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }
}
