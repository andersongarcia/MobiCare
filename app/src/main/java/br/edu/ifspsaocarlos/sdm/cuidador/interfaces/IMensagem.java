package br.edu.ifspsaocarlos.sdm.cuidador.interfaces;

import java.io.Serializable;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;

/**
 * Created by ander on 05/11/2017.
 */

public interface IMensagem extends Serializable {
    String getId();
    String getOrigem();
    String getFotoUri();
    String getAudioUri();
}
