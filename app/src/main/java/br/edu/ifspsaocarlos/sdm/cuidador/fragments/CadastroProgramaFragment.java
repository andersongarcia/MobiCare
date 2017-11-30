package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Semana;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.TimePickedListener;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.FirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ProgramasRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;
import br.edu.ifspsaocarlos.sdm.cuidador.util.CheckedTextViewHelper;

/**
 * Fragment de cadastro de programa.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroProgramaFragment  extends CadastroBaseFragment implements TimePickedListener {
    private static final String PROGRAMA = "PROGRAMA";

    private Programa programa;

    private EditText etNome;
    private EditText etHorarios;
    private EditText etLink;

    private CheckedTextView cbDomingo;
    private CheckedTextView cbSegunda;
    private CheckedTextView cbTerca;
    private CheckedTextView cbQuarta;
    private CheckedTextView cbQuinta;
    private CheckedTextView cbSexta;
    private CheckedTextView cbSabado;

    public CadastroProgramaFragment() {
        // Required empty public constructor
        super(ProgramasFragment.newInstance(), NO.PROGRAMAS);
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
        service = new UsuarioService(getActivity());
        if (getArguments() != null) {
            programa = (Programa) getArguments().getSerializable(PROGRAMA);
        }
    }

    @Override
    protected void salva() {
        String nome = ((TextView)getView().findViewById(R.id.programa_nome)).getText().toString().trim();
        String horarios = ((TextView)getView().findViewById(R.id.programa_horarios)).getText().toString().trim();
        String link = ((TextView)getView().findViewById(R.id.programa_link)).getText().toString().trim();

        Semana semana = new Semana();
        semana.setDomingo(cbDomingo.isChecked());
        semana.setSegunda(cbSegunda.isChecked());
        semana.setTerca(cbTerca.isChecked());
        semana.setQuarta(cbQuarta.isChecked());
        semana.setQuinta(cbQuinta.isChecked());
        semana.setSexta(cbSexta.isChecked());
        semana.setSabado(cbSabado.isChecked());

        Programa programa = new Programa();
        programa.setId(this.programa.getId());
        programa.setNome(nome);
        programa.setHorario(horarios);
        programa.setLink(link);
        programa.setSemana(semana);

        // salva programa
        // retorna id, já que pode ser novo
        final String id = ProgramasRepository.getInstance().salvaPrograma(activity.getPreferencias().getIdosoSelecionadoId(), programa);

        if(localFile != null && localFile.exists()){
            service.salvaFoto(NO.PROGRAMAS, id, localFile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri uri = taskSnapshot.getDownloadUrl();
                            FirebaseRepository.getInstance().salvaUri(NO.PROGRAMAS, activity.getPreferencias().getIdosoSelecionadoId(), id, uri.toString());
                            redirecionaParaLista();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            redirecionaParaLista();
                        }
                    });
        }else {
            redirecionaParaLista();
        }
    }

    @Override
    protected void exclui() {
        ProgramasRepository.getInstance().removePrograma(activity.getPreferencias().getIdosoSelecionadoId(), this.programa.getId());
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
    protected String getUriAvatar() { return programa.getFotoUri(); }

    @Override
    protected void criaReferenciasLayout() {
        etNome = (EditText)view.findViewById(R.id.programa_nome);
        etHorarios = (EditText) view.findViewById(R.id.programa_horarios);
        etLink = (EditText)view.findViewById(R.id.programa_link);

        cbDomingo = (CheckedTextView)view.findViewById(R.id.cb_domingo);
        cbSegunda = (CheckedTextView)view.findViewById(R.id.cb_segunda);
        cbTerca = (CheckedTextView)view.findViewById(R.id.cb_terca);
        cbQuarta = (CheckedTextView)view.findViewById(R.id.cb_quarta);
        cbQuinta = (CheckedTextView)view.findViewById(R.id.cb_quinta);
        cbSexta = (CheckedTextView)view.findViewById(R.id.cb_sexta);
        cbSabado = (CheckedTextView)view.findViewById(R.id.cb_sabado);
    }

    @Override
    protected void carregaInformacoesCadastradas() {
        etNome.setText(programa.getNome());
        etHorarios.setText(programa.getHorario());
        etLink.setText(programa.getLink());

        CheckedTextViewHelper.setChecked(cbDomingo, programa.getSemana().isDomingo());
        CheckedTextViewHelper.setChecked(cbSegunda, programa.getSemana().isSegunda());
        CheckedTextViewHelper.setChecked(cbTerca, programa.getSemana().isTerca());
        CheckedTextViewHelper.setChecked(cbQuarta, programa.getSemana().isQuarta());
        CheckedTextViewHelper.setChecked(cbQuinta, programa.getSemana().isQuinta());
        CheckedTextViewHelper.setChecked(cbSexta, programa.getSemana().isSexta());
        CheckedTextViewHelper.setChecked(cbSabado, programa.getSemana().isSabado());
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


        cbDomingo.setOnClickListener(CheckedTextViewHelper.getToogleListener());
        cbSegunda.setOnClickListener(CheckedTextViewHelper.getToogleListener());
        cbTerca.setOnClickListener(CheckedTextViewHelper.getToogleListener());
        cbQuarta.setOnClickListener(CheckedTextViewHelper.getToogleListener());
        cbQuinta.setOnClickListener(CheckedTextViewHelper.getToogleListener());
        cbSexta.setOnClickListener(CheckedTextViewHelper.getToogleListener());
        cbSabado.setOnClickListener(CheckedTextViewHelper.getToogleListener());
    }

    @Override
    public void onTimePicked(Calendar time) {
        etHorarios.setText(DateFormat.format("h:mm a", time));
    }
}
