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

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Created by ander on 19/10/2017.
 */

public class DialogAudioListener implements View.OnClickListener {

    // status para gravação de instrução
    private static final int AGUARDANDO_GRAVACAO = 0;
    private static final int GRAVANDO = 1;
    private static final int GRAVACAO_CONCLUIDA = 2;
    private static final int REPRODUZINDO = 3;
    private final Context context;
    private MediaPlayer mPlayer;
    private String fileName;
    private MediaRecorder mRecorder;

    public String getFileName() {
        return fileName;
    }

    // botões do dialog
    public enum TipoBotao { POSITIVO, NEGATIVO, NEUTRO };

    private final int dialog_title;
    private final int dialog_layout;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public DialogAudioListener(Context context, String fileName){
        this.context = context;
        String path = context.getExternalCacheDir().getAbsolutePath();
        this.fileName = path + "/" + fileName;

        // Cria o gerador do AlertDialog
        builder = new AlertDialog.Builder(context);

        // configurações padrão
        this.dialog_title = R.string.gravar_audio;
        this.dialog_layout = R.layout.dialog_gravar_instrucao;
    }


    public void setTitle(int resTitle){
        builder.setTitle(resTitle);
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
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
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
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
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
    }
}
