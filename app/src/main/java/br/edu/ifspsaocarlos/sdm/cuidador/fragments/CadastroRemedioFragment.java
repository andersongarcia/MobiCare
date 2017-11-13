package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.TimePickedListener;
import br.edu.ifspsaocarlos.sdm.cuidador.listeners.DialogAudioListener;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.FirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.RemediosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de remédio.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroRemedioFragment extends CadastroBaseFragment implements TimePickedListener {
    private static final String REMEDIO = "REMEDIO";

    private Remedio remedio;
    private EditText etNome;
    private EditText etDose;
    private EditText etHorario;
    private Switch switchAjustavel;
    private RadioGroup rgRepete;
    private TextView tvRepeticaoHoras;
    private DiscreteSeekBar dsbRepeticaoHoras;
    private Button btnOuvirInstrucao;
    private Button btnGravarInstrucao;

    private DialogAudioListener dlgGravarInstrucaoListener;
    private DialogAudioListener dlgOuvirInstrucaoListener;
    private boolean instrucaoAlterada = false;

    public CadastroRemedioFragment() {
        // Required empty public constructor
        super(RemediosFragment.newInstance(), NO.REMEDIOS);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param remedio Instância da remédio
     * @return Uma nova instância do fragment CadastroRemedioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CadastroRemedioFragment newInstance(Remedio remedio) {
        CadastroRemedioFragment fragment = new CadastroRemedioFragment();
        Bundle args = new Bundle();
        args.putSerializable(REMEDIO, remedio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new CuidadorService(getActivity());

        if (getArguments() != null) {
            this.remedio = (Remedio) getArguments().getSerializable(REMEDIO);
        }
    }

    @Override
    public int getLayoutCadastro() {
        return R.layout.fragment_cadastro_remedio;
    }

    @Override
    protected String getIdCadastro() {
        return remedio.getId();
    }

    @Override
    protected String getUriAvatar() { return remedio.getFotoUri(); }

    @Override
    protected void criaReferenciasLayout() {
        // referencia componentes do layout
        etNome = (EditText)view.findViewById(R.id.remedio_nome);
        etDose = (EditText)view.findViewById(R.id.remedio_dose);
        etHorario = (EditText) view.findViewById(R.id.remedio_horario);
        switchAjustavel = (Switch) view.findViewById(R.id.switchAjustavel);
        rgRepete = (RadioGroup) view.findViewById(R.id.rg_repete);
        tvRepeticaoHoras = (TextView) view.findViewById(R.id.tv_repeticao_horas);
        dsbRepeticaoHoras = (DiscreteSeekBar) view.findViewById(R.id.dsb_repeticao_horas);
        btnOuvirInstrucao = (Button)view.findViewById(R.id.btn_ouvir_instrucao);
        btnGravarInstrucao = (Button)view.findViewById(R.id.btn_gravar_instrucao);
    }

    @Override
    protected void carregaInformacoesCadastradas() {
        // carrega informações cadastradas
        etNome.setText(remedio.getNome());
        etHorario.setText(remedio.getHorario());
        etDose.setText(remedio.getDose());
        switchAjustavel.setChecked(remedio.isAjustavel());

        dsbRepeticaoHoras.setProgress(remedio.getRepeticao());
        switch (remedio.getRepeticao()){
            case 0:
                ((RadioButton)view.findViewById(R.id.rb_repete_nao)).setChecked(true);
                exibeSelecaoHoras(false);
                break;
            case 24:
                ((RadioButton)view.findViewById(R.id.rb_repete_diariamente)).setChecked(true);
                exibeSelecaoHoras(false);
                break;
            default:
                ((RadioButton)view.findViewById(R.id.rb_repete_em_horas)).setChecked(true);
                exibeSelecaoHoras(true);
        }
    }

    @Override
    protected void carregarOutrasReferencias() {
        btnOuvirInstrucao.setEnabled(false);

        // Cria timepicker para campo de horário
        etHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.remedio_horario);
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
            }
        });

        rgRepete.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbSelecionado = (RadioButton)rgRepete.findViewById(rgRepete.getCheckedRadioButtonId());
                boolean mostraHoras = rbSelecionado.getId() == R.id.rb_repete_em_horas;
                exibeSelecaoHoras(mostraHoras);
            }
        });

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        final String path = activity.getExternalCacheDir().getAbsolutePath();
        final String filename = String.valueOf(System.currentTimeMillis());

        // Tenta carregar instrução já gravada
        if(remedio.getInstrucaoUri() != null && !remedio.getInstrucaoUri().isEmpty()){
            try {
                final File localFile = File.createTempFile(filename, ".3gp");

                service.carregaArquivo(Uri.parse(remedio.getInstrucaoUri()), localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        criaDialogs(localFile.getAbsolutePath());
                        dlgOuvirInstrucaoListener.setRecord(true);
                        btnOuvirInstrucao.setEnabled(true);
                        carregaAvatar();
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        criaDialogs(path + "/" + filename);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            criaDialogs(path + "/" + filename);
        }


    }

    private void criaDialogs(String fullPathFilename) {
        //region Dialog para gravação da instrução
        dlgGravarInstrucaoListener = new DialogAudioListener(activity, fullPathFilename);
        dlgGravarInstrucaoListener.setTitle(R.string.gravar_instrucao);
        btnGravarInstrucao.setOnClickListener(dlgGravarInstrucaoListener);

        dlgGravarInstrucaoListener.setButton(DialogAudioListener.TipoBotao.POSITIVO, R.string.btn_usar, new Runnable() {
            @Override
            public void run() {
                dlgGravarInstrucaoListener.setStatus(DialogAudioListener.Status.AGUARDANDO_GRAVACAO);
                dlgOuvirInstrucaoListener.setRecord(true);
                btnOuvirInstrucao.setEnabled(true);
                instrucaoAlterada = true;
            }
        });

        dlgGravarInstrucaoListener.setButton(DialogAudioListener.TipoBotao.NEGATIVO, R.string.btn_cancelar, new Runnable() {
            @Override
            public void run() {
                dlgGravarInstrucaoListener.setRecord(true);
            }
        });

        dlgGravarInstrucaoListener.setButton(DialogAudioListener.TipoBotao.NEUTRO, R.string.btn_regravar, new Runnable() {
            @Override
            public void run() {
                btnGravarInstrucao.callOnClick();
            }
        });
        //endregion

        //region Dialog para reprodução da instrução gravada
        dlgOuvirInstrucaoListener = new DialogAudioListener(activity, fullPathFilename);
        dlgOuvirInstrucaoListener.setTitle(R.string.ouvir_instrucao);
        btnOuvirInstrucao.setOnClickListener(dlgOuvirInstrucaoListener);
        dlgOuvirInstrucaoListener.setStatus(DialogAudioListener.Status.GRAVACAO_CONCLUIDA);

        dlgOuvirInstrucaoListener.setButton(DialogAudioListener.TipoBotao.NEGATIVO, R.string.btn_fechar, new Runnable() {
            @Override
            public void run() {
                // não faz nada
            }
        });

        dlgOuvirInstrucaoListener.setButton(DialogAudioListener.TipoBotao.NEUTRO, R.string.btn_regravar, new Runnable() {
            @Override
            public void run() {
                btnGravarInstrucao.callOnClick();
            }
        });
        //endregion
    }

    private void exibeSelecaoHoras(boolean mostraHoras) {
        if(mostraHoras){
            tvRepeticaoHoras.setVisibility(View.VISIBLE);
            dsbRepeticaoHoras.setVisibility(View.VISIBLE);
        }else {
            tvRepeticaoHoras.setVisibility(View.INVISIBLE);
            dsbRepeticaoHoras.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void salva() {
        String nome = etNome.getText().toString().trim();
        String dose = etDose.getText().toString().trim();
        String horario = etHorario.getText().toString().trim();
        boolean isAjustavel = switchAjustavel.isChecked();

        int horasRepeticao = 0;
        RadioButton rbSelecionado = (RadioButton)rgRepete.findViewById(rgRepete.getCheckedRadioButtonId());
        switch (rbSelecionado.getId()){
            case R.id.rb_repete_em_horas:
                horasRepeticao = dsbRepeticaoHoras.getProgress();
                break;
            case R.id.rb_repete_diariamente:
                horasRepeticao = 24;
                break;
            default:
                horasRepeticao = 0;
        }

        remedio.setNome(nome);
        remedio.setDose(dose);
        remedio.setHorario(horario);
        remedio.setAjustavel(isAjustavel);
        remedio.setRepeticao(horasRepeticao);

        // salva remédio
        // retorna id, já que pode ser novo
        final String id = RemediosRepository.getInstance().salvaRemedio(activity.getPreferencias().getIdosoSelecionadoId(), remedio);

        // verifica se foto foi alterada
        if(fotoAlterada){
            // se foi, salva nova foto
            if(localFile != null && localFile.exists()){
                service.salvaFoto(NO.REMEDIOS, id, localFile)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri uri = taskSnapshot.getDownloadUrl();
                                FirebaseRepository.getInstance().salvaUri(NO.REMEDIOS, activity.getPreferencias().getIdosoSelecionadoId(), id, uri.toString());
                                redirecionaParaLista();
                            }
                        });
            }
        }

        // verifica se instrução foi alterada
        if(instrucaoAlterada){
            // se foi, salva nova instrução
            if(dlgGravarInstrucaoListener.getFileName() != null && !dlgGravarInstrucaoListener.getFileName().isEmpty()) {
                service.salvaAudioInstrucao(dlgGravarInstrucaoListener.getFileName(), remedio.getId(),
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String uri = taskSnapshot.getDownloadUrl().toString();
                                RemediosRepository.getInstance().salvaUriInstrucao(activity.getPreferencias().getIdosoSelecionadoId(), remedio.getId(), uri);
                            }
                        });
            }
        }
    }

    @Override
    protected void exclui() {
        RemediosRepository.getInstance().removeRemedio(activity.getPreferencias().getIdosoSelecionadoId(), this.remedio.getId());
    }

    @Override
    public void onTimePicked(Calendar time) {
        etHorario.setText(DateFormat.format("h:mm a", time));
    }
}