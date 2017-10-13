package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.ProgramasFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

public class ProgramasActivity extends AppCompatActivity {
    public static List<Programa> programas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        programas = CuidadorFirebaseRepository.getInstance().getProgramas();

        getFragmentManager().beginTransaction().replace(R.id.frame_programas,
                ProgramasFragment.newInstance(this)).commit();
    }

    public void openFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.frame_programas, fragment).commit();
    }
}
