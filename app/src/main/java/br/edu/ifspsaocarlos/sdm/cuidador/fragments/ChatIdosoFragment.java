package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment do chat do idoso
 *
 * @author Anderson Canale Garcia
 */
public class ChatIdosoFragment extends Fragment {

    private CuidadorService service;
    private MainActivity activity;

    public static ChatIdosoFragment newInstance() {
        ChatIdosoFragment fragment = new ChatIdosoFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_idoso, container, false);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.app_name));

        ImageView ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        ivAvatar.setImageResource(R.drawable.logo);


        return view;
    }

}
