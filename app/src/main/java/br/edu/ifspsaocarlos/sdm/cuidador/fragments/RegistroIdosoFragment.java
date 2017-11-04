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
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

/**
 * Fragment de para registro do idoso
 *
 * @author Anderson Canale Garcia
 */
public class RegistroIdosoFragment extends Fragment {
    private RegistroActivity activity;

    EditText tvTelefone;

    public static Fragment newInstance() {
        RegistroIdosoFragment fragment = new RegistroIdosoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_idoso, container, false);
        setHasOptionsMenu(true);

        activity = (RegistroActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.registro_idoso);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTelefone = (EditText) view.findViewById(R.id.registro_idoso_telefone);

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
                String telefone = tvTelefone.getText().toString().trim();
                activity.getCuidadorService().buscaContato(telefone, new CallbackGenerico<Contato>() {
                    @Override
                    public void OnComplete(Contato contatoIdoso) {
                        if(contatoIdoso != null){
                            activity.getCuidadorService().registraUsuarioIdoso(contatoIdoso.getId());
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