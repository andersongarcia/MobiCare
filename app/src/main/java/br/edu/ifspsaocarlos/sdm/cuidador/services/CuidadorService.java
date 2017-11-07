package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
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
        CONTADOR_ALARME,
        FOTO_URI,
        INSTRUCAO_URI;

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
                case FOTO_URI:
                    return "fotoUri";
                case INSTRUCAO_URI:
                    return "instrucaoUri";
                default:
                    return "";
            }
        }
    }

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

    public void removeRemedio(String remedioId) {
        repositorio.removeRemedio(preferencias.getIdosoSelecionadoId(), remedioId);
    }

    public void removePrograma(String idPrograma) {
        repositorio.removePrograma(preferencias.getIdosoSelecionadoId(), idPrograma);
    }

    public void registraCuidadorIdoso(final String nome, final String telefone, final String nomeIdoso, final String telefoneIdoso) {
        Contato cuidador = new Contato(nome, telefone);
        Contato idoso = new Contato(nomeIdoso, telefoneIdoso);

        repositorio.salvaContato(cuidador);
        repositorio.salvaContato(idoso);

        registraCuidador(cuidador.getId());
        registraIdoso(idoso.getId());
    }

    private void registraCuidador(String id) {
        registraUsuario(id, Usuario.CUIDADOR);
        repositorio.salvaCuidador(id);
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
     * @param idIdoso id do idoso (identificação)
     */
    public void registraIdoso(String idIdoso) {
        preferencias.setIdosoSelecionadoId(idIdoso);
        repositorio.salvaIdoso(idIdoso);
    }

    public void carregaListas() {
        repositorio.carregaListas(preferencias.getIdosoSelecionadoId());
    }

    public void buscaIdoso(String telefone, CallbackGenerico<Boolean> callback) {
        repositorio.buscaIdoso(telefone, callback);
    }

    public void buscaContato(String telefone, final CallbackGenerico<Contato> callback) {
        repositorio.buscaContato(telefone, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    // carrega contato
                    Contato contato = dataSnapshot.getValue(Contato.class);
                    callback.OnComplete(contato);
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
     */
    public void salvaContato(Contato contato) {
        repositorio.salvaContato(contato);
        repositorio.relacionaContatoIdoso(contato.getId(), preferencias.getIdosoSelecionadoId());
    }

    public void removeContato(String idContato, final Runnable runnable) {
        repositorio.removeContato(idContato, preferencias.getIdosoSelecionadoId(), runnable);
    }

    public void sincronizarRemedios() {
        repositorio.carregaRemedios(preferencias.getIdosoSelecionadoId());
        AlarmeService alarmeService = new AlarmeService(contexto);
        alarmeService.atualizaAlarmesRemedios();
    }

    public String salvaRemedio(Remedio remedio) {
        String id = remedio.getId();
        if(remedio.getId() == null || remedio.getId().isEmpty()){
            id = repositorio.adicionaRemedio(preferencias.getIdosoSelecionadoId(), remedio);
        }else {
            repositorio.atualizaRemedio(preferencias.getIdosoSelecionadoId(), remedio);
        }

        return id;
    }

    public void sincronizarProgramas() {
        repositorio.carregaProgramas(preferencias.getIdosoSelecionadoId());
        AlarmeService alarmeService = new AlarmeService(contexto);
        alarmeService.atualizaAlarmesProgramas();
    }

    public String salvaPrograma(Programa programa) {
        String id = programa.getId();
        if(programa.getId() == null || programa.getId().isEmpty()){
            id = repositorio.adicionaPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }else {
            repositorio.atualizaPrograma(preferencias.getIdosoSelecionadoId(), programa);
        }

        return id;
    }

    public void salvaAudioInstrucao(String fileName, String remedioId, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        CuidadorFirebaseStorage.getInstance().salvaAudioInstrucao(preferencias.getIdosoSelecionadoId(), remedioId, fileName, onSuccessListener);
    }

    public void salvaUriInstrucao(String id, String uri) {
        repositorio.salvaUriInstrucao(preferencias.getIdosoSelecionadoId(), id, uri);
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

    public void salvaAudioChat(String fileName, CallbackSimples callback) {
        CuidadorFirebaseStorage.getInstance().salvaAudioChat(preferencias.getIdosoSelecionadoId(), preferencias.getUsuarioLogadoId(), fileName, callback);
    }

    public void leNovasMensagens(ChildEventListener listener) {
        repositorio.leNovasMensagens(preferencias.getIdosoSelecionadoId(), listener);
    }

    public void efetuaLogout() {
        IMService.unsubscribe(preferencias.getUsuarioLogadoId());
        preferencias.setUsuarioLogadoId(null);
        preferencias.setIdosoSelecionadoId(null);
        FirebaseAuth.getInstance().signOut();
    }

    public UploadTask salvaFoto(NO no, String id, File arquivoFoto) {
        return CuidadorFirebaseStorage.getInstance().salvarArquivo(NO.getNo(no), id, arquivoFoto, contexto);
    }

    public UploadTask salvaFotoPerfil(File arquivoFoto) {
        return salvaFoto(NO.CONTATOS, preferencias.getUsuarioLogadoId(), arquivoFoto);
    }

    public void salvaUri(NO no, String id, String uri) {
        repositorio.salvaUri(no, preferencias.getIdosoSelecionadoId(), id, uri);
    }

    public void salvaUriContato(String id, String uri) {
        repositorio.salvaUriContato(id, uri);
    }
    public void salvaUriPerfil(String uri) {
        repositorio.salvaUriContato(preferencias.getUsuarioLogadoId(), uri);
    }

}