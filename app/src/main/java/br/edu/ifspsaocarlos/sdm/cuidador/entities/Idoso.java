package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Classe que representa um idoso
 *
 * @author Anderson Canale Garcia
 */
public class Idoso extends RealmObject {
    private Contato contato;

    private RealmList<Contato> contatos;

    private RealmList<Medicacao> medicacoes;

    private RealmList<Programa> programas;

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public RealmList<Contato> getContatos() {
        return contatos;
    }

    public void setContatos(RealmList<Contato> contatos) {
        this.contatos = contatos;
    }

    public RealmList<Medicacao> getMedicacoes() { return medicacoes; }

    public void setMedicacoes(RealmList<Medicacao> medicacoes) { this.medicacoes = medicacoes; }

    public RealmList<Programa> getProgramas() { return programas; }

    public void setProgramas(RealmList<Programa> programas) { this.programas = programas; }
}
