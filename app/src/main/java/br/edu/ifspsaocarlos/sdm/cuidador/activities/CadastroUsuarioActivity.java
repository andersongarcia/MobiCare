package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Activity de cadastro de usuário
 *
 * @author Anderson Canale Garcia
 */
public class CadastroUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.registro_usuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_cadastro ,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.salvar:
                String telefone = ((EditText)findViewById(R.id.cadastro_telefone)).getText().toString();
                String nome = ((EditText)findViewById(R.id.cadastro_nome)).getText().toString();
                RadioGroup rg = (RadioGroup)findViewById(R.id.cadastro_perfil);
                String perfil =  ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                registrarUsuario(telefone, nome, perfil);
                break;
            case android.R.id.home:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registrarUsuario(String telefone, String nome, String perfil) {
        CuidadorService service = new CuidadorService(this);

        // se usuário ainda não exite, insere
        if(!service.verificaUsuarioLogado()){
            service.registrarUsuario(nome, telefone, perfil);
        }

        // verifica se usuário possui ao menos um idoso registrado
        if(!service.verificaIdosoSelecionado()){
            // se não houver, redireciona para activity de cadastro do idoso
            Intent idosoIntent = new Intent(this, CadastroIdosoActivity.class);
            startActivity(idosoIntent);
        }else {
            Intent menuIntent = new Intent(this, MenuActivity.class);
            startActivity(menuIntent);
        }
    }

}
