package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.ContatosFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Activity responsável pelos contatos.
 *
 * @author Anderson Canale Garcia
 */

public class ContatosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contato);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction().replace(R.id.content_frame,
                ContatosFragment.newInstance(this)).commit();
    }

    public void openFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}
