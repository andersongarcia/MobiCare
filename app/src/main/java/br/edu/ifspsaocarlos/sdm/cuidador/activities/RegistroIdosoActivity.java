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

/**
 * Activity para registro de usuário do perfil Idoso
 *
 * @author Anderson Canale Garcia
 */
public class RegistroIdosoActivity extends RegistroBaseActivity {
    EditText etTelefone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro_idoso);
        super.onCreate(savedInstanceState);

        // Configura toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.registro_idoso);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencia campos do form
        etTelefone = (EditText) findViewById(R.id.registro_idoso_telefone);
        // Formatação para os campos de telefone
        BrPhoneNumberFormatter formatterTelefone = new BrPhoneNumberFormatter(new WeakReference<>(etTelefone));
        etTelefone.addTextChangedListener(formatterTelefone);
        // Define foco no campo de telefone
        etTelefone.requestFocus();
        // Força exibição do teclado
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        // Carrega dados do usuário autenticado
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

    /**
     * Implementa ações da toolbar
     * @param item item do menu clicado
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Ação salvar
            case R.id.salvar:
                // Esconde o teclado
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etTelefone.getWindowToken(), 0);
                // Lê telefone (somente números)
                final String telefone = BrPhoneNumberFormatter.onlyNumbers(etTelefone.getText().toString().trim());

                // Verifica se o idoso já está cadastrado
                ContatosRepository.getInstance().buscaIdoso(telefone, new CallbackGenerico<Boolean>() {
                    @Override
                    public void OnComplete(Boolean existe) {
                        if(existe){
                            // se idoso estiver cadastrado, registra usuário idoso
                            usuarioService.registraUsuarioIdoso(telefone);

                            // Redireciona para activity principal
                            Intent intent = new Intent(RegistroIdosoActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else {
                            // se idoso não estiver cadastrado, exibe mensagem
                            etTelefone.setError(getResources().getString(R.string.msg_erro_validacao_idoso));
                        }
                    }
                });
                break;

            // Ação voltar
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
