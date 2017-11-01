package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseStorage;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

/**
 * Classe de serviço para usuário
 *
 * @author Anderson Canale Garcia
 */
public class CuidadorService {
    public static final String NO_INSTRUCOES = "instrucoes";
    public static final String NO_CHAT = "instrucoes";

    public enum NO {
        CONTATOS,
        CUIDADORES,
        IDOSOS,
        REMEDIOS,
        CHAT,
        INSTRUCOES,
        FOTOS,
        PROGRAMAS,
        MENSAGENS;

        public static String getNo(NO no) {
            switch (no) {
                case CONTATOS:
                    return "contatos";
                case CUIDADORES:
                    return "cuidadores";
                case IDOSOS:
                    return "idosos";
                case REMEDIOS:
                    return "remedios";
                case CHAT:
                    return "chat";
                case INSTRUCOES:
                    return "instrucoes";
                case FOTOS:
                    return "fotos";
                case PROGRAMAS:
                    return "programas";
                case MENSAGENS:
                    return "mensagens";
                default:
                    return "";
            }
        }
    }

    private final Context contexto;
    private final PreferenciaHelper preferencias;
    private final CuidadorFirebaseRepository repositorio;
    private String idosoId;

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

    public void removerRemedio(String remedioId) {
        repositorio.removerRemedio(preferencias.getIdosoSelecionadoId(), remedioId);
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
                    for(DataSnapshot contato : dataSnapshot.getChildren()){
                        String key = contato.getKey();
                        criarRegistroUsuario(key, perfil);
                    }
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
     * @param runnable
     */
    public void registrarIdoso(final String nome, final String telefone, final Runnable runnable) {

        repositorio.buscarContatoPeloTelefone(telefone, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    // Contato não encontrado
                    // Salva dados de contato do usuário
                    final Contato contato = new Contato(nome, telefone);
                    idosoId = repositorio.salvarContato(contato, new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            // salva idoso
                            repositorio.salvarIdoso(idosoId);
                            // grava idoso nas preferências
                            preferencias.setIdosoSelecionadoId(idosoId);
                            runnable.run();
                        }
                    });
                }else {
                    // salva idoso
                    for(DataSnapshot contato : dataSnapshot.getChildren()){ // só traz 1 resultado
                        String key = contato.getKey();
                        repositorio.salvarIdoso(key);
                        // grava idoso nas preferências
                        preferencias.setIdosoSelecionadoId(key);
                        runnable.run();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

    public void salvarRemedio(Remedio remedio) {
        if(remedio.getId() == null || remedio.getId().isEmpty()){
            repositorio.adicionarRemedio(preferencias.getIdosoSelecionadoId(), remedio);
        }else {
            repositorio.atualizarRemedio(preferencias.getIdosoSelecionadoId(), remedio);
        }
    }

    public void salvarPrograma(Programa programa) {
        if(programa.getId() == null || programa.getId().isEmpty()){
            repositorio.adicionarPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }else {
            repositorio.atualizarPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }
    }

    public void salvarAudioInstrucao(String fileName, String remedioId, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        CuidadorFirebaseStorage.getInstance().salvarAudioInstrucao(preferencias.getIdosoSelecionadoId(), remedioId, fileName, onSuccessListener);
    }

    public void carregaInstrucaoURI(String remedioId, OnSuccessListener<Uri> successListener, OnFailureListener failureListener){
        CuidadorFirebaseStorage.getInstance().carregaInstrucaoURI(preferencias.getIdosoSelecionadoId(), remedioId, successListener, failureListener);
    }

    public void carregarArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        CuidadorFirebaseStorage.getInstance().carregarArquivo(uri, localFile, successListener, failureListener);
    }

    public String obterPerfilLogado() {
        return preferencias.getUsuarioLogadoPerfil();
    }

    public void salvarAudioChat(String fileName) {
        CuidadorFirebaseStorage.getInstance().salvarAudioChat(preferencias.getIdosoSelecionadoId(), preferencias.getUsuarioLogadoId(), fileName);
    }

    public void lerNovasMensagens(ChildEventListener listener) {
        repositorio.lerNovasMensagens(preferencias.getIdosoSelecionadoId(), listener);
    }

    public void efetuarLogout() {
        preferencias.setUsuarioLogadoId(null);
        preferencias.setIdosoSelecionadoId(null);
    }

    public void salvarFoto(NO no, String id, File arquivoFoto, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        CuidadorFirebaseStorage.getInstance().salvarArquivo(NO.getNo(no), id, arquivoFoto, onSuccessListener);
    }

    public void carregarFotoURI(NO no, String id, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        CuidadorFirebaseStorage.getInstance().carregaFotoURI(NO.getNo(no), id, successListener, failureListener);
    }
}

