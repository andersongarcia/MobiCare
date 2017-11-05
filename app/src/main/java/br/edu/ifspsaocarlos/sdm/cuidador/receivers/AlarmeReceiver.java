package br.edu.ifspsaocarlos.sdm.cuidador.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import br.edu.ifspsaocarlos.sdm.cuidador.activities.IdosoActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService.NO;

/**
 * Created by ander on 30/10/2017.
 */

public class AlarmeReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmeReceiver";
    private static final String DADOS = "dados";
    private  static final int REQUEST_ALARM_DEFAULT = 0;

    final public static String ONE_TIME = "onetime";
    final public static String TARGET = "target";
    private static final String REAGENDA = "reagenda";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        //Acquire the lock
        wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        Intent newIntent = new Intent(context, IdosoActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(extras != null){
            NO no = (NO)extras.get(TARGET);
            switch (no){
                case MENSAGENS:
                    Mensagem mensagem = (Mensagem) extras.get(String.valueOf(NO.MENSAGENS));
                    newIntent.putExtra(TARGET, NO.MENSAGENS);
                    newIntent.putExtra(String.valueOf(NO.MENSAGENS), mensagem);
                    break;
            }
        }

        context.startActivity(newIntent);

        //Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        //Release the lock
        wl.release();
    }

    public void cancelaAlarme(Context context, int requestCode)
    {
        Intent intent = new Intent(context, AlarmeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void defineAlarmeRecorrente(Context context, int requestCode, String horario, int recorrencia)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmeReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);

        // calcula intervalo do primeiro alarme
        Calendar cal = calculaIntervalo(horario);
        // calcula intervalo de recorrência
        long intervaloRecorrencia = TimeUnit.HOURS.toMillis(recorrencia);

        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), intervaloRecorrencia , pi);
    }

    public void defineAlarmeUnico(Context context, int requestCode, String horario, boolean deveAjustarProximo){
        // calcula intervalo do alarme
        Calendar cal = calculaIntervalo(horario);
        defineAlarmeUnico(context, requestCode, cal, deveAjustarProximo);
    }

    public void defineAlarmeUnico(Context context, int requestCode, Calendar agenda, boolean deveAjustarProximo) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmeReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        intent.putExtra(REAGENDA, deveAjustarProximo);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, agenda.getTimeInMillis(), pi);
    }

    public void mostraNovaMensagem(Context context, Mensagem mensagem) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmeReceiver.class);
        intent.putExtra(TARGET, NO.MENSAGENS);
        intent.putExtra(String.valueOf(NO.MENSAGENS), mensagem);
        PendingIntent pi = PendingIntent.getBroadcast(context, REQUEST_ALARM_DEFAULT, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

    private Calendar calculaIntervalo(String horario) {
        String array[];
        array = horario.split(":");
        int hora = Integer.parseInt(array[0]);
        int minuto = Integer.parseInt(array[1]);

        Calendar agora = new GregorianCalendar();
        agora.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, agora.get(Calendar.DAY_OF_YEAR));
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
