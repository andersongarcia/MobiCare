package br.edu.ifspsaocarlos.sdm.cuidador.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import br.edu.ifspsaocarlos.sdm.cuidador.activities.IdosoActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService.NO;

/**
 * Created by ander on 30/10/2017.
 */

public class AlarmeReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmeReceiver";
    final public static String ONE_TIME = "onetime";
    final public static String TARGET = "target";
    private static final String DADOS = "dados";

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
                    intent.putExtra(TARGET, NO.MENSAGENS);
                    intent.putExtra(String.valueOf(NO.MENSAGENS), mensagem);
                    break;
            }
        }

        context.startActivity(newIntent);

        //Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        //Release the lock
        wl.release();
    }

    public void setAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmeReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmeReceiver.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

    public void startActivityOnetime(Context context, NO no){
        setOnetimeTimer(context);
    }

    public void setNovaMensagem(Context context, Mensagem mensagem) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmeReceiver.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TARGET, NO.MENSAGENS);
        intent.putExtra(String.valueOf(NO.MENSAGENS), mensagem);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }
}
