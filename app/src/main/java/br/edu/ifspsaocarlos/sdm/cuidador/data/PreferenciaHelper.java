package br.edu.ifspsaocarlos.sdm.cuidador.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Classe helper de acesso às shared preferences do app
 *
 * @author Anderson Canale Garcia
 */
public final class PreferenciaHelper {
    private static final String USUARIO_LOGADO = "USUARIO_LOGADO";
    private static final String USUARIO_LOGADO_PERFIL = "USUARIO_LOGADO_PERFIL";
    private static final String IDOSO_SELECIONADO = "IDOSO_SELECIONADO";
    public static final String ALARMES_REMEDIOS = ":alarmesRemedios";
    public static final String ALARMES_PROGRAMAS = ":alarmesProgramas";

    private final Context context;

    public PreferenciaHelper(Context context){
        this.context = context;
    }

    public String getUsuarioLogadoId() {
        return obterPreferenciaString(USUARIO_LOGADO);
    }

    public void setUsuarioLogadoId(String id) {
        salvarPreferencia(USUARIO_LOGADO, id);
    }

    public String getIdosoSelecionadoId() {
        return obterPreferenciaString(IDOSO_SELECIONADO);
    }

    public void setIdosoSelecionadoId(String id) {
        salvarPreferencia(IDOSO_SELECIONADO, id);
    }

    public String getUsuarioLogadoPerfil() {
        return obterPreferenciaString(USUARIO_LOGADO_PERFIL);
    }

    public void setUsuarioLogadoPerfil(String perfil) { salvarPreferencia(USUARIO_LOGADO_PERFIL, perfil); }

    public void salvarPreferencia(String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public String obterPreferenciaString(String key ){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }

    public boolean obterPreferenciaBoolean(String key, boolean def){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        boolean token = sp.getBoolean(key, def);
        return( token );
    }


    private static void setAlarmesIds(Context context, List<Integer> alarmesIds, String tagAlarme) {
        JSONArray jsonArray = new JSONArray();
        for (Integer idAlarm : alarmesIds) {
            jsonArray.put(idAlarm);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getPackageName() + tagAlarme, jsonArray.toString());

        editor.apply();
    }

    public void setAlarmeId(int id, String tagAlarme) {
        List<Integer> idsAlarms = getAlarmesIds(tagAlarme);

        if (idsAlarms.contains(id)) {
            return;
        }

        idsAlarms.add(id);

        setAlarmesIds(context, idsAlarms, tagAlarme);
    }

    public List<Integer> getAlarmesIds(String tagAlarme) {
        List<Integer> ids = new ArrayList<>();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            JSONArray jsonArray2 = new JSONArray(prefs.getString(context.getPackageName() + tagAlarme, "[]"));

            for (int i = 0; i < jsonArray2.length(); i++) {
                ids.add(jsonArray2.getInt(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }
}