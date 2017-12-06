package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.lang.ref.WeakReference;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.util.BrPhoneNumberFormatter;

/**
 * Activity para registro de usuário do perfil Cuidador
 *
 * @author Anderson Canale Garcia
 */
public class RegistroCuidadorActivity extends RegistroBaseActivity implements Validator.ValidationListener {
    //region Constantes
    private static final String CUIDADOR_NOME = ":cuidadorNome";
    private static final String CUIDADOR_TELEFONE = ":cuidadorTelefone";
    private static final String IDOSO_NOME = ":idosoNome";
    private static final String IDOSO_TELEFONE = ":idosoTelefone";
    //endregion

    //region Campos do form
    @NotEmpty(messageResId = R.string.msg_erro_nome_invalido)
    EditText etNome;
    @NotEmpty(messageResId = R.string.msg_erro_telefone_invalido)
    EditText etTelefone;
    @NotEmpty(messageResId = R.string.msg_erro_nome_invalido)
    EditText etNomeIdoso;
    @NotEmpty(messageResId = R.string.msg_erro_telefone_invalido)
    EditText etTelefoneIdoso;

    private Validator validator;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro_cuidador);
        super.onCreate(savedInstanceState);

        // Configura toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.registro_cuidador);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencia campos do form
        etNome = (EditText)findViewById(R.id.registro_cuidador_nome);
        etTelefone = (EditText)findViewById(R.id.registro_cuidador_telefone);
        etNomeIdoso = (EditText)findViewById(R.id.registro_idoso_nome);
        etTelefoneIdoso = (EditText)findViewById(R.id.registro_idoso_telefone);

        // Validação
        validator = new Validator(this);
        validator.setValidationListener(this);

        // Formatação para os campos de telefone
        BrPhoneNumberFormatter formatterTelefone = new BrPhoneNumberFormatter(new WeakReference<>(etTelefone));
        etTelefone.addTextChangedListener(formatterTelefone);
        BrPhoneNumberFormatter formatterTelefoneIdoso = new BrPhoneNumberFormatter(new WeakReference<>(etTelefoneIdoso));
        etTelefoneIdoso.addTextChangedListener(formatterTelefoneIdoso);

        // Recupera dados da instância
        if(savedInstanceState != null){
            etNome.setText(savedInstanceState.getString(CUIDADOR_NOME));
            etTelefone.setText(savedInstanceState.getString(CUIDADOR_TELEFONE));
            etNomeIdoso.setText(savedInstanceState.getString(IDOSO_NOME));
            etTelefoneIdoso.setText(savedInstanceState.getString(IDOSO_TELEFONE));
        }

        // Carrega dados do usuário autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            etNome.setText(currentUser.getDisplayName());
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
                validator.validate();
                break;

            // Ação voltar
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Salva instância quando atividade sai da tela
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(CUIDADOR_NOME, etNome.getText().toString().trim());
        outState.putString(CUIDADOR_TELEFONE, BrPhoneNumberFormatter.onlyNumbers(etTelefone.getText().toString().trim()));
        outState.putString(IDOSO_NOME, etNomeIdoso.getText().toString().trim());
        outState.putString(IDOSO_TELEFONE, BrPhoneNumberFormatter.onlyNumbers( etTelefoneIdoso.getText().toString().trim()));
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onValidationSucceeded() {
        // Lê campos do form
        String nome = etNome.getText().toString().trim();
        String telefone = BrPhoneNumberFormatter.onlyNumbers(
                etTelefone.getText().toString().trim()); // traz apenas números para telefone
        String nomeIdoso = etNomeIdoso.getText().toString().trim();
        String telefoneIdoso = BrPhoneNumberFormatter.onlyNumbers(
                etTelefoneIdoso.getText().toString().trim()); // traz apenas números para telefone

        // Registra dados do cuidador (usuário) e do idoso
        usuarioService.registraCuidadorIdoso(nome, telefone, nomeIdoso, telefoneIdoso);

        // Redireciona para activity de foto de perfil
        Intent intent = new Intent(RegistroCuidadorActivity.this, RegistroFotoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
