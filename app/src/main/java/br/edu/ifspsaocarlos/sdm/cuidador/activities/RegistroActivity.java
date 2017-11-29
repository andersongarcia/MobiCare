package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;


/**
 * Activity para seleção do perfil de registro do usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroActivity extends RegistroBaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro);
        super.onCreate(savedInstanceState);

        // Exibe lista com os perfis do sistema
        ListView listView = (ListView) findViewById(R.id.lv_perfis);
        List<String> perfis = Arrays.asList( Usuario.CUIDADOR, Usuario.IDOSO, Usuario.CONTATO);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, perfis);
        listView.setAdapter(adapter);

        // Define a ação de abrir a activity correspondente para cada perfil da lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                String perfil = (String)adapterView.getItemAtPosition(position);

                switch (perfil){
                    case Usuario.CUIDADOR:
                        Intent intentCuidador = new Intent(RegistroActivity.this, RegistroCuidadorActivity.class);
                        startActivity(intentCuidador);
                        break;
                    case Usuario.IDOSO:
                        Intent intentIdoso = new Intent(RegistroActivity.this, RegistroIdosoActivity.class);
                        startActivity(intentIdoso);
                        break;
                    case Usuario.CONTATO:
                        Intent intentContato = new Intent(RegistroActivity.this, RegistroContatoActivity.class);
                        startActivity(intentContato);
                        break;
                }
            }
        });
    }
}