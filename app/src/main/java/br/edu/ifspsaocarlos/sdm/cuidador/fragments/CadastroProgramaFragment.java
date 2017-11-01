package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.TimePickedListener;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de programa.
 *
 * @author Anderson Canale Garcia
 */

public class CadastroProgramaFragment  extends CadastroBaseFragment implements TimePickedListener {
    private static final String PROGRAMA = "PROGRAMA";

    private Programa programa;

    private CadastroProgramaFragment.OnFragmentInteractionListener mListener;

    private EditText etNome;
    private EditText etHorarios;
    private EditText etLink;

    public CadastroProgramaFragment() {
        // Required empty public constructor
        super(ProgramasFragment.newInstance(), CuidadorService.NO.PROGRAMAS);
    }

    /**
     * Factory Method
     *
     * @param programa Instância do programa
     * @return Uma nova instância do fragment CadastroProgramaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CadastroProgramaFragment newInstance(Programa programa) {
        CadastroProgramaFragment fragment = new CadastroProgramaFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROGRAMA, programa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new CuidadorService(getActivity());
        if (getArguments() != null) {
            programa = (Programa) getArguments().getSerializable(PROGRAMA);
        }
    }

    @Override
    protected void salvar() {
        String nome = ((TextView)getView().findViewById(R.id.programa_nome)).getText().toString();
        String horarios = ((TextView)getView().findViewById(R.id.programa_horarios)).getText().toString();
        String link = ((TextView)getView().findViewById(R.id.programa_link)).getText().toString();

        Programa programa = new Programa();
        programa.setId(this.programa.getId());
        programa.setNome(nome);
        programa.setHorarios(horarios);
        programa.setLink(link);

        service.salvarPrograma(programa);
    }

    @Override
    protected void excluir() {
        service.removerPrograma(this.programa.getId());
    }

    @Override
    protected int getLayoutCadastro() {
        return R.layout.fragment_cadastro_programa;
    }

    @Override
    protected String getIdCadastro() {
        return programa.getId();
    }

    @Override
    protected void criarReferenciasLayout() {
        etNome = (EditText)view.findViewById(R.id.programa_nome);
        etHorarios = (EditText) view.findViewById(R.id.programa_horarios);
        etLink = (EditText)view.findViewById(R.id.programa_link);
    }

    @Override
    protected void carregarInformacoesCadastradas() {
        etNome.setText(programa.getNome());
        etHorarios.setText(programa.getHorarios());
        etLink.setText(programa.getLink());
    }

    @Override
    protected void carregarOutrasReferencias() {
        etHorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.programa_horarios);
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    public void onTimePicked(Calendar time) {
        etHorarios.setText(DateFormat.format("h:mm a", time));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CadastroProgramaFragment.OnFragmentInteractionListener) {
            mListener = (CadastroProgramaFragment.OnFragmentInteractionListener) context;
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
