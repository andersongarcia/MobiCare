package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.ifspsaocarlos.sdm.cuidador.holders.LineHolder;
import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MenuItem;

/**
 * Adapter para lista itens de menu
 *
 * @author Anderson Canale Garcia
 */

public class MenuItemAdapter extends RecyclerView.Adapter<LineHolder> {

    private final List<MenuItem> menuItems;
    private final Context context;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public MenuItemAdapter(Context context, ArrayList items, View.OnClickListener listener) {
        this.context = context;
        menuItems = items;
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lista_simples, parent, false);
        view.setOnClickListener(listener);
        return new LineHolder(view);
    }

    @Override
    public void onBindViewHolder(LineHolder holder, int position) {
        holder.title.setText(String.format(Locale.getDefault(), "%s", context.getResources().getString(menuItems.get(position).getTitulo())));
        holder.icon.setImageResource(menuItems.get(position).getIdImagem());
   }

    @Override
    public int getItemCount() {
        return menuItems != null ? menuItems.size() : 0;
    }

}