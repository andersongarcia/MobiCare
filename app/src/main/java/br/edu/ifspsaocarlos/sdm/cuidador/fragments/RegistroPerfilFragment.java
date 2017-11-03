package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.RegistroActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;


/**
 * Fragment de seleção do perfil para registro do usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroPerfilFragment extends Fragment {

    private ListView listView;

    public static RegistroPerfilFragment newInstance() {
        RegistroPerfilFragment fragment = new RegistroPerfilFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_perfil, container, false);

        final RegistroActivity activity = (RegistroActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.app_name));

        listView = (ListView) view.findViewById(R.id.lv_perfis);

        List<String> perfis = Arrays.asList( Usuario.CUIDADOR, Usuario.IDOSO, Usuario.CONTATO);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, perfis);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                String perfil = (String)adapterView.getItemAtPosition(position);

                switch (perfil){
                    case Usuario.CUIDADOR:
                        activity.abrirFragment(RegistroCuidadorFragment.newInstance());
                        break;
                    case Usuario.IDOSO:
                        activity.abrirFragment(RegistroIdosoFragment.newInstance());
                        break;
                    case Usuario.CONTATO:
                        activity.abrirFragment(RegistroContatoFragment.newInstance());
                        break;
                }
            }
        });

        return view;
    }

}
