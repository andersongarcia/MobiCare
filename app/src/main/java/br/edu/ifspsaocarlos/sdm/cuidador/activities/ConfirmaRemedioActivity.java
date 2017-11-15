package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.TimePickerFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.RemediosRepository;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmaRemedioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmaRemedioActivity";
    PreferenciaHelper preferencias;

    @BindView(R.id.confirma_remedio_idoso)
    TextView tvNomeIdoso;
    @BindView(R.id.confirma_remedio_remedio)
    TextView tvNomeRemedio;
    @BindView(R.id.confirma_remedio_horario)
    EditText etHoraMedicacao;
    @BindView(R.id.confirma_remedio_proximo)
    EditText etProximaMedicacao;

    Button btConfirma;
    private String remedioId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_remedio);

        ButterKnife.bind(this);

        preferencias = new PreferenciaHelper(this);

        btConfirma = (Button) findViewById(R.id.btn_confirma_remedio);
        btConfirma.setOnClickListener(this);

        // Cria timepicker para campos de horário
        etHoraMedicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.confirma_remedio_horario);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        etProximaMedicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.confirma_remedio_proximo);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            remedioId = extras.getString("remedioId");
            String idosoId = extras.getString("idosoId");

            final Remedio remedio = RemediosRepository.getInstance().obterRemedio(remedioId);

            ContatosRepository.getInstance().buscaContato(idosoId, new CallbackGenerico<Contato>() {
                        @Override
                        public void OnComplete(Contato idoso) {
                            tvNomeIdoso.setText(idoso.getNome());
                            tvNomeRemedio.setText(remedio.getNome());
                            etHoraMedicacao.setText(remedio.getHorario());
                            etProximaMedicacao.setText(remedio.calculaProximoHorario());
                        }
                    });

        }
    }

    @Override
    public void onClick(View view) {
        if(!remedioId.isEmpty()){
            Log.d(TAG, "Confirmando remédio " + tvNomeRemedio.getText());
            String horaMedicacao = etHoraMedicacao.getText().toString();
            String proximaMedicacao = etProximaMedicacao.getText().toString();

            RemediosRepository.getInstance().confirmarHorario(preferencias.getIdosoSelecionadoId(), remedioId, horaMedicacao, proximaMedicacao);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
