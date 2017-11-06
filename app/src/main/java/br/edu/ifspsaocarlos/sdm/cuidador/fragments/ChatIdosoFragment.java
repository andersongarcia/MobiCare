package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.BaseActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.util.MediaPlayerHelper;

/**
 * Fragment do chat do idoso
 *
 * @author Anderson Canale Garcia
 */
public class ChatIdosoFragment extends Fragment {

    private CuidadorService service;
    private BaseActivity activity;
    private TextView tvDescription;
    private IMensagem mensagem;

    public static ChatIdosoFragment newInstance(IMensagem mensagem) {
        ChatIdosoFragment fragment = new ChatIdosoFragment();
        fragment.setMensagem(mensagem);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_idoso, container, false);

        activity = (BaseActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.app_name));

        service = new CuidadorService(activity);

        final ImageView ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        ivAvatar.setImageResource(R.drawable.logo);

        tvDescription = (TextView) view.findViewById(R.id.tv_description);

        if(mensagem != null){
            FotoService.carregarAvatar(service, mensagem.getFotoUri(), ivAvatar, new CallbackSimples(){

                @Override
                public void OnComplete() {
                    // Ao terminar de carregar foto, inicia reprodução do áudio
                    try {
                        // Primeiro, carrega arquivo de áudio pela URI
                        final File localFile = File.createTempFile(mensagem.getId(), ".3gp");
                        service.carregaArquivo(Uri.parse(mensagem.getAudioUri()), localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // carrega arquibo no player e inicia reproducão
                                MediaPlayerHelper playerHelper = new MediaPlayerHelper(activity, localFile.getAbsolutePath());
                                playerHelper.iniciarReproducao(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        // ao completar reprodução, altera ícone na tela
                                    }
                                });
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

                }
            });
        }

        return view;
    }

    public void setMensagem(IMensagem mensagem) {
        this.mensagem = mensagem;
    }
}
