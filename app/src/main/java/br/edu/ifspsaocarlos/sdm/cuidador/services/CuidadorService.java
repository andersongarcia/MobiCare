package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseStorage;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
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

    public void removerMedicacao(String idMedicacao) {
        repositorio.removerMedicacao(preferencias.getIdosoSelecionadoId(), idMedicacao);
    }

    public void removerPrograma(String idPrograma) {
        repositorio.removerPrograma(preferencias.getIdosoSelecionadoId(), idPrograma);
    }

    /**
     * Registra usuário logado de acordo com o perfil selecionado
     * @param nome
     * @param telefone
     * @param perfil
     */
    public void registrarUsuario(final String nome, final String telefone, final String perfil) {

        String id = "";
        repositorio.buscarContatoPeloTelefone(telefone, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    // Contato não encontrado
                    // Salva dados de contato do usuário
                    Contato contato = new Contato(nome, telefone);
                    String id = repositorio.salvarContato(contato, new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                        }
                    });
                    criarRegistroUsuario(id, perfil);
                }else {
                    criarRegistroUsuario(dataSnapshot.getKey(), perfil);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void criarRegistroUsuario(String id, String perfil){
        // Cria registro de acordo com perfil selecionado
        switch (perfil){
            case Usuario.CUIDADOR:
                repositorio.salvarCuidador(id);
                break;
            case Usuario.IDOSO:
                repositorio.salvarIdoso(id);
                break;
        }

        preferencias.setUsuarioLogadoId(id);
        preferencias.setUsuarioLogadoPerfil(perfil);
    }

    /**
     * Registra idoso para cuidador selecionado
     * @param nome nome do idoso
     * @param telefone telefone do idoso (identificação)
     */
    public void registrarIdoso(String nome, String telefone) {
        // salva contato do idoso
        Contato contato = new Contato(nome, telefone);
        String id = repositorio.salvarContato(contato, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
            }
        });

        // grava idoso nas preferências
        preferencias.setIdosoSelecionadoId(id);

        // adiciona idoso para cuidador e vice-versa
        repositorio.relacionarCuidadorIdoso(preferencias.getUsuarioLogadoId(), id);
    }


    public void carregarListas() {
        repositorio.carregarListas(preferencias.getIdosoSelecionadoId());
    }

    /**
     * Salva dados do contato relacionado ao idoso
     * @param contato dados do contato
     */
    public void salvarContato(final Contato contato, final Runnable runnable) {
        repositorio.salvarContato(contato, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                repositorio.relacionarContatoIdoso(contato.getId(), preferencias.getIdosoSelecionadoId(), runnable);
            }
        });
    }

    public void removerContato(String idContato, final Runnable runnable) {
        repositorio.removerContato(idContato, preferencias.getIdosoSelecionadoId(), runnable);
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

    public String obterPerfilLogado() {
        return preferencias.getUsuarioLogadoPerfil();
    }
}
