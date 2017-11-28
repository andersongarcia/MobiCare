package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.util.BrPhoneNumberFormatter;

public class RegistroIdosoActivity extends RegistroBaseActivity {
    EditText etTelefone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro_idoso);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.registro_idoso);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTelefone = (EditText) findViewById(R.id.registro_idoso_telefone);
        BrPhoneNumberFormatter formatterTelefone = new BrPhoneNumberFormatter(new WeakReference<>(etTelefone));
        etTelefone.addTextChangedListener(formatterTelefone);
        etTelefone.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

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
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etTelefone.getWindowToken(), 0);
                final String telefone = BrPhoneNumberFormatter.onlyNumbers(etTelefone.getText().toString().trim());
                ContatosRepository.getInstance().buscaIdoso(telefone, new CallbackGenerico<Boolean>() {
                    @Override
                    public void OnComplete(Boolean existe) {
                        if(existe){
                            usuarioService.registraUsuarioIdoso(telefone);
                            Intent intent = new Intent(RegistroIdosoActivity.this, RegistroFotoActivity.class);
                            startActivity(intent);
                        }else {
                            etTelefone.setError(getResources().getString(R.string.msg_erro_validacao_idoso));
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
