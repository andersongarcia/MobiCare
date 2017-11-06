package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.adapters.ProgramaMensagemAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.RemedioMensagemAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.receivers.AlarmeReceiver;

/**
 * Created by ander on 03/11/2017.
 */

public class AlarmeService {

    private final Context contexto;
    private final CuidadorFirebaseRepository repositorio;
    private final AlarmeReceiver alarmeReceiver;

    public AlarmeService(Context contexto){
        this.contexto = contexto;
        this.repositorio = CuidadorFirebaseRepository.getInstance();
        this.alarmeReceiver = new AlarmeReceiver();
    }
    
    public void atualizaAlarmesRemedios(){
        List<Remedio> remedios = repositorio.getRemedios();

        for (Remedio remedio : remedios) {
            IMensagem mensagem = new RemedioMensagemAdapter(remedio);
            alarmeReceiver.cancelaAlarme(contexto, remedio.getCodigoAlarme());
            if(remedio.isAjustavel() || remedio.getRepeticao() == 0){
                alarmeReceiver.defineAlarmeUnico(contexto, mensagem, remedio.getCodigoAlarme(), remedio.getHorario(), remedio.getRepeticao() > 0);
            }else {
                alarmeReceiver.defineAlarmeRecorrente(contexto, mensagem, remedio.getCodigoAlarme(), remedio.getHorario(), remedio.getRepeticao());
            }
        }
    }

    public void atualizaAlarmesProgramas(){
        List<Programa> programas = repositorio.getProgramas();

        for (Programa programa : programas) {
            IMensagem mensagem = new ProgramaMensagemAdapter(programa);
            alarmeReceiver.cancelaAlarme(contexto, programa.getCodigoAlarme());
            Calendar proximaExibicao = programa.obterProximaExibicao();
            alarmeReceiver.defineAlarmeUnico(contexto, mensagem, programa.getCodigoAlarme(), proximaExibicao, false);
        }
    }
}
