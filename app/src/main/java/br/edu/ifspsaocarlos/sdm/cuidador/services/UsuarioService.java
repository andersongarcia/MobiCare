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

    /**
     * Verifica nas preferências se tem informações do usuário logado
     * @return verdadeiro se tiver informações do usuário logado, falso caso contrário.
     */
    public boolean verificaUsuarioLogado() {
        String usuarioLogadoId = preferencias.getUsuarioLogadoId();

        return (usuarioLogadoId != null && !usuarioLogadoId.isEmpty());
    }

    /**
     * Verifica se já possui idoso selecionado nas preferências
     * @return verdadeiro se possui informações do idoso selecionado, falso caso contrário.
     */
    public boolean verificaIdosoSelecionado() {
        String idosoSelecionadoId = preferencias.getIdosoSelecionadoId();

        return (idosoSelecionadoId != null && !idosoSelecionadoId.isEmpty());
    }

    /**
     * Salva dados do cuidador e do idoso como contatos na base.
     * Vincula cuidador com o idoso.
     * Salva nas preferências as informações do cuidador como usuário e do idoso como idoso selecionado.
     * @param nome Nome do cuidador
     * @param telefone Telefone do cuidador
     * @param nomeIdoso Nome do idoso
     * @param telefoneIdoso Telefone do idoso
     */
    public void registraCuidadorIdoso(final String nome, final String telefone, final String nomeIdoso, final String telefoneIdoso) {
        Contato cuidador = new Contato(nome, telefone);
        Contato idoso = new Contato(nomeIdoso, telefoneIdoso);

        ContatosRepository contatosRepository = ContatosRepository.getInstance();
        contatosRepository.salvaContato(preferencias.getIdosoSelecionadoId(), cuidador, false);
        contatosRepository.salvaContato(preferencias.getIdosoSelecionadoId(), idoso, false);
        contatosRepository.relacionaCuidadorIdoso(cuidador.getId(), idoso.getId());

        registraUsuario(cuidador.getId(), Usuario.CUIDADOR);
        preferencias.setIdosoSelecionadoId(idoso.getId());
    }

    /**
     * Registra nas preferências o id e perfil do usuário logado
     * @param id Id do usuário
     * @param perfil Perfil do usuário
     */
    public void registraUsuario(String id, String perfil) {
        preferencias.setUsuarioLogadoId(id);
        preferencias.setUsuarioLogadoPerfil(perfil);
    }

    /**
     * Registra idoso nas preferências como usuário e idoso selecionado
     * @param id Id do idoso
     */
    public void registraUsuarioIdoso(String id) {
        registraUsuario(id, Usuario.IDOSO);
        preferencias.setIdosoSelecionadoId(id);
    }

    /**
     * Registra um contato como usuário logado e busca o idoso relacionado
     * @param id
     */
    public void registraUsuarioContato(String id) {
        registraUsuario(id, Usuario.CONTATO);
        ContatosRepository.getInstance().buscaIdosoDoContato(id, new CallbackGenerico<String>() {
            @Override
            public void OnComplete(String idosoId) {
                preferencias.setIdosoSelecionadoId(idosoId);
            }
        });
    }

    /**
     * Sincroniza informações dos remédios do idoso
     */
    public void sincronizarRemedios() {
        AlarmeService alarmeService = null;
        // Se perfil do usuário for do idoso, deve redefinir alarmes
        if(preferencias.getUsuarioLogadoPerfil().equals(Usuario.IDOSO)){
            // Cria instância do serviço de alarmes
            alarmeService = new AlarmeService(contexto);
            alarmeService.cancelaTodos(PreferenciaHelper.ALARMES_REMEDIOS);
        }
        // Carrega remédios cadastrados para o idoso
        // Se a chamada é feita com o alarmService nulo, não redefine alarmes
        RemediosRepository.getInstance().carregaRemedios(preferencias.getIdosoSelecionadoId(), alarmeService);
    }

    /**
     * Sincroniza informações dos programas favoritos do idoso
     */
    public void sincronizarProgramas() {
        AlarmeService alarmeService = null;
        // Se perfil do usuário for do idoso, deve redefinir alarmes
        if(preferencias.getUsuarioLogadoPerfil().equals(Usuario.IDOSO)){
            // Cria instância do serviço de alarmes
            alarmeService = new AlarmeService(contexto);
            alarmeService.cancelaTodos(PreferenciaHelper.ALARMES_PROGRAMAS);
        }
        // Carrega os programas cadastrados para o idoso
        // Se a chamada é feita com o alarmService nulo, não redefine alarmes
        ProgramasRepository.getInstance().carregaProgramas(preferencias.getIdosoSelecionadoId(), alarmeService);
    }

    /**
     * Salva arquivo com áudio da instrução de medicação no Storage
     * @param fileName Nome do arquivo que contém o áudio
     * @param remedioId Id do remédio da instrução
     * @param onSuccessListener Código a ser executado se arquivo for salvo com sucesso no Storage
     */
    public void salvaAudioInstrucao(String fileName, String remedioId, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        FirebaseStorageService.getInstance().salvaAudioInstrucao(preferencias.getIdosoSelecionadoId(), remedioId, fileName, onSuccessListener);
    }

    /**
     * Carrega arquivo do Storage
     * @param uri Endereço do arquivo
     * @param localFile Arquivo local onde deve ser carregado
     * @param successListener Código a ser executado em caso de sucesso
     * @param failureListener Código a ser executado em caso de falha
     */
    public void carregaArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        FirebaseStorageService.getInstance().carregaArquivo(uri, localFile, successListener, failureListener);
    }

    /**
     * @return Id do usuário logado
     */
    public String obterIdLogado() {
        return preferencias.getUsuarioLogadoId();
    }

    /**
     * @return Perfil do usuário logado
     */
    public String obterPerfilLogado() {
        return preferencias.getUsuarioLogadoPerfil();
    }

    /**
     * Salva arquivo de áudio de mensagem
     * @param fileName Nome do arquivo que contém áudio da mensagem
     */
    public void salvaAudioChat(String fileName) {
        FirebaseStorageService.getInstance().salvaAudioChat(preferencias.getIdosoSelecionadoId(), preferencias.getUsuarioLogadoId(), fileName);
    }

    // Logout do usuário
    public void efetuaLogout() {
        // Desinscreve usuário dos serviços de notificação do app
        IMService.unsubscribe(preferencias.getUsuarioLogadoId());
        // Apaga informações do usuário das preferências
        preferencias.setUsuarioLogadoId(null);
        // Apaga informações do idoso selecionado das preferências
        preferencias.setIdosoSelecionadoId(null);
        // Retira autenticação do Firebase
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Grava arquivo de foto no Storage
     * @param no Nó de identificação da origem da foto
     * @param id Id da origem da foto
     * @param arquivoFoto Arquivo com a foto
     * @return Tarefa de upload da foto
     */
    public UploadTask salvaFoto(NO no, String id, File arquivoFoto) {
        return FirebaseStorageService.getInstance().salvarArquivo(NO.getNo(no), id, arquivoFoto, contexto);
    }

    /**
     * Grava arquivo de foto de perfil de um contato no Storage
     * @param arquivoFoto Arquivo com a foto
     * @return Tarefa de upload da foto
     */
    public UploadTask salvaFotoPerfil(File arquivoFoto) {
        return salvaFoto(NO.CONTATOS, preferencias.getUsuarioLogadoId(), arquivoFoto);
    }

}