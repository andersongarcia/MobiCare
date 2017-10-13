package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.AgendaMedicacaoActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.TimePickedListener;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment de cadastro de medicação.
 *
 * @author Anderson Canale Garcia
 */
public class CadastroMedicacaoFragment extends Fragment implements TimePickedListener {
    private static final String MEDICADAO = "MEDICACAO";

    // status para gravação de instrução
    private static final int AGUARDANDO_GRAVACAO = 0;
    private static final int GRAVANDO = 1;
    private static final int GRAVACAO_CONCLUIDA = 2;
    private static final int REPRODUZINDO = 3;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private OnFragmentInteractionListener mListener;

    private Medicacao medicacao;
    private CuidadorService cuidadorService;
    private EditText etNome;
    private EditText etHorarios;
    private EditText etDose;
    private Button btnOuvirInstrucao;
    private Button btnGravarInstrucao;
    private AlertDialog dlgOuvirInstrucao;
    private AlertDialog dlgGravarInstrucao;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private static String mFileName = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {android.Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) getActivity().finish();
    }

    public CadastroMedicacaoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param medicacao Instância da medicação
     * @return Uma nova instância do fragment CadastroMedicacaoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CadastroMedicacaoFragment newInstance(Medicacao medicacao) {
        CadastroMedicacaoFragment fragment = new CadastroMedicacaoFragment();
        Bundle args = new Bundle();
        args.putSerializable(MEDICADAO, medicacao);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cuidadorService = new CuidadorService(getActivity());

        if (getArguments() != null) {
            this.medicacao = (Medicacao) getArguments().getSerializable(MEDICADAO);
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

        etNome = (EditText)view.findViewById(R.id.medicacao_nome);
        etHorarios = (EditText) view.findViewById(R.id.medicacao_horarios);
        etDose = (EditText)view.findViewById(R.id.medicacao_dose);
        btnOuvirInstrucao = (Button)view.findViewById(R.id.btn_ouvir_instrucao);
        btnGravarInstrucao = (Button)view.findViewById(R.id.btn_gravar_instrucao);

        etNome.setText(medicacao.getNome());
        etHorarios.setText(medicacao.getHorarios());
        etDose.setText(medicacao.getDose());

        setLinkParaInstrucao();

        etHorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
            }
        });

        // Record to the external cache directory for visibility
        mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        mFileName += "/medicacao_" + medicacao.getId() + ".3gp";

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        // Ouvir gravação salva
        btnOuvirInstrucao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Define o titulo
                builder.setTitle(R.string.ouvir_instrucao);
                // Define o conteúdo
                builder.setView(R.layout.dialog_ouvir_instrucao);

                // Define um botão como negativo.
                builder.setNegativeButton(R.string.btn_fechar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // não faz nada
                    }
                });

                // Define um botão como meutro.
                builder.setNeutralButton(R.string.btn_regravar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        btnGravarInstrucao.callOnClick();
                    }
                });

                // Cria o AlertDialog
                dlgOuvirInstrucao = builder.create();
                // Exibe
                dlgOuvirInstrucao.show();

                // Define ações do controle de gravação
                final ImageButton btnIniciarReproducao = (ImageButton) dlgOuvirInstrucao.findViewById(R.id.btn_iniciar_reproducao);
                btnIniciarReproducao.setTag(GRAVACAO_CONCLUIDA);
                btnIniciarReproducao.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Integer status = (Integer) btnIniciarReproducao.getTag();
                        switch (status){
                            case GRAVACAO_CONCLUIDA:
                                // inicia reproducao
                                btnIniciarReproducao.setImageResource(R.drawable.ic_pause_black_48dp);
                                btnIniciarReproducao.setTag(REPRODUZINDO);

                                MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        btnIniciarReproducao.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                        btnIniciarReproducao.setTag(GRAVACAO_CONCLUIDA);
                                    }

                                };


                                iniciarReproducao(completionListener);
                                break;
                            case REPRODUZINDO:
                                // termina reproducao
                                btnIniciarReproducao.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                btnIniciarReproducao.setTag(GRAVACAO_CONCLUIDA);

                                pararReproducao();
                                break;
                        }
                    }
                });

            }
        });

        btnGravarInstrucao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Define o titulo
                builder.setTitle(R.string.gravar_instrucao);
                // Define o conteúdo
                builder.setView(R.layout.dialog_gravar_instrucao);

                // Define um botão como positivo
                builder.setPositiveButton(R.string.btn_usar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        salvarAudio();
                    }
                });

                // Define um botão como negativo.
                builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // não faz nada
                    }
                });

                // Define um botão como meutro.
                builder.setNeutralButton(R.string.btn_regravar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        btnGravarInstrucao.callOnClick();
                    }
                });


                // Cria o AlertDialog
                dlgGravarInstrucao = builder.create();
                // Exibe
                dlgGravarInstrucao.show();

                // Inicialmente, botôes positivo e neutro permanecem desabilitados
                dlgGravarInstrucao.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                dlgGravarInstrucao.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);

                // Define ações do controle de gravação
                final ImageButton btnIniciarGravacao = (ImageButton) dlgGravarInstrucao.findViewById(R.id.btn_iniciar_gravacao);
                btnIniciarGravacao.setTag(AGUARDANDO_GRAVACAO);
                btnIniciarGravacao.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Integer status = (Integer) btnIniciarGravacao.getTag();
                        switch (status){
                            case AGUARDANDO_GRAVACAO:
                                // inicia gravação
                                btnIniciarGravacao.setImageResource(R.drawable.ic_mic_black_48dp);
                                btnIniciarGravacao.setTag(GRAVANDO);

                                iniciarGravacao();
                                break;
                            case GRAVANDO:
                                // termina gravação
                                btnIniciarGravacao.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                dlgGravarInstrucao.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                dlgGravarInstrucao.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                                btnIniciarGravacao.setTag(GRAVACAO_CONCLUIDA);

                                pararGravacao();
                                break;
                            case GRAVACAO_CONCLUIDA:
                                // inicia reproducao
                                btnIniciarGravacao.setImageResource(R.drawable.ic_pause_black_48dp);
                                btnIniciarGravacao.setTag(REPRODUZINDO);

                                MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        // termina reproducao
                                        btnIniciarGravacao.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                        btnIniciarGravacao.setTag(GRAVACAO_CONCLUIDA);
                                    }

                                };

                                iniciarReproducao(completionListener);
                                break;
                            case REPRODUZINDO:
                                // termina reproducao
                                btnIniciarGravacao.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                btnIniciarGravacao.setTag(GRAVACAO_CONCLUIDA);

                                pararReproducao();
                                break;
                        }
                    }
                });

            }
        });


        return view;
    }

    private void setLinkParaInstrucao() {
        if(medicacao.getId() != null){
            final CuidadorService service = new CuidadorService(getActivity());
            service.carregaInstrucaoURI(medicacao.getId(), new OnSuccessListener<Uri>(){
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    try {
                        final File localFile = File.createTempFile(medicacao.getId(), ".3gp");
                        service.carregarArquivo(uri, localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mFileName = localFile.getAbsolutePath();
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
                    btnOuvirInstrucao.setVisibility(View.VISIBLE);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    btnOuvirInstrucao.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void iniciarReproducao(MediaPlayer.OnCompletionListener completionListener) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(mFileName);
            mPlayer.setOnCompletionListener(completionListener);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.msg_erro_reproducao + ": " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void pararReproducao() {
        mPlayer.release();
        mPlayer = null;
    }

    private void iniciarGravacao() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.msg_erro_gravacao + ": " + e.getMessage(), Toast.LENGTH_LONG);
        }

        mRecorder.start();
    }

    private void pararGravacao() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void salvarAudio(){
        new CuidadorService(getActivity()).salvarAudioInstrucao(mFileName, medicacao.getId());
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

                Medicacao medicacao = new Medicacao();
                medicacao.setId(this.medicacao.getId());
                medicacao.setNome(nome);
                medicacao.setHorarios(horarios);
                medicacao.setDose(dose);

                cuidadorService.salvarMedicacao(medicacao);
                redirecionaParaLista();
                break;
            case R.id.excluir:
                cuidadorService.removerMedicacao(this.medicacao.getId());
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

    @Override
    public void onTimePicked(Calendar time) {
        etHorarios.setText(DateFormat.format("h:mm a", time));
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
