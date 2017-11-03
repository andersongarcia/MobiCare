package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.ChatIdosoFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

public class IdosoActivity extends BaseActivity {
    private RecyclerView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_idoso);
        super.onCreate(savedInstanceState);

        final Window win= getWindow(); win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Mensagem mensagem = (Mensagem) extras.get(String.valueOf(CuidadorService.NO.MENSAGENS));
            openFragment(ChatIdosoFragment.newInstance(mensagem));
        }

    }
}
