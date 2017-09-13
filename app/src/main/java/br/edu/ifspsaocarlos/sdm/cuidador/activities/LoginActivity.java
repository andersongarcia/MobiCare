package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.util.Util;


/**
 * Activity responsável pelo login do usuário.
 *
 * @author Anderson Canale Garcia
 */
public class LoginActivity extends Activity {

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

            if (validarUsuario(telefone)) {
                Toast.makeText(getApplicationContext(), "Login feito com sucesso!.", Toast.LENGTH_LONG).show();

                redirecionarActivityPrincipal();
            } else {
                mensagemErro.setText(R.string.msg_erro_validacao_usuario);
                //fgToast.makeText(getApplicationContext(), "Por favor, digite um apelido de usuário válido.", Toast.LENGTH_LONG).show();
            }
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
    private boolean validarUsuario(String telefone) {

        if (telefone == null || telefone.isEmpty()) {
            return false;
        }

        CuidadorRepository dao = new CuidadorRepository(this);
        Usuario usuario = dao.buscaUsuarioPeloTelefone(telefone);

        if(usuario == null){
            mensagemErro.setText(R.string.msg_usuario_nao_encontrado);
            return false;
        }else {
            usuarioLogado = usuario;
            return true;
        }
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
