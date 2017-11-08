package br.edu.ifspsaocarlos.sdm.cuidador.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MensagemSet;
import br.edu.ifspsaocarlos.sdm.cuidador.interfaces.RecyclerViewOnItemSelecionado;

/**
 * Created by ander on 07/11/2017.
 */

public class MensagemSetListAdapter extends RecyclerView.Adapter<MensagemSetListAdapter.MensagemSetHolder> {
    private List<MensagemSet> lista;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnItemSelecionado meuRecyclerViewOnItemSelecionado;

    public MensagemSetListAdapter(Context c, List<MensagemSet> l) {
        lista = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MensagemSetListAdapter.MensagemSetHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mLayoutInflater.inflate(R.layout.item_lista, parent, false);
        MensagemSetListAdapter.MensagemSetHolder mvh = new MensagemSetListAdapter.MensagemSetHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MensagemSetListAdapter.MensagemSetHolder holder, int position) {

        holder.ivAvatar.setImageResource(R.drawable.ic_play_circle_outline_black_36dp);
        holder.tvNome.setText(lista.get(position).getEmissor().getNome());
        holder.tvHora.setText(lista.get(position).getMensagem().obterDataHora());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setRecyclerViewOnItemSelecionado(RecyclerViewOnItemSelecionado r){

        meuRecyclerViewOnItemSelecionado = r;
    }

    public class MensagemSetHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvNome;
        public TextView tvHora;
        public ImageView ivAvatar;

        public MensagemSetHolder(View itemView) {

            super(itemView);

            ivAvatar = (ImageView) itemView.findViewById(R.id.list_avatar);
            tvNome = (TextView) itemView.findViewById(R.id.lista_titulo);
            tvHora = (TextView) itemView.findViewById(R.id.lista_descricao);

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