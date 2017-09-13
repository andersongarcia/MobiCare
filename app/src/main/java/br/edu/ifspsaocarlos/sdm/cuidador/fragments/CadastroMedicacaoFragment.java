package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.AgendaMedicacaoActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de medicação.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroMedicacaoFragment extends Fragment {
    private static final String NOME = "NOME";

    private String nome;

    private CadastroMedicacaoFragment.OnFragmentInteractionListener mListener;

    public CadastroMedicacaoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nome Nome da medicação
     * @return Uma nova instância do fragment CadastroMedicacaoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CadastroMedicacaoFragment newInstance(String nome) {
        CadastroMedicacaoFragment fragment = new CadastroMedicacaoFragment();
        Bundle args = new Bundle();
        args.putString(NOME, nome);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nome = getArguments().getString(NOME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_medicacao, container, false);
        setHasOptionsMenu(true);

        final AgendaMedicacaoActivity activity = (AgendaMedicacaoActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((EditText)view.findViewById(R.id.medicacao_nome)).setText(nome);
        //((EditText)view.findViewById(R.id.medicacao_horarios)).setText(telefone);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cadastro ,menu);
        menu.findItem(R.id.excluir).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.salvar:
                String nome = ((TextView)getView().findViewById(R.id.medicacao_nome)).getText().toString();
                String horarios = ((TextView)getView().findViewById(R.id.medicacao_horarios)).getText().toString();
                String dose = ((TextView)getView().findViewById(R.id.medicacao_dose)).getText().toString();
                adicionarMedicacao(nome, horarios, dose);
                redirecionaParaLista();
                break;
            case R.id.excluir:
                String nomeRemove = ((TextView)getView().findViewById(R.id.medicacao_nome)).getText().toString();
                new CuidadorService(getActivity()).removerMedicacao(nomeRemove);
                redirecionaParaLista();
                break;
            case android.R.id.home:
                redirecionaParaLista();
                break;
        }

        //Toast.makeText(this, msg + " clicked !", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    private void redirecionaParaLista() {
        Intent loginIntent = new Intent(getActivity(), AgendaMedicacaoActivity.class);
        startActivity(loginIntent);
    }

    private void adicionarMedicacao(String nome, String horarios, String dose) {
        Medicacao medicacao = new Medicacao();
        medicacao.setNome(nome);
        medicacao.setHorarios(horarios);
        medicacao.setDose(dose);

        new CuidadorService(getActivity()).adicionarMedicacao(medicacao);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CadastroMedicacaoFragment.OnFragmentInteractionListener) {
            mListener = (CadastroMedicacaoFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
