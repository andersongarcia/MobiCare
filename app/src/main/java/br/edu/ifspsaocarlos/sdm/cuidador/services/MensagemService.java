package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.receivers.AlarmeReceiver;

/**
 * Created by ander on 29/10/2017.
 */

public class MensagemService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private static final String NOTIFICACAO_NOVA_MENSAGEM = "mensagem";
    private static final String NOTIFICACAO_SINCRONIZAR_CONTATOS = "contatos";
    private static final String NOTIFICACAO_SINCRONIZAR_REMEDIOS = "remedios";
    private static final String NOTIFICACAO_SINCRONIZAR_PROGRAMAS = "programas";
    private static final String NOTIFICACAO_CONFIRMAR_REMEDIO = "alertaRemedio";

    /**
     * Chamado quando uma mensagem é recebida
     *
     * @param remoteMessage Objeto representando a mensagem recebida do Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Checa se a mensagem contém payload de dados
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // mapeia dados do payload
            Map<String, String> data = remoteMessage.getData();

            UsuarioService usuarioService = new UsuarioService(getBaseContext());

            // identifica a que se destina a mensagem
            switch (data.get("label")){
                case NOTIFICACAO_NOVA_MENSAGEM:
                    Mensagem mensagem = new Mensagem(data.get("emissorId"), data.get("destinatarioId"), data.get("audioUri"));
                    mensagem.setId(data.get("id"));
                    mensagem.setTitulo(data.get("titulo"));
                    mensagem.setFotoUri(data.get("fotoUri"));
                    AlarmeReceiver alarm = new AlarmeReceiver();
                    alarm.mostraNovaMensagem(getBaseContext(), mensagem);
                    break;
                case NOTIFICACAO_SINCRONIZAR_CONTATOS:
                    Log.d(TAG, "sincronizar contatos");
                    //usuarioService.sincronizarContatos();
                    break;
                case NOTIFICACAO_SINCRONIZAR_REMEDIOS:
                    usuarioService.sincronizarRemedios();
                    break;
                case NOTIFICACAO_SINCRONIZAR_PROGRAMAS:
                    usuarioService.sincronizarProgramas();
                    break;
                case NOTIFICACAO_CONFIRMAR_REMEDIO:
                    /*String remedioId = data.get("remedioId");
                    String idosoId = data.get("idosoId");

                    Intent intent = new Intent(this, ConfirmaRemedioActivity.class);
                    intent.putExtra("remedioId", remedioId);
                    intent.putExtra("idosoId", idosoId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    break;
            }

        }

        // Checa se a mensagem contém payload de notificação
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(CustomJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

}
