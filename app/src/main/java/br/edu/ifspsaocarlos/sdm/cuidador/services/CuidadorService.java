package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.data.CuidadorFirebaseStorage;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;


/**
 * Classe de serviço para usuário
 *
 * @author Anderson Canale Garcia
 */
public class CuidadorService {
    private static final String TAG = "CuidadorService";
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
        MENSAGENS,
        CONTADOR_ALARME;

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
                case CONTADOR_ALARME:
                    return "contadorAlarme";
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

    public void removeRemedio(String remedioId) {
        repositorio.removeRemedio(preferencias.getIdosoSelecionadoId(), remedioId);
    }

    public void removePrograma(String idPrograma) {
        repositorio.removePrograma(preferencias.getIdosoSelecionadoId(), idPrograma);
    }

    public void registraCuidadorIdoso(final String nome, final String telefone, final String nomeIdoso, final String telefoneIdoso) {
        // busca contato do cuidador
        buscaContato(telefone, new CallbackGenerico<Contato>() {
            @Override
            public void OnComplete(Contato contato) {
                // Verifica se encontrou contato
                // Em caso positivo, registra id como cuidador
                if(contato != null){
                    defineCuidadorRegistrarIdoso(contato, nomeIdoso, telefoneIdoso);
                }else {
                    // Se não encontrou contato, cria-o, e depois registra o id como cuidador
                    contato = new Contato(nome, telefone);
                    repositorio.salvaContato(contato, new CallbackGenerico<Contato>() {
                        @Override
                        public void OnComplete(Contato c) {
                            // Se correr tudo bem, registra novo contato como cuidador
                            defineCuidadorRegistrarIdoso(c, nomeIdoso, telefoneIdoso);
                        }
                    });
                }
            }
        });
    }

    private void defineCuidadorRegistrarIdoso(Contato contato, String nomeIdoso, String telefoneIdoso) {
        registraUsuario(contato.getId(), Usuario.CUIDADOR);
        repositorio.salvarCuidador(contato.getId());
        // inicia registro do idoso
        registraIdoso(nomeIdoso, telefoneIdoso);
    }

    public void registraUsuario(String id, String perfil) {
        preferencias.setUsuarioLogadoId(id);
        preferencias.setUsuarioLogadoPerfil(perfil);
    }

    public void registraUsuarioIdoso(String id) {
        registraUsuario(id, Usuario.IDOSO);
        preferencias.setIdosoSelecionadoId(id);
    }

    public void registraUsuarioContato(String id) {
        registraUsuario(id, Usuario.CONTATO);
        repositorio.buscaIdosoDoContato(id, new CallbackGenerico<String>() {
            @Override
            public void OnComplete(String idosoId) {
                preferencias.setIdosoSelecionadoId(idosoId);
            }
        });
    }

    /**
     * Registra idoso para cuidador selecionado
     * @param nome nome do idoso
     * @param telefone telefone do idoso (identificação)
     */
    public void registraIdoso(final String nome, final String telefone) {

        buscaContato(telefone, new CallbackGenerico<Contato>() {
            @Override
            public void OnComplete(Contato contato) {
                if(contato != null){
                    preferencias.setIdosoSelecionadoId(contato.getId());
                    repositorio.salvaIdoso(contato.getId());
                }else {
                    contato = new Contato(nome, telefone);
                    repositorio.salvaContato(contato, new CallbackGenerico<Contato>() {
                        @Override
                        public void OnComplete(Contato c) {
                            // grava idoso nas preferências
                            preferencias.setIdosoSelecionadoId(c.getId());
                            // salva idoso
                            repositorio.salvaIdoso(c.getId());
                        }
                    });
                }
            }
        });
    }

    public void carregaListas() {
        repositorio.carregaListas(preferencias.getIdosoSelecionadoId());
    }

    public void buscaContato(String telefone, final CallbackGenerico<Contato> callback) {
        repositorio.buscaContatoPeloTelefone(telefone, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    // carrega contato
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // só traz 1 resultado
                        Contato contato = snapshot.getValue(Contato.class);
                        callback.OnComplete(contato);
                    }
                }else {
                    callback.OnComplete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(contexto, R.string.msg_erro_busca_contato, Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Salva dados do contato relacionado ao idoso
     * @param contato dados do contato
     * @param callback
     */
    public void salvaContato(Contato contato, final CallbackGenerico<Contato> callback) {
        repositorio.salvaContato(contato, new CallbackGenerico<Contato>() {
            @Override
            public void OnComplete(Contato c) {
                repositorio.relacionaContatoIdoso(c.getId(), preferencias.getIdosoSelecionadoId());
                callback.OnComplete(c);
            }
        });
    }

    public void removeContato(String idContato, final Runnable runnable) {
        repositorio.removeContato(idContato, preferencias.getIdosoSelecionadoId(), runnable);
    }

    public void salvaRemedio(Remedio remedio) {
        if(remedio.getId() == null || remedio.getId().isEmpty()){
            repositorio.adicionaRemedio(preferencias.getIdosoSelecionadoId(), remedio);
        }else {
            repositorio.atualizaRemedio(preferencias.getIdosoSelecionadoId(), remedio);
        }
    }

    public void salvaPrograma(Programa programa) {
        if(programa.getId() == null || programa.getId().isEmpty()){
            repositorio.adicionaPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }else {
            repositorio.atualizaPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }
    }

    public void salvaAudioInstrucao(String fileName, String remedioId, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        CuidadorFirebaseStorage.getInstance().salvaAudioInstrucao(preferencias.getIdosoSelecionadoId(), remedioId, fileName, onSuccessListener);
    }

    public void carregaInstrucaoURI(String remedioId, OnSuccessListener<Uri> successListener, OnFailureListener failureListener){
        CuidadorFirebaseStorage.getInstance().carregaInstrucaoURI(preferencias.getIdosoSelecionadoId(), remedioId, successListener, failureListener);
    }

    public void carregaArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        CuidadorFirebaseStorage.getInstance().carregaArquivo(uri, localFile, successListener, failureListener);
    }

    public String obterIdLogado() {
        return preferencias.getUsuarioLogadoId();
    }

    public String obterPerfilLogado() {
        return preferencias.getUsuarioLogadoPerfil();
    }

    public void salvaAudioChat(String fileName) {
        CuidadorFirebaseStorage.getInstance().salvaAudioChat(preferencias.getIdosoSelecionadoId(), preferencias.getUsuarioLogadoId(), fileName);
    }

    public void leNovasMensagens(ChildEventListener listener) {
        repositorio.leNovasMensagens(preferencias.getIdosoSelecionadoId(), listener);
    }

    public void efetuaLogout() {
        preferencias.setUsuarioLogadoId(null);
        preferencias.setIdosoSelecionadoId(null);
    }

    public UploadTask salvaFoto(NO no, String id, File arquivoFoto) {
        return CuidadorFirebaseStorage.getInstance().salvarArquivo(NO.getNo(no), id, arquivoFoto, contexto);
    }

    public UploadTask salvaFotoPerfil(File arquivoFoto) {
        return salvaFoto(NO.CONTATOS, preferencias.getUsuarioLogadoId(), arquivoFoto);
    }

    public void carregaFotoURI(NO no, String id, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        CuidadorFirebaseStorage.getInstance().carregaFotoURI(NO.getNo(no), id, successListener, failureListener);
    }


    public void obterRemedios() {

    }
}