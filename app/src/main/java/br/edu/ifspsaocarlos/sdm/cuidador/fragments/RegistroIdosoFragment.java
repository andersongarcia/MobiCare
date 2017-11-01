package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.RegistroActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

/**
 * Fragment de para registro do idoso
 *
 * @author Anderson Canale Garcia
 */
public class RegistroIdosoFragment extends Fragment {
    private String perfilUsuario;
    private RegistroActivity activity;

    public static Fragment newInstance(String perfilUsuario) {
        RegistroIdosoFragment fragment = new RegistroIdosoFragment();
        fragment.setPerfilUsuario(perfilUsuario);
        return fragment;
    }

    public void setPerfilUsuario(String perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_idoso, container, false);
        setHasOptionsMenu(true);

        activity = (RegistroActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.registro_idoso);
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
                String nome = ((TextView)getView().findViewById(R.id.registro_idoso_nome)).getText().toString().trim();
                String telefone = ((TextView)getView().findViewById(R.id.registro_idoso_telefone)).getText().toString().trim();

                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        Intent menuIntent = new Intent(activity, MainActivity.class);
                        startActivity(menuIntent);
                    }
                };

                switch (perfilUsuario){
                    case Usuario.CUIDADOR:
                        activity.getCuidadorService().registrarIdoso(nome, telefone, runnable);
                        break;
                    case Usuario.IDOSO:
                        activity.getCuidadorService().registrarUsuario(nome, telefone, Usuario.IDOSO);
                        activity.getCuidadorService().registrarIdoso(nome, telefone, runnable);
                        break;
                    case Usuario.CONTATO:
                        break;
                }
                break;
            case android.R.id.home:
                switch (perfilUsuario){
                    case Usuario.CUIDADOR:
                        activity.abrirFragment(RegistroCuidadorFragment.newInstance());
                        break;
                    case  Usuario.IDOSO:
                        activity.abrirFragment(RegistroPerfilFragment.newInstance());
                        break;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }}
