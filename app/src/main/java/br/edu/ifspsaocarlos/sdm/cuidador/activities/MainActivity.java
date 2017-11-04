package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.os.Bundle;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.ChatFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.ChatIdosoFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);


        getSupportActionBar().setTitle(R.string.app_name);

        //IMService.getToken(this);
        //AlarmeReceiver alarm = new AlarmeReceiver();
        //alarm.defineAlarmeRecorrente(this);

        // Verifica perfil
        switch (service.obterPerfilLogado()){
            case Usuario.CUIDADOR:
                service.carregaListas();
                openFragment(ChatFragment.newInstance());
                break;
            case Usuario.IDOSO:
                openFragment(ChatIdosoFragment.newInstance(null));
                break;
            case Usuario.CONTATO:
                break;
        }
    }



}
