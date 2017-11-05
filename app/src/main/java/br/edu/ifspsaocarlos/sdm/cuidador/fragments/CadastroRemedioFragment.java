package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
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
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.TimePickedListener;
import br.edu.ifspsaocarlos.sdm.cuidador.listeners.DialogAudioListener;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de remédio.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroRemedioFragment extends CadastroBaseFragment implements TimePickedListener {
    private static final String REMEDIO = "REMEDIO";
    private static final String FILE_PREFIX = REMEDIO.toLowerCase() + "_";

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

    private String fileName;

    public CadastroRemedioFragment() {
        // Required empty public constructor
        super(RemediosFragment.newInstance(), CuidadorService.NO.REMEDIOS);
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
    protected void criarReferenciasLayout() {
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
    protected void carregarInformacoesCadastradas() {
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
        setLinkParaInstrucao();

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

        fileName = FILE_PREFIX + remedio.getId();

        //region Dialog para gravação da instrução
        final DialogAudioListener dlgGravarInstrucaoListener = new DialogAudioListener(activity, fileName);
        dlgGravarInstrucaoListener.setTitle(R.string.gravar_instrucao);
        btnGravarInstrucao.setOnClickListener(dlgGravarInstrucaoListener);

        dlgGravarInstrucaoListener.setButton(DialogAudioListener.TipoBotao.POSITIVO, R.string.btn_usar, new Runnable() {
            @Override
            public void run() {
                service.salvaAudioInstrucao(dlgGravarInstrucaoListener.getFileName(), remedio.getId(),
                        new OnSuccessListener<UploadTask.TaskSnapshot>(){

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                setLinkParaInstrucao();
                            }
                        });
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
        final DialogAudioListener dlgOuvirInstrucaoListener = new DialogAudioListener(activity, fileName);
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

    private final void setLinkParaInstrucao() {
        if(remedio.getId() != null){
            service.carregaInstrucaoURI(remedio.getId(), new OnSuccessListener<Uri>(){
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    try {
                        final File localFile = File.createTempFile(remedio.getId(), ".3gp");
                        service.carregaArquivo(uri, localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                fileName = localFile.getAbsolutePath();
                                Log.e("firebase ",";local tem file created  created " + localFile.toString());
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("firebase ",";local tem file not created  created " +exception.toString());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnOuvirInstrucao.setEnabled(true);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    btnOuvirInstrucao.setEnabled(false);
                }
            });
        }
    }

    @Override
    protected void salvar() {
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

        Remedio remedio = new Remedio();
        remedio.setId(this.remedio.getId());
        remedio.setNome(nome);
        remedio.setDose(dose);
        remedio.setHorario(horario);
        remedio.setAjustavel(isAjustavel);
        remedio.setRepeticao(horasRepeticao);

        // salva remédio
        // retorna id, já que pode ser novo
        String id = service.salvaRemedio(remedio);

        if(localFile != null && localFile.exists()){
            service.salvaFoto(CuidadorService.NO.REMEDIOS, id, localFile);
        }
    }

    @Override
    protected void excluir() {
        service.removeRemedio(this.remedio.getId());
    }

    @Override
    public void onTimePicked(Calendar time) {
        etHorario.setText(DateFormat.format("h:mm a", time));
    }
}