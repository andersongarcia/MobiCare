package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de contato.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroContatoFragment extends CadastroBaseFragment {
    private static final String ID = "ID";
    private static final String NOME = "NOME";
    private static final String TELEFONE = "TELEFONE";

    private String id;
    private String nome;
    private String telefone;

    private OnFragmentInteractionListener mListener;
    private EditText etNome;
    private EditText etTelefone;

    public CadastroContatoFragment() {
        // Required empty public constructor
        super(ContatosFragment.newInstance(), CuidadorService.NO.CONTATOS);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param nome Nome do contato
     * @param telefone Telefone do contato
     * @return Uma nova instância do fragment CadastroContatoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CadastroContatoFragment newInstance(String id, String nome, String telefone) {
        CadastroContatoFragment fragment = new CadastroContatoFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(NOME, nome);
        args.putString(TELEFONE, telefone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new CuidadorService(getActivity());
        if (getArguments() != null) {
            id = getArguments().getString(ID);
            nome = getArguments().getString(NOME);
            telefone = getArguments().getString(TELEFONE);
        }
    }

    @Override
    protected void salvar() {
        String nome = etNome.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();

        Contato contato = new Contato(nome, telefone);
        contato.setId(id);
        service.salvarContato(contato, new CallbackGenerico<Contato>() {
                    @Override
                    public void OnComplete(Contato c) {
                    }
                }
        );
    }

    @Override
    protected void excluir() {
        service.removerContato(id, new Runnable() {
            @Override
            public void run() {
                //redirecionaParaLista();
            }
        });
    }

    @Override
    protected int getLayoutCadastro() {
        return R.layout.fragment_cadastro_contato;
    }

    @Override
    protected String getIdCadastro() {
        return id;
    }

    @Override
    protected void criarReferenciasLayout() {
        etNome = (EditText)view.findViewById(R.id.contato_nome);
        etTelefone = (EditText) view.findViewById(R.id.contato_telefone);
    }

    @Override
    protected void carregarInformacoesCadastradas() {
        etNome.setText(nome);
        etTelefone.setText(telefone);
    }

    @Override
    protected void carregarOutrasReferencias() {

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
