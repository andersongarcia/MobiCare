package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.RegistroActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

/**
 * Fragment de para registro do usuário cuidador
 *
 * @author Anderson Canale Garcia
 */
public class RegistroCuidadorFragment extends Fragment {
    RegistroActivity activity;

    public static RegistroCuidadorFragment newInstance() {
        RegistroCuidadorFragment fragment = new RegistroCuidadorFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_cuidador, container, false);
        setHasOptionsMenu(true);

        activity = (RegistroActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.registro_cuidador);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_cadastro ,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.salvar:
                String nome = ((TextView)getView().findViewById(R.id.registro_cuidador_nome)).getText().toString().trim();
                String telefone = ((TextView)getView().findViewById(R.id.registro_cuidador_telefone)).getText().toString().trim();

                activity.getCuidadorService().registrarUsuario(nome, telefone, Usuario.CUIDADOR);
                activity.abrirFragment(RegistroIdosoFragment.newInstance(Usuario.CUIDADOR));
                break;
            case android.R.id.home:
                activity.abrirFragment(RegistroPerfilFragment.newInstance());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
