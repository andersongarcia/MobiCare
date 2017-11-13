package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.cuidador.adapters.ProgramaMensagemAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.RemedioMensagemAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.receivers.AlarmeReceiver;

/**
 * Created by ander on 03/11/2017.
 */

public class AlarmeService {

    private static final String TAG = "AlarmeService";
    private final Context contexto;
    private final AlarmeReceiver alarmeReceiver;
    private final PreferenciaHelper preferencias;

    public AlarmeService(Context contexto){
        this.contexto = contexto;
        this.alarmeReceiver = new AlarmeReceiver();
        this.preferencias = new PreferenciaHelper(contexto);
    }

    public void atualizaAlarmeRemedio(Remedio remedio) {
        IMensagem mensagem = new RemedioMensagemAdapter(remedio);
        alarmeReceiver.cancelaAlarme(contexto, remedio.getCodigoAlarme());
        Log.d(TAG, "Atualizando alarmes para remédio " + remedio.getNome());
        if(remedio.isAjustavel() || remedio.getRepeticao() == 0){
            alarmeReceiver.defineAlarmeUnico(contexto, mensagem, remedio.getCodigoAlarme(), remedio.getHorario(), remedio.getRepeticao() > 0);
        }else {
            alarmeReceiver.defineAlarmeRecorrente(contexto, mensagem, remedio.getCodigoAlarme(), remedio.getHorario(), remedio.getRepeticao());
        }
        // salva código do alarme nas preferências
        preferencias.setAlarmeId(remedio.getCodigoAlarme(), PreferenciaHelper.ALARMES_REMEDIOS);
    }

    public void atualizaAlarmePrograma(Programa programa) {
        IMensagem mensagem = new ProgramaMensagemAdapter(programa);
        Log.d(TAG, "Atualizando alarmes para programa " + programa.getNome());
        alarmeReceiver.cancelaAlarme(contexto, programa.getCodigoAlarme());
        Calendar proximaExibicao = programa.obterProximaExibicao();
        alarmeReceiver.defineAlarmeUnico(contexto, mensagem, programa.getCodigoAlarme(), proximaExibicao, false);
        // salva código do alarme nas preferências
        preferencias.setAlarmeId(programa.getCodigoAlarme(), PreferenciaHelper.ALARMES_PROGRAMAS);
    }

    public void cancelaTodos(String tagAlarme) {
        for (int idAlarm : preferencias.getAlarmesIds(tagAlarme)) {
            alarmeReceiver.cancelaAlarme(contexto, idAlarm);
        }
    }
}