package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseStorage;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Idoso;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

/**
 * Classe de serviço para usuário
 *
 * @author Anderson Canale Garcia
 */
public class CuidadorService {
    private final Context contexto;
    private final PreferenciaHelper preferencias;
    private final CuidadorFirebaseRepository repositorio;

    public CuidadorService(Context context){
        this.contexto = context;
        this.preferencias = new PreferenciaHelper(context);
        this.repositorio = CuidadorFirebaseRepository.getInstance();
    }

    public boolean verificaUsuarioLogado() {
        String usuarioLogadoId = preferencias.getUsuarioLogadoId();

        return (usuarioLogadoId != null && !usuarioLogadoId.isEmpty());
    }

    // Verifica se já possui idoso selecionado nas preferências
    public boolean verificaIdosoSelecionado() {
        String idosoSelecionadoId = preferencias.getIdosoSelecionadoId();

        return (idosoSelecionadoId != null && !idosoSelecionadoId.isEmpty());
    }

    public void removerContato(String idContato) {
        repositorio.removerContato(preferencias.getIdosoSelecionadoId(), idContato);
    }

    public void removerMedicacao(String idMedicacao) {
        repositorio.removerMedicacao(preferencias.getIdosoSelecionadoId(), idMedicacao);
    }

    public void removerPrograma(String idPrograma) {
        repositorio.removerPrograma(preferencias.getIdosoSelecionadoId(), idPrograma);
    }

    public void registrarIdoso(String nome, String telefone) {
        Idoso idoso = repositorio.adicionarIdoso(preferencias.getUsuarioLogadoId(), nome, telefone);
        preferencias.setIdosoSelecionadoId(idoso.getId());
    }

    public void registrarUsuario(String nome, String telefone, String perfil) {
        Usuario usuario = repositorio.criarUsuario(nome, telefone, perfil);
        preferencias.setUsuarioLogadoId(usuario.getId());
    }

    public void carregarListas() {
        repositorio.carregarListas(preferencias.getIdosoSelecionadoId());
    }

    public void salvarContato(Contato contato) {
        if(contato.getId() == null || contato.getId().isEmpty()){
            repositorio.adicionarContato(preferencias.getIdosoSelecionadoId(), contato);
        }else{
            repositorio.atualizarContato(preferencias.getIdosoSelecionadoId(), contato);
        }
    }

    public void salvarMedicacao(Medicacao medicacao) {
        if(medicacao.getId() == null || medicacao.getId().isEmpty()){
            repositorio.adicionarMedicacao(preferencias.getIdosoSelecionadoId(), medicacao);
        }else {
            repositorio.atualizarMedicacao(preferencias.getIdosoSelecionadoId(), medicacao);
        }
    }

    public void salvarPrograma(Programa programa) {
        if(programa.getId() == null || programa.getId().isEmpty()){
            repositorio.adicionarPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }else {
            repositorio.atualizarPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }
    }

    public void salvarAudioInstrucao(String fileName, String medicacaoId) {
        CuidadorFirebaseStorage.getInstance().salvarAudioInstrucao(preferencias.getIdosoSelecionadoId(), medicacaoId, fileName);
    }

    public void carregaInstrucaoURI(String medicacaoId, OnSuccessListener<Uri> successListener, OnFailureListener failureListener){
        CuidadorFirebaseStorage.getInstance().carregaInstrucaoURI(preferencias.getIdosoSelecionadoId(), medicacaoId, successListener, failureListener);
    }

    public void carregarArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        CuidadorFirebaseStorage.getInstance().carregaArquivo(uri, localFile, successListener, failureListener);
    }
}
