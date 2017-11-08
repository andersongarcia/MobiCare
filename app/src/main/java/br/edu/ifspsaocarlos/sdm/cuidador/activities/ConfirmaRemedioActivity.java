package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.TimePickerFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

public class ConfirmaRemedioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmaRemedioActivity";
    TextView tvNomeIdoso;
    TextView tvNomeRemedio;
    EditText etHoraMedicacao;
    EditText etProximaMedicacao;

    Button btConfirma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_remedio);

        CuidadorService service = new CuidadorService(this);

        tvNomeIdoso = (TextView) findViewById(R.id.confirma_remedio_idoso);
        tvNomeRemedio = (TextView) findViewById(R.id.confirma_remedio_remedio);
        etHoraMedicacao = (EditText) findViewById(R.id.confirma_remedio_horario);
        etProximaMedicacao = (EditText) findViewById(R.id.confirma_remedio_proximo);

        btConfirma = (Button) findViewById(R.id.btn_confirma_remedio);
        btConfirma.setOnClickListener(this);

        // Cria timepicker para campos de horário
        etHoraMedicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.remedio_horario);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        etProximaMedicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.remedio_horario);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String remedioId = extras.getString("remedioId");
            String idosoId = extras.getString("idosoId");

            Remedio remedio = service.obterRemedio(remedioId);
            Contato idoso = service.obterContato(idosoId);

            //tvNomeIdoso.setText(idoso.getNome());
            tvNomeRemedio.setText(remedio.getNome());
            etHoraMedicacao.setText(remedio.getHorario());
            etProximaMedicacao.setText(remedio.calculaProximoHorario());
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "Confirmando remédio " + tvNomeRemedio.getText());
    }
}
