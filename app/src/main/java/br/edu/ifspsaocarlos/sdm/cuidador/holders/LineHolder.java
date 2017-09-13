package br.edu.ifspsaocarlos.sdm.cuidador.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * ViewHolder para item de lista de menu
 *
 * @author Anderson Canale Garcia
 */

public class LineHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public ImageView icon;
    public ImageButton deleteButton;

    public LineHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.lista_simples_titulo);
        icon = (ImageView) itemView.findViewById(R.id.lista_simples_avatar);
    }
}