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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

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
    private EditText etHorarios;
    private EditText etDose;
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
        etHorarios = (EditText) view.findViewById(R.id.remedio_horarios);
        etDose = (EditText)view.findViewById(R.id.remedio_dose);
        btnOuvirInstrucao = (Button)view.findViewById(R.id.btn_ouvir_instrucao);
        btnGravarInstrucao = (Button)view.findViewById(R.id.btn_gravar_instrucao);
    }

    @Override
    protected void carregarInformacoesCadastradas() {
        // carrega informações cadastradas
        etNome.setText(remedio.getNome());
        etHorarios.setText(remedio.getHorarios());
        etDose.setText(remedio.getDose());
    }

    @Override
    protected void carregarOutrasReferencias() {
        setLinkParaInstrucao();

        // Cria timepicker para campo de horário
        etHorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(R.id.remedio_horarios);
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
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
                service.salvarAudioInstrucao(dlgGravarInstrucaoListener.getFileName(), remedio.getId(),
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

    private final void setLinkParaInstrucao() {
        if(remedio.getId() != null){
            service.carregaInstrucaoURI(remedio.getId(), new OnSuccessListener<Uri>(){
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    try {
                        final File localFile = File.createTempFile(remedio.getId(), ".3gp");
                        service.carregarArquivo(uri, localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
        String horarios = etHorarios.getText().toString().trim();
        String dose = etDose.getText().toString().trim();

        Remedio remedio = new Remedio();
        remedio.setId(this.remedio.getId());
        remedio.setNome(nome);
        remedio.setHorarios(horarios);
        remedio.setDose(dose);

        service.salvarRemedio(remedio);
    }

    @Override
    protected void excluir() {
        service.removerRemedio(this.remedio.getId());
    }

    @Override
    public void onTimePicked(Calendar time) {
        etHorarios.setText(DateFormat.format("h:mm a", time));
    }
}