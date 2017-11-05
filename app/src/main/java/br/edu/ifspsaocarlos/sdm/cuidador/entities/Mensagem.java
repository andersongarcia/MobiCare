package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import java.io.Serializable;

import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Classe que representa uma mensagem de áudio.
 *
 * @author Anderson Canale Garcia
 */
public class Mensagem implements Serializable, IMensagem {
    private String id;
    private String emissorId;
    private String destinatarioId;
    private String fotoUri;
    private String audioUri;

    public Mensagem(String emissorId, String destinatarioId, String audioUri) {
        this.emissorId = emissorId;
        this.destinatarioId = destinatarioId;
        this.audioUri = audioUri;
    }

    @Override
    public String getOrigem() {
        return CuidadorService.NO.getNo(CuidadorService.NO.MENSAGENS);
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

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }
}
