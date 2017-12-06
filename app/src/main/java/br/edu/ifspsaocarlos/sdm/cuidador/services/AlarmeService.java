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
 * Serviço para atualizações de alarmes
 */
public class AlarmeService {
    //region TAGs
    private static final String TAG = "AlarmeService";
    //endregion

    //region Atributos
    private final Context contexto;
    private final AlarmeReceiver alarmeReceiver;
    private final PreferenciaHelper preferencias;
    //endregion

    //region Construtor
    AlarmeService(Context contexto){
        this.contexto = contexto;
        this.alarmeReceiver = new AlarmeReceiver();
        this.preferencias = new PreferenciaHelper(contexto);
    }
    //endregion

    /**
     * Atualiza alarme para um remédio
     * @param remedio instância do remédio a ter o alarme atualizado
     */
    public void atualizaAlarmeRemedio(Remedio remedio) {
        // Se não tiver horário agendado, não agenda o alarme
        if(remedio.getHorario() == null || remedio.getHorario().isEmpty())
            return;

        // Converte alarme para formato de mensagem
        IMensagem mensagem = new RemedioMensagemAdapter(remedio);
        // Cancela alarme anteriormente definido para o remédio
        alarmeReceiver.cancelaAlarme(contexto, remedio.getCodigoAlarme());
        Log.d(TAG, "Atualizando alarmes para remédio " + remedio.getNome());

        if(remedio.isAjustavel() || remedio.getRepeticao() == 0){
            // Se remédio solicita reprogramação para agendamento, agenda alarme único
            alarmeReceiver.defineAlarmeUnico(contexto, mensagem, remedio.getCodigoAlarme(),
                    remedio.getHorario(), remedio.getRepeticao() > 0);
        }else {
            // Caso contrário, agenda alarme recorrente
            alarmeReceiver.defineAlarmeRecorrente(contexto, mensagem, remedio.getCodigoAlarme(),
                    remedio.getHorario(), remedio.getRepeticao());
        }
        // salva código do alarme nas preferências
        preferencias.setAlarmeId(remedio.getCodigoAlarme(), PreferenciaHelper.ALARMES_REMEDIOS);
    }

    /**
     * Atualiza alarme para um programa
     * @param programa instância do programa a ter o alarme atualizado
     */
    public void atualizaAlarmePrograma(Programa programa) {
        // Se não tiver horário agendado, não agenda o alarme
        if(programa.getHorario() == null || programa.getHorario().isEmpty())
            return;

        // Converte alarme para formato de mensagem
        IMensagem mensagem = new ProgramaMensagemAdapter(programa);
        // Cancela alarme anteriormente definido para o programa
        alarmeReceiver.cancelaAlarme(contexto, programa.getCodigoAlarme());
        Log.d(TAG, "Atualizando alarmes para programa " + programa.getNome());
        // Obtém horário da próxima exibição do programa
        Calendar proximaExibicao = programa.obterProximaExibicao();
        // Se encontrar próxima exibição, definir alarme único
        if(proximaExibicao != null){
            alarmeReceiver.defineAlarmeUnico(contexto, mensagem, programa.getCodigoAlarme(), proximaExibicao, false);
            // salva código do alarme nas preferências
            preferencias.setAlarmeId(programa.getCodigoAlarme(), PreferenciaHelper.ALARMES_PROGRAMAS);
        }
    }

    void cancelaTodos(String tagAlarme) {
        for (int idAlarm : preferencias.getAlarmesIds(tagAlarme)) {
            alarmeReceiver.cancelaAlarme(contexto, idAlarm);
        }
    }
}