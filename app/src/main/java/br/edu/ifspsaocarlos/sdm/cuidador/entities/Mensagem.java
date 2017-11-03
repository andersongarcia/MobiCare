package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

/**
 * Classe que representa uma mensagem de áudio.
 *
 * @author Anderson Canale Garcia
 */
public class Mensagem implements Serializable {
    private String id;
    private String emissorId;
    private String destinatarioId;
    private String fileName;

    public Mensagem(String emissorId, String destinatarioId, String fileName) {
        this.emissorId = emissorId;
        this.destinatarioId = destinatarioId;
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getEmissorId() {
        return emissorId;
    }

    public void setEmissorId(String emissorId) {
        this.emissorId = emissorId.trim();
    }

    public String getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(String destinatarioId) {
        this.destinatarioId = destinatarioId.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName.trim();
    }
}
