package br.edu.ifspsaocarlos.sdm.cuidador.interfaces;

import java.io.Serializable;

/**
 * Created by ander on 05/11/2017.
 */

public interface IMensagem extends Serializable {
    String getId();
    String getTitulo();
    String getOrigem();
    String getFotoUri();
    String getAudioUri();
}
