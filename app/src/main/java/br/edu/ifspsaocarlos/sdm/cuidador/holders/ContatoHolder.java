package br.edu.ifspsaocarlos.sdm.cuidador.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;

/**
 * Created by ander on 12/11/2017.
 */

public class ContatoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvNome;
    public TextView tvTelefone;
    public ImageView ivAvatar;

    public ContatoHolder(View itemView) {

        super(itemView);

        tvNome = (TextView) itemView.findViewById(R.id.lista_titulo);
        tvTelefone = (TextView) itemView.findViewById(R.id.lista_descricao);
        ivAvatar = (ImageView) itemView.findViewById(R.id.list_avatar);

        itemView.setOnClickListener(this);
    }

    public void bind(Contato model) {
        this.tvNome.setText(model.getNome());
        this.tvTelefone.setText(model.getTelefone());
    }

    @Override
    public void onClick(View v) {
    }
}
