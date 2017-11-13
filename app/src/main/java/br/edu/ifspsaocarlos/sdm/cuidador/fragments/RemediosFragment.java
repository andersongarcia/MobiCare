package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.RemedioListAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.RemediosRepository;

/**
 * Created by ander on 11/09/2017.
 */

public class RemediosFragment extends Fragment implements RecyclerViewOnItemSelecionado {
    private RecyclerView mRecyclerView;
    private List<Remedio> listaRemedios;
    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remedios, container, false);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.menu_remedios));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_remedios);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        listaRemedios = RemediosRepository.getInstance().getRemedios();
        RemedioListAdapter adapter = new RemedioListAdapter(getActivity(), listaRemedios);
        adapter.setRecyclerViewOnItemSelecionado(this);
        mRecyclerView.setAdapter(adapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        view.findViewById(R.id.btn_cadastrar_remedio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastroRemedioFragment fragment = CadastroRemedioFragment.newInstance(new Remedio());
                activity.openFragment(fragment);
            }

        });

        return view;
    }

    @Override
    public void onItemSelecionado(View view, int posicao) {

        Remedio remedio = listaRemedios.get(posicao);

        CadastroRemedioFragment fragment = CadastroRemedioFragment.newInstance(remedio);
        activity.openFragment(fragment);
    }

    public static RemediosFragment newInstance() {
        RemediosFragment fragment = new RemediosFragment();
        return fragment;
    }
}
