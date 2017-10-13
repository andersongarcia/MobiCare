package br.edu.ifspsaocarlos.sdm.cuidador.data;

import android.content.Context;
import android.content.SharedPreferences;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Classe helper de acesso às shared preferences do app
 *
 * @author Anderson Canale Garcia
 */
public final class PreferenciaHelper {
    private static final String USUARIO_LOGADO = "USUARIO_LOGADO";
    private static final String IDOSO_SELECIONADO = "IDOSO_SELECIONADO";
    private final Context context;

    public PreferenciaHelper(Context context){
        this.context = context;
    }

    public String getUsuarioLogadoId() {
        return obterPreferencia(USUARIO_LOGADO);
    }

    public void setUsuarioLogadoId(String id) {
        salvarPreferencia(USUARIO_LOGADO, id);
    }

    public String getIdosoSelecionadoId() {
        return obterPreferencia(IDOSO_SELECIONADO);
    }

    public void setIdosoSelecionadoId(String id) {
        salvarPreferencia(IDOSO_SELECIONADO, id);
    }

    public void salvarPreferencia(String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public String obterPreferencia(String key ){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }

}