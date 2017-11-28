package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.MensagemSetListAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MensagemSet;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;
import br.edu.ifspsaocarlos.sdm.cuidador.listeners.DialogAudioListener;
import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment do chat.
 *
 * @author Anderson Canale Garcia
 */
public class ChatFragment extends Fragment implements RecyclerViewOnItemSelecionado {
    protected static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String TAG = "ChatFragment";

    // Permissões a serem solicitadas
    private boolean permissionToRecordAccepted = false;
    protected String [] permissions = {android.Manifest.permission.RECORD_AUDIO};

    private UsuarioService service;
    private MainActivity activity;

    @BindView(R.id.rv_chat)
    RecyclerView recyclerView;
    @BindView(R.id.ll_empty_view)
    View emptyView;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.tv_empty_view_help)
    TextView tvEmptyViewHelp;

    private MensagemSetListAdapter adapter;

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

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.mensagens));

        service = new UsuarioService(activity);

        tvEmptyView.setText(R.string.nenhuma_mensagem);
        tvEmptyViewHelp.setText(R.string.chat_empty);

        //region Lista de mensagens
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        adapter = new MensagemSetListAdapter(activity, activity.getPreferencias().getIdosoSelecionadoId());
        adapter.setRecyclerViewOnItemSelecionado(this);
        adapter.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //endregion


        String path = activity.getExternalCacheDir().getAbsolutePath();
        String filename = String.valueOf(System.currentTimeMillis());
        filename = path + "/" + filename;
        final DialogAudioListener dialogAudioListener = new DialogAudioListener(activity, filename);

        view.findViewById(R.id.btn_cadastrar).setOnClickListener(dialogAudioListener);

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        dialogAudioListener.setButton(DialogAudioListener.TipoBotao.POSITIVO, R.string.enviar, new Runnable() {
            @Override
            public void run() {
                if(dialogAudioListener.getFileName() != null && !dialogAudioListener.getFileName().isEmpty()){
                    dialogAudioListener.setStatus(DialogAudioListener.Status.AGUARDANDO_GRAVACAO);
                    service.salvaAudioChat(dialogAudioListener.getFileName());
                }
            }
        });

        return view;
    }

    @Override
    public void onItemSelecionado(final View view, int posicao) {
        MensagemSet mensagemSet = adapter.getItem(posicao);

        final String path = activity.getExternalCacheDir().getAbsolutePath();
        final String filename = String.valueOf(System.currentTimeMillis());

        // Tenta carregar instrução já gravada
        if(mensagemSet.getMensagem().getAudioUri() != null && !mensagemSet.getMensagem().getAudioUri().isEmpty()){
            try {
                final File localFile = File.createTempFile(filename, ".3gp");

                service.carregaArquivo(Uri.parse(mensagemSet.getMensagem().getAudioUri()), localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        DialogAudioListener dialogAudioListener = new DialogAudioListener(activity, localFile.getAbsolutePath());
                        dialogAudioListener.setTitle(R.string.ouvir_mensagem);
                        dialogAudioListener.setStatus(DialogAudioListener.Status.GRAVACAO_CONCLUIDA);

                        dialogAudioListener.onClick(view);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Erro ao carregar áudio");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG, "Áudio não existente na mensagem");
        }


    }
}
