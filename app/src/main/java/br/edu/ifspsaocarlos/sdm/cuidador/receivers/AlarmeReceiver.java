package br.edu.ifspsaocarlos.sdm.cuidador.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import br.edu.ifspsaocarlos.sdm.cuidador.activities.IdosoActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.util.DatetimeHelper;

/**
 * Receiver para disparos do alarme
 *
 * @author Anderson Canale Garcia
 */
public class AlarmeReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmeReceiver";
    private static final String DADOS = "dados";
    private  static final int REQUEST_ALARM_DEFAULT = 0;

    final public static String ONE_TIME = "onetime";
    final public static String TARGET = "target";
    private static final String REAGENDA = "reagenda";
    private static final String BUNDLE = "bundle";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        Log.d(TAG, "Alarme disparado");

        //Acquire the lock
        wl.acquire();

        // Lê dados do bundle
        Bundle bundle = intent.getBundleExtra(BUNDLE);

        // Cria instância do intent para tela do idoso
        Intent newIntent = new Intent(context, IdosoActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Se bundle não for nulo, lê dados da mensagem e envia para tela do idoso
        if(bundle != null){
            IMensagem mensagem = (IMensagem) bundle.getSerializable(NO.getNo(NO.MENSAGENS));
            Bundle newBundle = new Bundle();
            newBundle.putSerializable(NO.getNo(NO.MENSAGENS), mensagem);
            newIntent.putExtra(BUNDLE, newBundle);
            context.startActivity(newIntent);
        }


        //Release the lock
        wl.release();
    }

    /**
     * Cancela alarme pelo request code
     * @param context Contexto
     * @param requestCode Código de requisição (request code). Identifica o alarme a ser cancelado.
     */
    public void cancelaAlarme(Context context, int requestCode)
    {
        Intent intent = new Intent(context, AlarmeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    /**
     * Agenda alarme para executar de acordo com periodicidade definida
     * @param context Contexto
     * @param mensagem Mensagem a ser exibida a cada disparo
     * @param requestCode Código de requisição (request code). Identifica o alarme criado.
     * @param horario Horário do primeiro alarme
     * @param recorrencia Intervalo de recorrência em horas
     */
    public void defineAlarmeRecorrente(Context context, IMensagem mensagem, int requestCode, String horario, int recorrencia)
    {
        // Se horário estiver vazio, não agenda o alarme
        if(horario.isEmpty()){
            Log.d(TAG, "Horário indefinido para criação de alarme");
            return;
        }

        // Recupera gerenciador de alarmes do contexto
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Inclui mensagem no budle
        Bundle bundle = new Bundle();
        bundle.putSerializable(NO.getNo(NO.MENSAGENS), mensagem);
        bundle.putBoolean(ONE_TIME, Boolean.FALSE);

        // Cria instância para a própria classe para atender aos disparos do alarme
        Intent intent = new Intent(context, AlarmeReceiver.class);
        intent.putExtra(BUNDLE, bundle);

        // Pendura intent a ser executada no disparo do alarme
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // calcula intervalo do primeiro alarme
        Calendar cal = calculaIntervalo(horario);
        Log.d(TAG, "primeiro alarme definido para " + cal.get(Calendar.DATE));
        // calcula intervalo de recorrência
        long intervaloRecorrencia = TimeUnit.HOURS.toMillis(recorrencia);

        // Agenda alarme de acordo com o horário e intervalo estipulados
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), intervaloRecorrencia , pi);

        Log.d(TAG, "Novo alarme único definido para " + cal.toString());
    }

    /**
     * Agenda alarme para executar uma única vez
     * @param context Contexto
     * @param mensagem Mensagem a ser exibida a cada disparo
     * @param requestCode Código de requisição (request code). Identifica o alarme criado.
     * @param horario Horário do alarme único
     * @param deveAjustarProximo Flag para dizer se próximo horário depende de reagendamento
     */
    public void defineAlarmeUnico(Context context, IMensagem mensagem, int requestCode,
                                  String horario, boolean deveAjustarProximo){
        if(horario.isEmpty()){
            Log.d(TAG, "Horário indefinido para criação de alarme");
            return;
        }

        // calcula intervalo do alarme
        Calendar cal = calculaIntervalo(horario);
        //Calendar cal = new GregorianCalendar();  // testes para execução imediata
        defineAlarmeUnico(context, mensagem, requestCode, cal, deveAjustarProximo);
    }

     /**
     * Agenda alarme para executar uma única vez
     * @param context Contexto
     * @param mensagem Mensagem a ser exibida a cada disparo
     * @param requestCode Código de requisição (request code). Identifica o alarme criado.
     * @param agenda Horário do alarme único
     * @param deveAjustarProximo Flag para dizer se próximo horário depende de reagendamento
     */
    public void defineAlarmeUnico(Context context, IMensagem mensagem, int requestCode,
                                  Calendar agenda, boolean deveAjustarProximo) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Inclui mensagem no budle
        Bundle bundle = new Bundle();
        bundle.putSerializable(NO.getNo(NO.MENSAGENS), mensagem);
        bundle.putBoolean(REAGENDA, deveAjustarProximo);

        // Cria instância para a própria classe para atender aos disparos do alarme
        Intent intent = new Intent(context, AlarmeReceiver.class);
        intent.putExtra(BUNDLE, bundle);

        // Pendura intent a ser executada no disparo do alarme
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, agenda.getTimeInMillis(), pi);

        Log.d(TAG, "Novo alarme único definido para " +
                DatetimeHelper.getFormatedDate(agenda, "dd/MM/yyyy HH:mm"));
    }

    public void mostraNovaMensagem(Context context, Mensagem mensagem) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Inclui mensagem no budle
        Bundle bundle = new Bundle();
        bundle.putSerializable(NO.getNo(NO.MENSAGENS), mensagem);

        // Cria instância para a própria classe para atender aos disparos do alarme
        Intent intent = new Intent(context, AlarmeReceiver.class);
        intent.putExtra(BUNDLE, bundle);

        // Pendura intent a ser executada no disparo do alarme
        PendingIntent pi = PendingIntent.getBroadcast(context, REQUEST_ALARM_DEFAULT, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);

        Log.d(TAG, "Nova mensagem enviada");
    }

    /**
     * Calcula intervalo entre o agora e o horário definido
     * @param horario O horário com o qual se deseja comparar
     * @return Objeto com o intervalo
     */
    private Calendar calculaIntervalo(String horario) {
        String array[];
        array = horario.split(":");
        int hora = Integer.parseInt(array[0]);
        int minuto = Integer.parseInt(array[1]);

        // Pega o horário atual
        Calendar agora = new GregorianCalendar();
        agora.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, agora.get(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DATE, agora.get(Calendar.DATE));
        cal.set(Calendar.MONTH, agora.get(Calendar.MONTH));
        if(cal.before(agora)){
            cal.add(Calendar.DATE, 1);
        }

        return cal;
    }
}
