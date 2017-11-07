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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.MensagemSetListAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MensagemSet;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;
import br.edu.ifspsaocarlos.sdm.cuidador.listeners.DialogAudioListener;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

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

    private CuidadorService service;
    private MainActivity activity;

    private RecyclerView recyclerView;
    private List<MensagemSet> listaMensagens;

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

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.app_name));

        service = new CuidadorService(activity);

        //region Lista de mensagens
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        listaMensagens = CuidadorFirebaseRepository.getInstance().getMensagens();
        final MensagemSetListAdapter adapter = new MensagemSetListAdapter(getActivity(), listaMensagens);
        adapter.setRecyclerViewOnItemSelecionado(this);
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
                    service.salvaAudioChat(dialogAudioListener.getFileName(), new CallbackSimples(){

                        @Override
                        public void OnComplete() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onItemSelecionado(final View view, int posicao) {
        MensagemSet mensagemSet = listaMensagens.get(posicao);

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
