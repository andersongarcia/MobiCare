package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment do chat do idoso
 *
 * @author Anderson Canale Garcia
 */
public class ChatIdosoFragment extends Fragment {

    private CuidadorService service;
    private MainActivity activity;
    private TextView tvDescription;

    public static ChatIdosoFragment newInstance() {
        ChatIdosoFragment fragment = new ChatIdosoFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_idoso, container, false);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.app_name));

        service = new CuidadorService(activity);

        ImageView ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        ivAvatar.setImageResource(R.drawable.logo);

        tvDescription = (TextView) view.findViewById(R.id.tv_description);

        service.lerNovasMensagens(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                tvDescription.setText("nova mensagem: " + mensagem.getId());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
