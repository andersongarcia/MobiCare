package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

public class ConfirmaRemedioActivity extends AppCompatActivity {

    TextView tvNomeIdoso;
    TextView tvNomeRemedio;
    EditText tvHoraMedicacao;
    EditText tvProximaMedicacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_remedio);

        tvNomeIdoso = (TextView) findViewById(R.id.confirma_remedio_idoso);
        tvNomeRemedio = (TextView) findViewById(R.id.confirma_remedio_remedio);
        tvHoraMedicacao = (EditText) findViewById(R.id.confirma_remedio_horario);
        tvProximaMedicacao = (EditText) findViewById(R.id.confirma_remedio_proximo);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            
        }
    }
}
