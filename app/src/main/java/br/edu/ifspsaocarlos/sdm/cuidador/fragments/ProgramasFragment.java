package br.edu.ifspsaocarlos.sdm.cuidador.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.ProgramasActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.ProgramaAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramasFragment extends Fragment implements RecyclerViewOnItemSelecionado {
    private RecyclerView mRecyclerView;
    private List<Programa> listaProgramas;
    ProgramasActivity programasActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicacoes, container, false);

        programasActivity = (ProgramasActivity) getActivity();
        programasActivity.getSupportActionBar().setTitle(getString(R.string.gerenciar_programas_favoritos));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_medicacoes);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        listaProgramas = ProgramasActivity.programas;
        ProgramaAdapter adapter = new ProgramaAdapter(getActivity(), listaProgramas);
        adapter.setRecyclerViewOnItemSelecionado(this);
        mRecyclerView.setAdapter(adapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        view.findViewById(R.id.btn_cadastrar_medicacao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastroProgramaFragment fragment = CadastroProgramaFragment.newInstance(new Programa());
                programasActivity.openFragment(fragment);
            }

        });

        return view;
    }

    @Override
    public void onItemSelecionado(View view, int posicao) {

        Programa programa = listaProgramas.get(posicao);

        CadastroProgramaFragment fragment = CadastroProgramaFragment.newInstance(programa);
        programasActivity.openFragment(fragment);
    }

    public static ProgramasFragment newInstance(Context context) {
        ProgramasFragment fragment = new ProgramasFragment();
        return fragment;
    }
}
