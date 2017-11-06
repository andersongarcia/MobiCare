package br.edu.ifspsaocarlos.sdm.cuidador.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Created by ander on 05/11/2017.
 */

public class MediaPlayerHelper {
    private MediaPlayer mPlayer;
    private String filename;
    private Context contexto;

    public MediaPlayerHelper(Context contexto, String filename){
        this.contexto = contexto;
        this.filename = filename;
    }

    public void iniciarReproducao(MediaPlayer.OnCompletionListener completionListener){
        this.mPlayer = new MediaPlayer();
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(filename);
            mPlayer.setOnCompletionListener(completionListener);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Toast.makeText(contexto, R.string.msg_erro_reproducao + ": " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void pararReproducao() {
        mPlayer.release();
        mPlayer = null;
    }
}
