package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;

/**
 * Adapter de contato.
 *
 * @author Anderson Canale Garcia
 */
public class MedicacaoAdapter extends RecyclerView.Adapter<MedicacaoAdapter.MedicacaoHolder> {

    private List<Medicacao> lista;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnItemSelecionado meuRecyclerViewOnItemSelecionado;

    public MedicacaoAdapter(Context c, List<Medicacao> l) {

        lista = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MedicacaoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mLayoutInflater.inflate(R.layout.item_lista, parent, false);
        MedicacaoHolder mvh = new MedicacaoHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MedicacaoHolder holder, int position) {

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

    public class MedicacaoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvNome;
        public TextView tvHorarios;

        public MedicacaoHolder(View itemView) {

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
