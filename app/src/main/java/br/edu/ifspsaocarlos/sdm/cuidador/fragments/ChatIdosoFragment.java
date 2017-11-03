package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.BaseActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Fragment do chat do idoso
 *
 * @author Anderson Canale Garcia
 */
public class ChatIdosoFragment extends Fragment {

    private CuidadorService service;
    private BaseActivity activity;
    private TextView tvDescription;
    private Mensagem mensagem;

    public static ChatIdosoFragment newInstance(Mensagem mensagem) {
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
            //activity.carregarAvatar(CuidadorService.NO.CONTATOS, mensagem.getEmissorId(), ivAvatar);
        }

        return view;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }
}
