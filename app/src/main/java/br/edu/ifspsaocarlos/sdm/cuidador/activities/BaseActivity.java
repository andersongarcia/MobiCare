package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Created by ander on 18/10/2017.
 */

public class BaseActivity extends AppCompatActivity {
    CuidadorService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = new CuidadorService(this);
    }

    public void openFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
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
