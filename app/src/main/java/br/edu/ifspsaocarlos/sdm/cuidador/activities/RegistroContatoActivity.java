package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;

public class RegistroContatoActivity extends RegistroBaseActivity {
    EditText etTelefone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro_contato);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.registro_contato);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTelefone = (EditText) findViewById(R.id.registro_contato_telefone);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            etTelefone.setText(currentUser.getPhoneNumber().substring(3));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.salvar:
                String telefone = etTelefone.getText().toString().trim();

                ContatosRepository.getInstance().buscaContato(telefone, new CallbackGenerico<Contato>() {
                    @Override
                    public void OnComplete(Contato contato) {
                        if(contato != null){
                            usuarioService.registraUsuarioContato(contato.getId());
                            Intent intent = new Intent(RegistroContatoActivity.this, RegistroFotoActivity.class);
                            startActivity(intent);
                        }else {
                            etTelefone.setError(getResources().getString(R.string.msg_erro_validacao_contato));
                        }
                    }
                });
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
