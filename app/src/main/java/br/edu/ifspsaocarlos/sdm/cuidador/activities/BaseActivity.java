package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.IMService;

/**
 * Activity base
 *
 * @author Anderson Canale Garcia
 */
public class BaseActivity extends AppCompatActivity {
    UsuarioService service;
    PreferenciaHelper preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencias = new PreferenciaHelper(this);

        // inicia classe de serviço com o contexto
        service = new UsuarioService(this);

        // inscreve usuário logado para receber notificações a ele direcionadas
        String idLogado = service.obterIdLogado();
        if(!idLogado.isEmpty()){
            IMService.subscribe(idLogado);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(preferencias.obterPreferenciaBoolean("authFirebase", false)){
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Intent intent = new Intent(this, AutenticaSMSActivity.class);
                startActivity(intent);
                finish();
            }
        }

        // verifica se tem usuário logado
        verificaLogado();
    }

    // verifica se tem usuário logado
    public void verificaLogado() {
        UsuarioService service = new UsuarioService(this);

        Intent intent;
        if(!service.verificaUsuarioLogado()){
            // Se não estiver logado, redireciona para tela de registro do usuário
            intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
            finish();
        }else {
            // Se estiver logado, verifica se tem idoso registrado
            if(!service.verificaIdosoSelecionado()){
                // Se não tiver idoso selecionado, redireciona para registro do idoso
                intent = new Intent(this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public PreferenciaHelper getPreferencias() {
        return preferencias;
    }
}