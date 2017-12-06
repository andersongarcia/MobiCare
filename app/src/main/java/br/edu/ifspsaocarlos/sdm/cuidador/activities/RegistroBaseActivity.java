package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;

/**
 * Activity base para todas as telas de registro de usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroBaseActivity extends AppCompatActivity {
    protected UsuarioService usuarioService;
    protected Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean("authFirebase", true)){
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Intent intent = new Intent(this, AutenticaSMSActivity.class);
                startActivity(intent);
                finish();
            }
        }

        usuarioService = new UsuarioService(this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
