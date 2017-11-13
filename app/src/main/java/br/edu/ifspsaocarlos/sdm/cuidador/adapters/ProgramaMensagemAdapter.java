package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import android.media.RingtoneManager;

import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;

/**
 * Created by ander on 05/11/2017.
 */

public class ProgramaMensagemAdapter implements IMensagem {

    private final Programa programa;

    public ProgramaMensagemAdapter(Programa programa){
        this.programa = programa;
    }

    @Override
    public String getId() {
        return programa.getId();
    }

    @Override
    public String getTitulo() { return programa.getNome(); }

    @Override
    public String getOrigem() {
        return NO.getNo(NO.PROGRAMAS);
    }

    @Override
    public String getFotoUri() {
        return programa.getFotoUri();
    }

    @Override
    public String getAudioUri() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
    }
}
