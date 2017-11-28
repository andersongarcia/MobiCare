package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ProgramasRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.RemediosRepository;


/**
 * Classe de serviço para usuário
 *
 * @author Anderson Canale Garcia
 */
public class UsuarioService {
    private static final String TAG = "UsuarioService";


    private final Context contexto;
    private final PreferenciaHelper preferencias;

    public UsuarioService(Context context){
        this.contexto = context;
        this.preferencias = new PreferenciaHelper(context);
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

    public void registraCuidadorIdoso(final String nome, final String telefone, final String nomeIdoso, final String telefoneIdoso) {
        Contato cuidador = new Contato(nome, telefone);
        Contato idoso = new Contato(nomeIdoso, telefoneIdoso);

        ContatosRepository contatosRepository = ContatosRepository.getInstance();
        contatosRepository.salvaContato(preferencias.getIdosoSelecionadoId(), cuidador);
        contatosRepository.salvaContato(preferencias.getIdosoSelecionadoId(), idoso);
        contatosRepository.relacionaCuidadorIdoso(cuidador.getId(), idoso.getId());

        registraUsuario(cuidador.getId(), Usuario.CUIDADOR);
        preferencias.setIdosoSelecionadoId(idoso.getId());
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
        ContatosRepository.getInstance().buscaIdosoDoContato(id, new CallbackGenerico<String>() {
            @Override
            public void OnComplete(String idosoId) {
                preferencias.setIdosoSelecionadoId(idosoId);
            }
        });
    }

    public void sincronizarRemedios() {
        AlarmeService alarmeService = new AlarmeService(contexto);
        alarmeService.cancelaTodos(PreferenciaHelper.ALARMES_REMEDIOS);
        RemediosRepository.getInstance().carregaRemedios(preferencias.getIdosoSelecionadoId(), alarmeService);
    }

    public void sincronizarProgramas() {
        AlarmeService alarmeService = new AlarmeService(contexto);
        alarmeService.cancelaTodos(PreferenciaHelper.ALARMES_PROGRAMAS);
        ProgramasRepository.getInstance().carregaProgramas(preferencias.getIdosoSelecionadoId(), alarmeService);
    }

    public void salvaAudioInstrucao(String fileName, String remedioId, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        FirebaseStorageService.getInstance().salvaAudioInstrucao(preferencias.getIdosoSelecionadoId(), remedioId, fileName, onSuccessListener);
    }

    public void carregaArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        FirebaseStorageService.getInstance().carregaArquivo(uri, localFile, successListener, failureListener);
    }

    public String obterIdLogado() {
        return preferencias.getUsuarioLogadoId();
    }

    public String obterPerfilLogado() {
        return preferencias.getUsuarioLogadoPerfil();
    }

    public void salvaAudioChat(String fileName) {
        FirebaseStorageService.getInstance().salvaAudioChat(preferencias.getIdosoSelecionadoId(), preferencias.getUsuarioLogadoId(), fileName);
    }

    public void efetuaLogout() {
        IMService.unsubscribe(preferencias.getUsuarioLogadoId());
        preferencias.setUsuarioLogadoId(null);
        preferencias.setIdosoSelecionadoId(null);
        FirebaseAuth.getInstance().signOut();
    }

    public UploadTask salvaFoto(NO no, String id, File arquivoFoto) {
        return FirebaseStorageService.getInstance().salvarArquivo(NO.getNo(no), id, arquivoFoto, contexto);
    }

    public UploadTask salvaFotoPerfil(File arquivoFoto) {
        return salvaFoto(NO.CONTATOS, preferencias.getUsuarioLogadoId(), arquivoFoto);
    }

}