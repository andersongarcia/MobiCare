package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.listeners.DialogAudioListener;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment do chat.
 *
 * @author Anderson Canale Garcia
 */
public class ChatFragment extends Fragment {
    private static final String FILE_PREFIX = "chat_";
    protected static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Permissões a serem solicitadas
    private boolean permissionToRecordAccepted = false;
    protected String [] permissions = {android.Manifest.permission.RECORD_AUDIO};

    private CuidadorService service;
    private MainActivity activity;

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
}
