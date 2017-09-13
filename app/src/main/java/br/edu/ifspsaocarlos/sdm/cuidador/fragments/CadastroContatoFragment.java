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
import br.edu.ifspsaocarlos.sdm.cuidador.activities.ContatosActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de contato.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroContatoFragment extends Fragment {
    private static final String NOME = "NOME";
    private static final String TELEFONE = "TELEFONE";

    private String nome;
    private String telefone;

    private OnFragmentInteractionListener mListener;

    public CadastroContatoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nome Nome do contato
     * @param telefone Telefone do contato
     * @return Uma nova instância do fragment CadastroContatoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CadastroContatoFragment newInstance(String nome, String telefone) {
        CadastroContatoFragment fragment = new CadastroContatoFragment();
        Bundle args = new Bundle();
        args.putString(NOME, nome);
        args.putString(TELEFONE, telefone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nome = getArguments().getString(NOME);
            telefone = getArguments().getString(TELEFONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_contato, container, false);
        setHasOptionsMenu(true);

        final ContatosActivity contatosActivity = (ContatosActivity) getActivity();
        contatosActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        contatosActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((EditText)view.findViewById(R.id.contato_nome)).setText(nome);
        ((EditText)view.findViewById(R.id.contato_telefone)).setText(telefone);

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
                String nome = ((TextView)getView().findViewById(R.id.contato_nome)).getText().toString();
                String telefone = ((TextView)getView().findViewById(R.id.contato_telefone)).getText().toString();
                adicionarContato(nome, telefone);
                redirecionaParaLista();
                break;
            case R.id.excluir:
                String tel = ((TextView)getView().findViewById(R.id.contato_telefone)).getText().toString();
                new CuidadorService(getActivity()).removerContato(tel);
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
        Intent loginIntent = new Intent(getActivity(), ContatosActivity.class);
        startActivity(loginIntent);
    }

    private void adicionarContato(String nome, String telefone) {
        Contato contato = new Contato();
        contato.setNome(nome);
        contato.setTelefone(telefone);

        new CuidadorService(getActivity()).adicionarContato(contato);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
