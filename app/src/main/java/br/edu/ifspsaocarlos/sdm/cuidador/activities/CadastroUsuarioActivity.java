package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                String telefone = ((TextView)findViewById(R.id.cadastro_telefone)).getText().toString();
                String nome = ((TextView)findViewById(R.id.cadastro_nome)).getText().toString();
                registrarUsuario(telefone, nome);
                break;
            case android.R.id.home:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registrarUsuario(String telefone, String nome) {

        // busca usuário na base
        CuidadorRepository dao = new CuidadorRepository(this);
        Usuario usuario = dao.buscaUsuarioPeloTelefone(telefone);

        // se usuário ainda não exite, insere
        if(usuario == null){
            usuario = dao.criarUsuario(nome, telefone);
        }

        // define usuário como usuário logado
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.chaveUsuarioLogado), usuario.getContato().getTelefone());
        editor.commit();

        // verifica se usuário possui ao menos um idoso registrado
        if(usuario.getIdosos().size() == 0){
            // se não houver, redireciona para activity de cadastro do idoso
            Intent idosoIntent = new Intent(this, CadastroIdosoActivity.class);
            startActivity(idosoIntent);
        }else {
            Intent menuIntent = new Intent(this, MenuActivity.class);
            startActivity(menuIntent);
        }
    }

}
