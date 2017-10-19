package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
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

    private CuidadorService service;
    private MainActivity activity;

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

        Long l = System.currentTimeMillis() / 1000;
        String fileName = FILE_PREFIX + l.toString();
        final DialogAudioListener dialogAudioListener = new DialogAudioListener(activity, fileName);

        view.findViewById(R.id.btn_cadastrar).setOnClickListener(dialogAudioListener);

        dialogAudioListener.setButton(DialogAudioListener.TipoBotao.POSITIVO, R.string.enviar, new Runnable() {
            @Override
            public void run() {
                service.salvarAudioChat(dialogAudioListener.getFileName());
            }
        });

        return view;
    }
}
