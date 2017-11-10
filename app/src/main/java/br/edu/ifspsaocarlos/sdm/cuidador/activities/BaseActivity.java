package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.IMService;

/**
 * Created by ander on 18/10/2017.
 */

public class BaseActivity extends AppCompatActivity {
    CuidadorService service;
    private File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean("authFirebase", true)){
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Intent intent = new Intent(this, PhoneAuthActivity.class);
                startActivity(intent);
            }
        }

        // verifica se tem usuário logado
        verificaLogado();

        // inicia classe de serviço com o contexto
        service = new CuidadorService(this);

        // inscreve usuário logado para receber notificações a ele direcionadas
        String idLogado = service.obterIdLogado();
        if(!idLogado.isEmpty()){
            IMService.subscribe(idLogado);
        }
    }

    // verifica se tem usuário logado
    public void verificaLogado() {
        CuidadorService service = new CuidadorService(this);

        if(!service.verificaUsuarioLogado()){
            // Se não estiver logado, redireciona para tela de registro do usuário
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        }else {
            // Se estiver logado, verifica se tem idoso registrado
            if(!service.verificaIdosoSelecionado()){
                // Se não tiver idoso selecionado, redireciona para registro do idoso
                Intent intent = new Intent(this, RegistroActivity.class);
                startActivity(intent);
            }
        }
    }
}