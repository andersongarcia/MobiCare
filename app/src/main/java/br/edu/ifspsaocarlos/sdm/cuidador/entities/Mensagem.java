package br.edu.ifspsaocarlos.sdm.cuidador.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Classe que representa uma mensagem de áudio.
 *
 * @author Anderson Canale Garcia
 */
public class Mensagem implements Serializable, IMensagem {
    private HashMap<String, Object> timestampEnvio;
    private String id;
    private String emissorId;
    private String destinatarioId;
    private String fotoUri;
    private String audioUri;

    public Mensagem(){
        this.emissorId = "";
        this.destinatarioId = "";
        this.audioUri = "";
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampEnvio = timestampNow;
    }

    public Mensagem(String emissorId, String destinatarioId, String audioUri) {
        this.emissorId = emissorId;
        this.destinatarioId = destinatarioId;
        this.audioUri = audioUri;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampEnvio = timestampNow;
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

    public HashMap<String, Object> getTimestampEnvio(){
        return timestampEnvio;
    }

    @Exclude
    public long getTimestampEnvioLong(){
        Object timestamp = timestampEnvio.get("timestamp");
        return (long) timestamp;
    }

    @Exclude
    public String obterDataHora() {
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/mm/yyyy hh:mm");
        return dt1.format(new Date(getTimestampEnvioLong()));
    }
}
