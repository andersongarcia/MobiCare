package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.os.Bundle;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.ChatFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.ChatIdosoFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.IMService;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);


        getSupportActionBar().setTitle(R.string.app_name);

        IMService.subscribe("-KxFu5wMTkjf1lZ94RBq");
        //IMService.getToken(this);
        //AlarmeReceiver alarm = new AlarmeReceiver();
        //alarm.setAlarm(this);

        // Verifica perfil
        switch (service.obterPerfilLogado()){
            case Usuario.CUIDADOR:
                service.carregarListas();
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
