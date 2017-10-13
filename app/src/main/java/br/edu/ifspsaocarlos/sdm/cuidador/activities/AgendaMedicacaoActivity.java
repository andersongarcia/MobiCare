package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.MedicacoesFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Activity responsável peloas medicacões.
 *
 * @author Anderson Canale Garcia
 */
public class AgendaMedicacaoActivity extends AppCompatActivity {

    public static List<Medicacao> medicacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_medicacao);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        medicacoes = CuidadorFirebaseRepository.getInstance().getMedicacoes();

        getFragmentManager().beginTransaction().replace(R.id.frame_medicacao,
                MedicacoesFragment.newInstance(this)).commit();
    }

    public void openFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.frame_medicacao, fragment).commit();
    }

}
