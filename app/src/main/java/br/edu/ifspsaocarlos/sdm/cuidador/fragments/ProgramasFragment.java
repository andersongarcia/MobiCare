package br.edu.ifspsaocarlos.sdm.cuidador.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.ProgramaListAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment da lista de programas favoritos.
 *
 * @author Anderson Canale Garcia
 */
public class ProgramasFragment extends Fragment implements RecyclerViewOnItemSelecionado {
    private RecyclerView mRecyclerView;
    MainActivity activity;
    private ProgramaListAdapter adapter;

    @BindView(R.id.ll_empty_view)
    View emptyView;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.tv_empty_view_help)
    TextView tvEmptyViewHelp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_programas, container, false);
        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.menu_programas_favoritos));

        tvEmptyView.setText(R.string.nenhum_programa);
        tvEmptyViewHelp.setText(R.string.programas_empty);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_programas);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        adapter = new ProgramaListAdapter(getActivity(), activity.getPreferencias().getIdosoSelecionadoId());
        adapter.setRecyclerViewOnItemSelecionado(this);
        adapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(adapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        view.findViewById(R.id.btn_cadastrar_programa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastroProgramaFragment fragment = CadastroProgramaFragment.newInstance(new Programa());
                activity.openFragment(fragment);
            }

        });

        return view;
    }

    @Override
    public void onItemSelecionado(View view, int posicao) {

        Programa programa = adapter.getItem(posicao);

        CadastroProgramaFragment fragment = CadastroProgramaFragment.newInstance(programa);
        activity.openFragment(fragment);
    }

    public static ProgramasFragment newInstance() {
        return new ProgramasFragment();
    }
}
