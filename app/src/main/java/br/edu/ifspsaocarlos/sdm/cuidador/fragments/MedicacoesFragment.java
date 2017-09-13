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
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.AgendaMedicacaoActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.MedicacaoAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;

/**
 * Created by ander on 11/09/2017.
 */

public class MedicacoesFragment extends Fragment implements RecyclerViewOnItemSelecionado {
    private RecyclerView mRecyclerView;
    private List<Medicacao> mList;
    AgendaMedicacaoActivity agendaMedicacaoActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicacoes, container, false);

        agendaMedicacaoActivity = (AgendaMedicacaoActivity) getActivity();
        agendaMedicacaoActivity.getSupportActionBar().setTitle(getString(R.string.agendar_medicacao));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_medicacoes);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mList = AgendaMedicacaoActivity.medicacoes;
        MedicacaoAdapter adapter = new MedicacaoAdapter(getActivity(), mList);
        adapter.setRecyclerViewOnItemSelecionado(this);
        mRecyclerView.setAdapter(adapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        view.findViewById(R.id.btn_cadastrar_medicacao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CadastroMedicacaoFragment fragment = CadastroMedicacaoFragment.newInstance("");
                agendaMedicacaoActivity.openFragment(fragment);
            }

        });

        return view;
    }

    private Medicacao getMedicacao(View v) {
        Medicacao medicacao = new Medicacao();

        String nome = ((TextView)v.findViewById(R.id.lista_titulo)).getText().toString();
        String horarios = ((TextView)v.findViewById(R.id.lista_descricao)).getText().toString();

        medicacao.setNome(nome);
        medicacao.setHorarios(horarios);

        return medicacao;
    }

    @Override
    public void onItemSelecionado(View view, int posicao) {

        Medicacao medicacao = getMedicacao(view);

        CadastroMedicacaoFragment fragment = CadastroMedicacaoFragment.newInstance(medicacao.getNome());
        agendaMedicacaoActivity.openFragment(fragment);
    }

    public static MedicacoesFragment newInstance(Context context) {
        MedicacoesFragment fragment = new MedicacoesFragment();
        return fragment;
    }
}
