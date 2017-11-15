package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.IMService;

/**
 * Created by ander on 18/10/2017.
 */

public class BaseActivity extends AppCompatActivity {
    CuidadorService service;
    PreferenciaHelper preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencias = new PreferenciaHelper(this);

        // inicia classe de serviço com o contexto
        service = new CuidadorService(this);

        // inscreve usuário logado para receber notificações a ele direcionadas
        String idLogado = service.obterIdLogado();
        if(!idLogado.isEmpty()){
            IMService.subscribe(idLogado);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(preferencias.obterPreferenciaBoolean("authFirebase", true)){
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Intent intent = new Intent(this, PhoneAuthActivity.class);
                startActivity(intent);
            }
        }

        // verifica se tem usuário logado
        verificaLogado();
    }

    // verifica se tem usuário logado
    public void verificaLogado() {
        CuidadorService service = new CuidadorService(this);

        Intent intent;
        if(!service.verificaUsuarioLogado()){
            // Se não estiver logado, redireciona para tela de registro do usuário
            intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        }else {
            // Se estiver logado, verifica se tem idoso registrado
            if(!service.verificaIdosoSelecionado()){
                // Se não tiver idoso selecionado, redireciona para registro do idoso
                intent = new Intent(this, RegistroActivity.class);
                startActivity(intent);
            }
        }
    }

    public PreferenciaHelper getPreferencias() {
        return preferencias;
    }
}