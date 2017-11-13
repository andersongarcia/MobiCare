package br.edu.ifspsaocarlos.sdm.cuidador.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.AbstractChat;

/**
 * Created by ander on 12/11/2017.
 */

public class ChatHolder extends RecyclerView.ViewHolder {

    private final TextView tvTitulo;
    private final TextView tvSubtitulo;

    public ChatHolder(View itemView) {
        super(itemView);
        tvTitulo = (TextView) itemView.findViewById(R.id.lista_titulo);
        tvSubtitulo = (TextView) itemView.findViewById(R.id.lista_descricao);
    }

    public void bind(AbstractChat chat) {
        setName(chat.getName());
        setText(chat.getMessage());
    }

    private void setName(String name) {
        tvTitulo.setText(name);
    }

    private void setText(String text) {
        tvSubtitulo.setText(text);
    }
}
