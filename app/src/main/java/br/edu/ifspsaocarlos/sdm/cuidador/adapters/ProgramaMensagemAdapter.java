package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import android.media.RingtoneManager;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

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
    public String getOrigem() {
        return CuidadorService.NO.getNo(CuidadorService.NO.PROGRAMAS);
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
