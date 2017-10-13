package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;
import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Adapter de contato.
 *
 * @author Anderson Canale Garcia
 */
public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ContatoHolder> {

    private List<Contato> listaContatos;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnItemSelecionado meuRecyclerViewOnItemSelecionado;

    public ContatoAdapter(Context c, List<Contato> l) {

        listaContatos = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ContatoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mLayoutInflater.inflate(R.layout.item_lista, parent, false);
        ContatoHolder mvh = new ContatoHolder(v);

        return mvh;
    }

    public void adicionarItemLista(Contato c, int position) {

        listaContatos.add(c);
        notifyItemInserted(position);

    }

    @Override
    public void onBindViewHolder(ContatoHolder holder, int position) {

        holder.tvNome.setText(listaContatos.get(position).getNome());
        holder.tvTelefone.setText(listaContatos.get(position).getTelefone());
        holder.ivAvatar.setImageResource(R.drawable.ic_person_black_48dp);
    }

    @Override
    public int getItemCount() {
        if(listaContatos != null)
            return listaContatos.size();

        return 0;
    }

    public void setRecyclerViewOnItemSelecionado(RecyclerViewOnItemSelecionado r){

        meuRecyclerViewOnItemSelecionado = r;
    }

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

        @Override
        public void onClick(View v) {
            if(meuRecyclerViewOnItemSelecionado != null){
                meuRecyclerViewOnItemSelecionado.onItemSelecionado(v, getAdapterPosition());
            }
        }
    }
}
