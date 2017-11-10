package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.util.MediaPlayerHelper;

public class IdosoActivity extends BaseActivity {
    private static final String TAG = "IdosoActivity";

    private static final String BUNDLE = "bundle";
    private ImageView ivAvatar;
    private TextView tvTituloMensagem;
    private NestedScrollView scrollView;
    private BottomSheetBehavior bsBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_idoso);
        super.onCreate(savedInstanceState);

        final Window win= getWindow(); win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        service = new CuidadorService(this);

        Log.d(TAG, "Nova mensagem a ser exibida para idoso");

        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivAvatar.setImageResource(R.drawable.logo);

        tvTituloMensagem = (TextView) findViewById(R.id.tv_titulo_mensagem);
        tvTituloMensagem.getBackground().setAlpha(128);
        scrollView = (NestedScrollView) findViewById(R.id.bs_mensagem);
        scrollView.getBackground().setAlpha(128);
        bsBehavior = BottomSheetBehavior.from(scrollView);
        mostraAcoes(false);

        verMensagem();

    }

    private void mostraAcoes(boolean mostra){
        if(mostra) {
            bsBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        else {
            bsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bsBehavior.setPeekHeight(0);
        }
    }

    private void verMensagem() {


        Bundle extras = getIntent().getBundleExtra(BUNDLE);

        if (extras != null) {
            final IMensagem mensagem = (IMensagem) extras.get(CuidadorService.NO.getNo(CuidadorService.NO.MENSAGENS));
            //openFragment(ChatIdosoFragment.newInstance(mensagem));
            if (mensagem != null) {
                // Toca som padrão para chamar a atenção
                /*try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                FotoService.carregarAvatar(service, mensagem.getFotoUri(), ivAvatar, new CallbackSimples() {

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
                                    MediaPlayerHelper playerHelper = new MediaPlayerHelper(getBaseContext(), localFile.getAbsolutePath());
                                    playerHelper.iniciarReproducao(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            // ao completar reprodução, mostra botões de ação
                                            if (mensagem.getOrigem().equals(CuidadorService.NO.getNo(CuidadorService.NO.REMEDIOS))) {
                                                service.notificarCuidador(mensagem.getId());
                                                mostraAcoes(true);
                                            }
                                        }
                                    });
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        }

    }
}
