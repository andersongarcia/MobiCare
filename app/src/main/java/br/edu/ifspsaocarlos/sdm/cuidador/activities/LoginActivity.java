package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.util.Util;

/**
 * Activity responsável pelo login do usuário.
 *
 * @author Anderson Canale Garcia
 */
public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    // Objeto de progresso de diálogo.
    ProgressDialog prgDialogo;
    // Mensagem de erro Text View.
    TextView mensagemErro;
    // ID Edit View.
    EditText idET;
    //Usuário logado no sistema.
    private Usuario usuarioLogado = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        // Referência ao Text View de mensagem de erro
        mensagemErro = (TextView) findViewById(R.id.login_erro);

        // Referência ao Edit View do ID do usuário
        idET = (EditText) findViewById(R.id.login);

        // Referência ao objeto de diálogo de progresso
        prgDialogo = new ProgressDialog(this);

        prgDialogo.setMessage("Por favor, aguarde...");

        prgDialogo.setCancelable(false);
    }

    /**
     * Método chamado quando o botão de login for selecionado.
     */
    public void loginUsuario(View view) {

        String telefone = idET.getText().toString();

        // Faz uma pré validação para verificar se o ID está dentro dos padrões exigidos
        if (Util.isNotNull(telefone)) {
            validarUsuario(telefone);
        }
        // Caso algum Edit View tenha ficado em branco.
        else {
            mensagemErro.setText(R.string.msg_telefone_requerido);
        }
    }

    /**
     * Verifica se o usuário é válido.
     *
     * @param telefone O telefone do usuário que pretende fazer login no sistema
     * @return true caso o usuário exista, false caso contrário
     */
    private void validarUsuario(String telefone) {

        if (telefone == null || telefone.isEmpty()) {
            return;
        }

        Query query = CuidadorFirebaseRepository.getInstance().obterReferenciaUsuario(telefone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                Log.d(TAG, "User name: " + usuario.getContato().getNome() + ", telefone " + usuario.getContato().getTelefone());

                if(usuario == null){
                    mensagemErro.setText(R.string.msg_usuario_nao_encontrado);
                }else {
                    usuarioLogado = usuario;
                    Toast.makeText(getApplicationContext(), "Login feito com sucesso!.", Toast.LENGTH_LONG).show();
                    redirecionarActivityPrincipal();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Erro ao ler usuário.", error.toException());
            }
        });
    }

    /**
     * Redireciona para a tela Home.
     */
    public void redirecionarActivityPrincipal() {

/*        Bundle bundle = new Bundle();
        bundle.putParcelable("usuarioLogado", finalUsuarios);*/

        Intent homeIntent = new Intent(getApplicationContext(), MenuActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.putExtra("contatoLogado", usuarioLogado);

        startActivity(homeIntent);
    }

    /**
     * Método invocado para cadastrar um novo usuário.
     */
    public void enviarNovaSenha(View view) {

        Intent cadastroIntent = new Intent(getApplicationContext(), CadastroUsuarioActivity.class);
        cadastroIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(cadastroIntent);
    }

}
