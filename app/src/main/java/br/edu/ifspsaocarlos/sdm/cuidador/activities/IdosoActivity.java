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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.AlertaRemedio;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.IMensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.RemediosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.util.MediaPlayerHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdosoActivity extends BaseActivity {
    private static final String TAG = "IdosoActivity";

    private static final String BUNDLE = "bundle";

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_titulo_mensagem)
    TextView tvTituloMensagem;
    @BindView(R.id.bs_mensagem)
    NestedScrollView scrollView;
    @BindView(R.id.action_repetir)
    Button btRepetir;
    @BindView(R.id.action_confirmar)
    Button btConfirmar;


    private BottomSheetBehavior bsBehavior;
    private File localFile;
    private IMensagem mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_idoso);
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        final Window win= getWindow(); win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        service = new UsuarioService(this);

        Log.d(TAG, "Nova mensagem a ser exibida para idoso");

        tvTituloMensagem.getBackground().setAlpha(128);
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
            mensagem = (IMensagem) extras.get(NO.getNo(NO.MENSAGENS));
            //openFragment(ChatIdosoFragment.newInstance(mensagem));
            if (mensagem != null) {
                tvTituloMensagem.setText(mensagem.getTitulo());
                // Toca som padrão para chamar a atenção
                try {
                    MediaPlayer ring= MediaPlayer.create(this, R.raw.jingle);
                    ring.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FotoService.carregarAvatar(service, mensagem.getFotoUri(), ivAvatar, new CallbackSimples() {

                    @Override
                    public void OnComplete() {

                        // Ao terminar de carregar foto, inicia reprodução do áudio
                        try {
                            // Primeiro, carrega arquivo de áudio pela URI
                            localFile = File.createTempFile(mensagem.getId(), ".3gp");
                            service.carregaArquivo(Uri.parse(mensagem.getAudioUri()), localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // carrega arquibo no player e inicia reproducão
                                    reproduzirAudio(true);
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

    private void reproduzirAudio(final boolean alerta) {
        MediaPlayerHelper playerHelper = new MediaPlayerHelper(getBaseContext(), localFile.getAbsolutePath());
        playerHelper.iniciarReproducao(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // ao completar reprodução, mostra botões de ação
                if (mensagem.getOrigem().equals(NO.getNo(NO.REMEDIOS))) {
                    btConfirmar.setText(R.string.menu_confirmar_remedio);
                    if(alerta){
                        RemediosRepository.getInstance().salvaAlertaRemedio(AlertaRemedio.ENVIO, preferencias.getIdosoSelecionadoId(), mensagem.getId());
                    }
                }
                if(mensagem.getOrigem().equals(NO.getNo(NO.MENSAGENS))){
                    btConfirmar.setText(R.string.action_confirmar_mensagem);
                }
                mostraAcoes(true);
            }
        });
    }

    @OnClick(R.id.action_repetir)
    public void onRepetirClick(){
        mostraAcoes(false);
        Log.d(TAG, "replay");
        reproduzirAudio(false);
    }

    @OnClick(R.id.action_confirmar)
    public void onConfirmarClick(){
        // ao clicar na confirmação, salvar horário e fechar tela
        if (mensagem.getOrigem().equals(NO.getNo(NO.REMEDIOS))) {
            RemediosRepository.getInstance().salvaAlertaRemedio(AlertaRemedio.CONFIRMACAO_IDOSO, preferencias.getIdosoSelecionadoId(), mensagem.getId());
        }
        finish();
    }
}
