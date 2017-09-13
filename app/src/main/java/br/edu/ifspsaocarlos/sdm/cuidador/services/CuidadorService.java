package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Idoso;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import io.realm.RealmList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Classe de serviço para usuário
 *
 * @author Anderson Canale Garcia
 */
public class CuidadorService {
    Context context;

    public CuidadorService(Context context){
        this.context = context;
    }

    public Usuario obterUsuarioLogado()
    {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
        String telefoneLogado = preferences.getString(context.getString(R.string.chaveUsuarioLogado), "");

        CuidadorRepository dao = new CuidadorRepository(context);
        Usuario usuario = dao.buscaUsuarioPeloTelefone(telefoneLogado);

        return usuario;
    }

    public ArrayList<Contato> obterContatos(){
        Idoso idoso = obterIdosoSelecionado();

        RealmList<Contato> contatos = idoso.getContatos();
        return new ArrayList<>(contatos);
    }

    public Idoso obterIdosoSelecionado() {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
        String idosoSelecionado = preferences.getString(context.getString(R.string.chaveIdosoSelecionado), "");

        CuidadorRepository repository = new CuidadorRepository(context);
        Idoso idoso = repository.buscaIdosoPeloTelefone(idosoSelecionado);

        return idoso;
    }

    public void adicionarContato(Contato contato) {
        Idoso idoso = obterIdosoSelecionado();

        CuidadorRepository repository = new CuidadorRepository(context);
        repository.adicionarContato(idoso, contato);
    }

    public void removerContato(String telefone) {
        CuidadorRepository repository = new CuidadorRepository(context);
        repository.removerContato(telefone);
    }

    public List<Medicacao> obterMedicacoes() {
        Idoso idoso = obterIdosoSelecionado();

        RealmList<Medicacao> medicacoes = idoso.getMedicacoes();
        return new ArrayList<>(medicacoes);
    }

    public void adicionarMedicacao(Medicacao medicacao) {
        Idoso idoso = obterIdosoSelecionado();

        CuidadorRepository repository = new CuidadorRepository(context);
        repository.adicionarMedicacao(idoso, medicacao);
    }

    public void removerMedicacao(String nome) {
        CuidadorRepository repository = new CuidadorRepository(context);
        repository.removerMedicacao(nome);
    }

    public List<Programa> obterProgramas() {
        Idoso idoso = obterIdosoSelecionado();

        RealmList<Programa> programas = idoso.getProgramas();
        return new ArrayList<>(programas);
    }

    public void adicionarPrograma(Programa programa) {
        Idoso idoso = obterIdosoSelecionado();

        CuidadorRepository repository = new CuidadorRepository(context);
        repository.adicionarPrograma(idoso, programa);
    }
}
