package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.util.BrPhoneNumberFormatter;

public class RegistroCuidadorActivity extends RegistroBaseActivity {
    private static final String CUIDADOR_NOME = ":cuidadorNome";
    private static final String CUIDADOR_TELEFONE = ":cuidadorTelefone";
    private static final String IDOSO_NOME = ":idosoNome";
    private static final String IDOSO_TELEFONE = ":idosoTelefone";

    EditText etNome;
    EditText etTelefone;
    EditText etNomeIdoso;
    EditText etTelefoneIdoso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro_cuidador);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.registro_cuidador);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNome = (EditText)findViewById(R.id.registro_cuidador_nome);
        etTelefone = (EditText)findViewById(R.id.registro_cuidador_telefone);
        etNomeIdoso = (EditText)findViewById(R.id.registro_idoso_nome);
        etTelefoneIdoso = (EditText)findViewById(R.id.registro_idoso_telefone);

        BrPhoneNumberFormatter formatterTelefone = new BrPhoneNumberFormatter(new WeakReference<>(etTelefone));
        etTelefone.addTextChangedListener(formatterTelefone);
        BrPhoneNumberFormatter formatterTelefoneIdoso = new BrPhoneNumberFormatter(new WeakReference<>(etTelefoneIdoso));
        etTelefoneIdoso.addTextChangedListener(formatterTelefoneIdoso);

        if(savedInstanceState != null){
            etNome.setText(savedInstanceState.getString(CUIDADOR_NOME));
            etTelefone.setText(savedInstanceState.getString(CUIDADOR_TELEFONE));
            etNomeIdoso.setText(savedInstanceState.getString(IDOSO_NOME));
            etTelefoneIdoso.setText(savedInstanceState.getString(IDOSO_TELEFONE));
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.salvar:
                String nome = etNome.getText().toString().trim();
                String telefone = BrPhoneNumberFormatter.onlyNumbers(etTelefone.getText().toString().trim());
                String nomeIdoso = etNomeIdoso.getText().toString().trim();
                String telefoneIdoso = BrPhoneNumberFormatter.onlyNumbers( etTelefoneIdoso.getText().toString().trim());

                usuarioService.registraCuidadorIdoso(nome, telefone, nomeIdoso, telefoneIdoso);

                Intent intent = new Intent(RegistroCuidadorActivity.this, RegistroFotoActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(CUIDADOR_NOME, etNome.getText().toString().trim());
        outState.putString(CUIDADOR_TELEFONE, BrPhoneNumberFormatter.onlyNumbers(etTelefone.getText().toString().trim()));
        outState.putString(IDOSO_NOME, etNomeIdoso.getText().toString().trim());
        outState.putString(IDOSO_TELEFONE, BrPhoneNumberFormatter.onlyNumbers( etTelefoneIdoso.getText().toString().trim()));
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
