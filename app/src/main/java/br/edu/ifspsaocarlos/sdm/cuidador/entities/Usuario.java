package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe que representa um usuário
 *
 * @author Anderson Canale Garcia
 */
public class Usuario implements Serializable {
    public static final String CUIDADOR = "Cuidador";
    public static final String FAMILIAR = "Familiar";
    public static final String IDOSO = "Idoso";

    private String id;

    private Contato contato;

    private String perfil;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public String getPerfil() { return perfil; }

    public void setPerfil(String perfil) { this.perfil = perfil; }
}
