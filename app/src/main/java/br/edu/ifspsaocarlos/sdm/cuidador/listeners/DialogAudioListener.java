package br.edu.ifspsaocarlos.sdm.cuidador.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Created by ander on 19/10/2017.
 */

public class DialogAudioListener implements View.OnClickListener {

    private final Context context;
    private Status status;
    private MediaPlayer mPlayer;
    private String fileName;
    private MediaRecorder mRecorder;
    private boolean isRecord;
    private static final Map<Status, Integer> statusImage;
    static
    {
        statusImage = new HashMap<>();
        statusImage.put(Status.AGUARDANDO_GRAVACAO, R.drawable.ic_mic_none_black_48dp);
        statusImage.put(Status.GRAVANDO, R.drawable.ic_mic_black_48dp);
        statusImage.put(Status.GRAVACAO_CONCLUIDA, R.drawable.ic_play_arrow_black_48dp);
        statusImage.put(Status.REPRODUZINDO, R.drawable.ic_pause_black_48dp);
    }

    public String getFileName() {
        return fileName;
    }

    public void setStatus(Status status) { this.status = status; }

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecord(boolean record) {
        isRecord = record;
    }

    // botões do dialog
    public enum TipoBotao { POSITIVO, NEGATIVO, NEUTRO };
    // status para gravação de instrução
    public enum Status { AGUARDANDO_GRAVACAO, GRAVANDO, GRAVACAO_CONCLUIDA, REPRODUZINDO };

    private int dialog_title;
    private final int dialog_layout;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public DialogAudioListener(Context context, String fileName){
        this.context = context;
        //String path = context.getExternalCacheDir().getAbsolutePath();
        //this.fileName = path + "/" + fileName;
        this.fileName = fileName;
        this.isRecord = false;

        // Cria o gerador do AlertDialog
        builder = new AlertDialog.Builder(context);

        // configurações padrão
        this.dialog_title = R.string.gravar_audio;
        this.dialog_layout = R.layout.dialog_gravar_instrucao;
        this.status = Status.AGUARDANDO_GRAVACAO;
    }


    public void setTitle(int resTitle){
        this.dialog_title = resTitle;
    }

    public void setButton(TipoBotao tipoBotao, int resTextoBotao, final Runnable runnable){

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                runnable.run();
            }
        };

        switch (tipoBotao){
            case POSITIVO:
                builder.setPositiveButton(resTextoBotao, listener);
                break;
            case NEGATIVO:
                builder.setNegativeButton(resTextoBotao, listener);
                break;
            case NEUTRO:
                builder.setNeutralButton(resTextoBotao, listener);
                break;
        }

    }

    @Override
    public void onClick(View view) {
        // Define o titulo
        builder.setTitle(dialog_title);
        // Define o conteúdo
        builder.setView(dialog_layout);

        // Define um botão como negativo.
        builder.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // não faz nada
            }
        });

        // Cria o AlertDialog
        dialog = builder.create();
        // Exibe
        dialog.show();

        // Inicialmente, botôes positivo e neutro permanecem desabilitados
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);

        // Define ações do controle de gravação
        final ImageButton btnIniciarGravacao = (ImageButton) dialog.findViewById(R.id.btn_iniciar_gravacao);
        btnIniciarGravacao.setTag(status);
        btnIniciarGravacao.setImageResource(statusImage.get(status));
        btnIniciarGravacao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Integer status = (Integer) btnIniciarGravacao.getTag();
                status = (Status) btnIniciarGravacao.getTag();
                switch (status){
                    case AGUARDANDO_GRAVACAO:
                        // inicia gravação
                        btnIniciarGravacao.setTag(Status.GRAVANDO);
                        btnIniciarGravacao.setImageResource(statusImage.get(Status.GRAVANDO));

                        iniciarGravacao();
                        break;
                    case GRAVANDO:
                        // termina gravação
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                        btnIniciarGravacao.setTag(Status.GRAVACAO_CONCLUIDA);
                        btnIniciarGravacao.setImageResource(statusImage.get(Status.GRAVACAO_CONCLUIDA));

                        pararGravacao();
                        break;
                    case GRAVACAO_CONCLUIDA:
                        // inicia reproducao
                        btnIniciarGravacao.setTag(Status.REPRODUZINDO);
                        btnIniciarGravacao.setImageResource(statusImage.get(Status.REPRODUZINDO));

                        MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // termina reproducao
                                btnIniciarGravacao.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                                btnIniciarGravacao.setTag(Status.GRAVACAO_CONCLUIDA);
                            }

                        };

                        iniciarReproducao(completionListener);
                        break;
                    case REPRODUZINDO:
                        // termina reproducao
                        btnIniciarGravacao.setTag(Status.GRAVACAO_CONCLUIDA);
                        btnIniciarGravacao.setImageResource(statusImage.get(Status.GRAVACAO_CONCLUIDA));

                        pararReproducao();
                        break;
                }

            }
        });
    }

    private void iniciarReproducao(MediaPlayer.OnCompletionListener completionListener) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(fileName);
            mPlayer.setOnCompletionListener(completionListener);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Toast.makeText(context, R.string.msg_erro_reproducao + ": " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void pararReproducao() {
        mPlayer.release();
        mPlayer = null;
    }

    private void iniciarGravacao() {
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(fileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(context, R.string.msg_erro_gravacao + ": " + e.getMessage(), Toast.LENGTH_LONG);
        }

        mRecorder.start();
    }

    private void pararGravacao() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        isRecord = true;
    }
}
