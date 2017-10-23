package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;

/**
 * Adapter de contato.
 *
 * @author Anderson Canale Garcia
 */
public class RemedioAdapter extends RecyclerView.Adapter<RemedioAdapter.RemedioHolder> {

    private List<Remedio> lista;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnItemSelecionado meuRecyclerViewOnItemSelecionado;

    public RemedioAdapter(Context c, List<Remedio> l) {

        lista = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RemedioHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mLayoutInflater.inflate(R.layout.item_lista, parent, false);
        RemedioHolder mvh = new RemedioHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(RemedioHolder holder, int position) {

        holder.tvNome.setText(lista.get(position).getNome());
        holder.tvHorarios.setText(lista.get(position).getHorarios());
    }

    @Override
    public int getItemCount() {

        return lista.size();
    }

    public void setRecyclerViewOnItemSelecionado(RecyclerViewOnItemSelecionado r){

        meuRecyclerViewOnItemSelecionado = r;
    }

    public class RemedioHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvNome;
        public TextView tvHorarios;

        public RemedioHolder(View itemView) {

            super(itemView);

            tvNome = (TextView) itemView.findViewById(R.id.lista_titulo);
            tvHorarios = (TextView) itemView.findViewById(R.id.lista_descricao);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(meuRecyclerViewOnItemSelecionado != null){
                meuRecyclerViewOnItemSelecionado.onItemSelecionado(v, getAdapterPosition());
            }
        }
    }
}
