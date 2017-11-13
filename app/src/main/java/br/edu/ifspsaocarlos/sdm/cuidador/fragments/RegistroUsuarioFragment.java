package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.RegistroActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;

/**
 * Fragment de para registro do usuário através do Firebase Authentications
 *
 * @author Anderson Canale Garcia
 */
public class RegistroUsuarioFragment extends Fragment {
    private RegistroActivity activity;

    EditText tvTelefone;

    public static Fragment newInstance() {
        RegistroUsuarioFragment fragment = new RegistroUsuarioFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_usuario, container, false);
        setHasOptionsMenu(true);

        activity = (RegistroActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.registro_usuario);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //etTelefone = (EditText) view.findViewById(R.id.registro_usuario);

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
                final String telefone = tvTelefone.getText().toString().trim();
                ContatosRepository.getInstance().buscaIdoso(telefone, new CallbackGenerico<Boolean>() {
                    @Override
                    public void OnComplete(Boolean existe) {
                        if(existe){
                            activity.getCuidadorService().registraUsuarioIdoso(telefone);
                            activity.abrirFragment(RegistroFotoFragment.newInstance(Usuario.IDOSO));
                        }else {
                            tvTelefone.setError(getResources().getString(R.string.msg_erro_validacao_idoso));
                        }
                    }
                });


                break;
            case android.R.id.home:
                activity.abrirFragment(RegistroPerfilFragment.newInstance());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
