package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Created by ander on 05/11/2017.
 */

public class RemedioMensagemAdapter implements IMensagem {

    private final Remedio remedio;

    public RemedioMensagemAdapter(Remedio remedio){
        this.remedio = remedio;
    }

    @Override
    public String getId() {
        return remedio.getId();
    }

    @Override
    public String getOrigem() {
        return CuidadorService.NO.getNo(CuidadorService.NO.REMEDIOS);
    }

    @Override
    public String getFotoUri() {
        return remedio.getFotoUri();
    }

    @Override
    public String getAudioUri() {
        return remedio.getInstrucaoUri();
    }
}
