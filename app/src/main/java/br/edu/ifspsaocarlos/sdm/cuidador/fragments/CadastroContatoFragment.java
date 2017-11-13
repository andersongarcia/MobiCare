package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de contato.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroContatoFragment extends CadastroBaseFragment {
    private static final String CONTATO = "CONTATO";

    private OnFragmentInteractionListener mListener;
    private EditText etNome;
    private EditText etTelefone;
    private Contato contato;

    public CadastroContatoFragment() {
        // Required empty public constructor
        super(ContatosFragment.newInstance(), NO.CONTATOS);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param contato Dados do contato
     * @return Uma nova instância do fragment CadastroContatoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CadastroContatoFragment newInstance(Contato contato) {
        CadastroContatoFragment fragment = new CadastroContatoFragment();
        Bundle args = new Bundle();
        args.putSerializable(CONTATO, contato);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new CuidadorService(getActivity());
        if (getArguments() != null) {
            this.contato = (Contato) getArguments().getSerializable(CONTATO);
        }
    }

    @Override
    protected void salva() {
        String nome = etNome.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();

        final Contato contato = new Contato(nome, telefone);
        ContatosRepository.getInstance().salvaContato(activity.getPreferencias().getIdosoSelecionadoId(), contato).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(localFile != null && localFile.exists()){
                    service.salvaFoto(NO.CONTATOS, contato.getId(), localFile)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri uri = taskSnapshot.getDownloadUrl();
                                    ContatosRepository.getInstance().salvaUriContato(contato.getId(), uri.toString());
                                }
                            });
                }
            }
        });
        redirecionaParaLista();
    }

    @Override
    protected void exclui() {
        ContatosRepository.getInstance().removeContato(contato.getId(), activity.getPreferencias().getIdosoSelecionadoId(), new CallbackSimples() {
            @Override
            public void OnComplete() {
                redirecionaParaLista();
            }
        });
    }

    @Override
    protected int getLayoutCadastro() {
        return R.layout.fragment_cadastro_contato;
    }

    @Override
    protected String getIdCadastro() {
        return contato.getId();
    }

    @Override
    protected String getUriAvatar() { return contato.getFotoUri(); }

    @Override
    protected void criaReferenciasLayout() {
        etNome = (EditText)view.findViewById(R.id.contato_nome);
        etTelefone = (EditText) view.findViewById(R.id.contato_telefone);
    }

    @Override
    protected void carregaInformacoesCadastradas() {
        etNome.setText(contato.getNome());
        etTelefone.setText(contato.getTelefone());

        carregaAvatar();
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
