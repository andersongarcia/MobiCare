package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.MenuItemAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.data.MenuItemLista;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

/**
 * Activity do menu principal
 *
 * @author Anderson Canale Garcia
 */
public class MenuActivity extends AppCompatActivity {

    RecyclerView menuRecyclerView;
    private MenuItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // verifica se tem usuário logado
        verificaLogado();

        setContentView(R.layout.activity_menu);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(R.string.app_name);

        //mToolbar.setTitle(R.string.app_name);
        //mToolbar.setSubtitle(");

        /*mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = (String) item.getTitle();
                Toast.makeText(MenuActivity.this, title + " Selected !", Toast.LENGTH_SHORT).show();

                return true;
            }
        });*/

        menuRecyclerView = (RecyclerView)findViewById(R.id.recycleView);

        setupRecycler();

    }

    // verifica se tem usuário logado
    private void verificaLogado() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String telefoneLogado = preferences.getString(getString(R.string.chaveUsuarioLogado), "");

        CuidadorRepository dao = new CuidadorRepository(this);
        Usuario usuario = dao.buscaUsuarioPeloTelefone(telefoneLogado);

        if(usuario == null){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main ,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String msg = "";

        switch (item.getItemId()) {

            case R.id.settings:
                msg = getString(R.string.configuracoes);
                break;

            case R.id.web_search:
                msg = getString(R.string.busca);
                break;

            case R.id.help:
                msg = getString(R.string.ajuda);
                break;
        }

        Toast.makeText(this, msg + " clicked !", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        mAdapter = new MenuItemAdapter(this, MenuItemLista.getData(), listener);
        menuRecyclerView.setAdapter(mAdapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        menuRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = menuRecyclerView.getChildLayoutPosition(view);
            switch (MenuItemLista.getData().get(position).getTitulo()){
                case R.string.agendar_medicacao:
                    Intent agendaMedicacao = new Intent(getApplicationContext(), AgendaMedicacaoActivity.class);
                    startActivity(agendaMedicacao);
                    break;
                case R.string.gerenciar_contatos:
                    Intent contatos = new Intent(getApplicationContext(), ContatosActivity.class);
                    startActivity(contatos);
                    break;
                case R.string.gerenciar_programas_favoritos:
                    Intent programas = new Intent(getApplicationContext(), ProgramasActivity.class);
                    startActivity(programas);
                    break;
            }
        }
    };
}
