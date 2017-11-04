package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
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
            alarmeReceiver.cancelaAlarme(contexto, remedio.getCodigoAlarme());
            if(remedio.isAjustavel()){
                alarmeReceiver.defineAlarmeUnico(contexto, remedio.getCodigoAlarme(), remedio.getHorario(), true);
            }else {
                alarmeReceiver.defineAlarmeRecorrente(contexto, remedio.getCodigoAlarme(), remedio.getHorario(), remedio.getRepeticao());
            }
        }
    }

    public void atualizaAlarmesProgramas(){
        List<Programa> programas = repositorio.getProgramas();

        for (Programa programa : programas) {
            alarmeReceiver.cancelaAlarme(contexto, programa.getCodigoAlarme());
            if(programa.getRepeticao() > 0){
                alarmeReceiver.defineAlarmeRecorrente(contexto, programa.getCodigoAlarme(), programa.getHorario(), programa.getRepeticao());
            }else {
                alarmeReceiver.defineAlarmeUnico(contexto, programa.getCodigoAlarme(), programa.getHorario(), true);
            }
        }
    }
}
