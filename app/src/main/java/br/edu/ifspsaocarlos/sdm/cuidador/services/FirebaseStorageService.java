package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.MensagensRepository;

/**
 * Classe de acesso ao storage Firebase
 *
 * @author Anderson Canale Garcia
 */
public class FirebaseStorageService {
    //region TAGs
    private static final String TAG = "FirebaseStorage";
    //endregion

    //region Atributos
    private static FirebaseStorageService storage;
    private final StorageReference idosoEndPoint;
    private final StorageReference fotosEndPoint;
    //endregion

    //region Singleton
    private FirebaseStorageService(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();
        idosoEndPoint = rootRef.child(NO.getNo(NO.IDOSOS));
        fotosEndPoint = rootRef.child(NO.getNo(NO.FOTOS));
    }

    public static FirebaseStorageService getInstance(){
        if(storage == null){
            storage = new FirebaseStorageService();
        }

        return storage;
    }
    //endregion

    /**
     * Salva arquivo de áudio com instrução para remédio no Storage
     * @param idosoId Id do idoso
     * @param remedioId Id do remédio
     * @param fileName Nome do arquivo de áudio
     * @param onSuccessListener Código a ser executado em caso de sucesso
     */
    public void salvaAudioInstrucao(String idosoId, String remedioId, String fileName,
                                    OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        Uri uri = Uri.fromFile(new File(fileName));
        // Cria tarefa de upload no nó do remédio
        UploadTask uploadTask = idosoEndPoint.child(idosoId)
                .child(NO.getNo(NO.INSTRUCOES)).child(remedioId).putFile(uri);

        // Registra observers para escutar quando o upload estiver concluído ou se falhar
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(onSuccessListener);
    }

    public void carregaInstrucaoURI(String idosoId, String remedioId, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        idosoEndPoint.child(idosoId).child(NO.getNo(NO.INSTRUCOES)).child(remedioId).getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void carregaArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
        reference.getFile(localFile).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void salvaAudioChat(final String idosoId, final String contatoId, final String fileName) {
        Uri uri = Uri.fromFile(new File(fileName));
        UploadTask uploadTask = idosoEndPoint.child(idosoId).child(NO.getNo(NO.CHAT)).child(contatoId).putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                ContatosRepository.getInstance().buscaContato(contatoId, new CallbackGenerico<Contato>() {
                    @Override
                    public void OnComplete(Contato contato) {
                        Mensagem mensagem = new Mensagem(contatoId, idosoId, downloadUrl.toString());
                        mensagem.setTitulo(String.format("Mensagem de %s", contato.getNome()));

                        MensagensRepository.getInstance().salvaMensagem(idosoId, mensagem);
                    }
                });

            }
        });
    }

    public void carregaFotoURI(String no, String id, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        if(id!= null){
            fotosEndPoint.child(no).child(id).getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
        }
    }

    public UploadTask salvarArquivo(String child, String id, File arquivo, final Context contexto) {
        Uri uri = Uri.fromFile(arquivo);
        UploadTask uploadTask = fotosEndPoint.child(child).child(id).putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(contexto, R.string.msg_erro_salva_foto, Toast.LENGTH_LONG).show();
                Log.d(TAG, e.getMessage());
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(contexto, R.string.msg_sucesso_salva_foto, Toast.LENGTH_LONG).show();
                Log.d(TAG, contexto.getString(R.string.msg_sucesso_salva_foto));
            }
        });

        return uploadTask;
    }
}
