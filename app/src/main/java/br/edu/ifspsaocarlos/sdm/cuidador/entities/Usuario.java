package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Classe que representa um usuário
 *
 * @author Anderson Canale Garcia
 */
public class Usuario extends RealmObject implements Serializable {
    private Contato contato;

    private RealmList<Idoso> idosos;

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public RealmList<Idoso> getIdosos() {
        return idosos;
    }

    public void setIdosos(RealmList<Idoso> idosos) {
        this.idosos = idosos;
    }
}
