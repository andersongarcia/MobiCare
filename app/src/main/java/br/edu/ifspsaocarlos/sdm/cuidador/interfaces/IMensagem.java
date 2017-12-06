package br.edu.ifspsaocarlos.sdm.cuidador.interfaces;

import java.io.Serializable;

/**
 * Interface para adapter de mensagem
 *
 * @author Anderson Canale Garcia
 */
public interface IMensagem extends Serializable {
    String getId();
    String getTitulo();
    String getOrigem();
    String getFotoUri();
    String getAudioUri();
}
