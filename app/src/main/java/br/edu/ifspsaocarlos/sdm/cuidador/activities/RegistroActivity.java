package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.RegistroPerfilFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;


/**
 * Activity para registro do usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroActivity extends AppCompatActivity {

    private CuidadorService cuidadorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        cuidadorService = new CuidadorService(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        abrirFragment(RegistroPerfilFragment.newInstance());
    }

    public void abrirFragment(Fragment fragment){
        getFragmentManager().beginTransaction().replace(R.id.frame_registro, fragment).commit();
    }

    public CuidadorService getCuidadorService() {
        return cuidadorService;
    }
}