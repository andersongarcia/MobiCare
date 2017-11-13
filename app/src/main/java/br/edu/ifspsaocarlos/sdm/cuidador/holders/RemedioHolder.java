package br.edu.ifspsaocarlos.sdm.cuidador.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;

/**
 * Created by ander on 11/11/2017.
 */
public class RemedioHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvNome;
    public TextView tvHorario;

    public RemedioHolder(View itemView) {

        super(itemView);

        tvNome = (TextView) itemView.findViewById(R.id.lista_titulo);
        tvHorario = (TextView) itemView.findViewById(R.id.lista_descricao);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    }

    public void bind(Remedio model) {
        this.tvNome.setText(model.getNome());
        this.tvHorario.setText(model.getHorario());
    }
}
