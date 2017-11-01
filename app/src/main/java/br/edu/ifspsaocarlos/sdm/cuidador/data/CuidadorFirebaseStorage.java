package br.edu.ifspsaocarlos.sdm.cuidador.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Classe de acesso ao storage Firebase
 *
 * @author Anderson Canale Garcia
 */
public class CuidadorFirebaseStorage {

    private static CuidadorFirebaseStorage storage;
    private final StorageReference idosoEndPoint;
    private final StorageReference fotosEndPoint;

    private CuidadorFirebaseStorage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();
        idosoEndPoint = rootRef.child(String.valueOf(CuidadorService.NO.IDOSOS));
        fotosEndPoint = rootRef.child(String.valueOf(CuidadorService.NO.FOTOS));
    }

    // Singleton
    public static CuidadorFirebaseStorage getInstance(){
        if(storage == null){
            storage = new CuidadorFirebaseStorage();
        }

        return storage;
    }

    public void salvarAudioInstrucao(String idosoId, String remedioId, String fileName, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        Uri uri = Uri.fromFile(new File(fileName));
        UploadTask uploadTask = idosoEndPoint.child(idosoId).child(String.valueOf(CuidadorService.NO.INSTRUCOES)).child(remedioId).putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(onSuccessListener);
    }

    public void carregaInstrucaoURI(String idosoId, String remedioId, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        idosoEndPoint.child(idosoId).child(String.valueOf(CuidadorService.NO.INSTRUCOES)).child(remedioId).getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void carregarArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
        reference.getFile(localFile).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void salvarAudioChat(final String idosoId, final String contatoId, final String fileName) {
        Uri uri = Uri.fromFile(new File(fileName));
        UploadTask uploadTask = idosoEndPoint.child(idosoId).child(String.valueOf(CuidadorService.NO.CHAT)).child(contatoId).putFile(uri);

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
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Mensagem mensagem = new Mensagem(contatoId, idosoId, fileName);

                CuidadorFirebaseRepository.getInstance().salvarMensagem(idosoId, mensagem);
            }
        });
    }

    public void carregaFotoURI(String no, String id, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        fotosEndPoint.child(no).child(id).getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void salvarArquivo(String child, String id, File arquivo, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        Uri uri = Uri.fromFile(arquivo);
        UploadTask uploadTask = fotosEndPoint.child(child).child(id).putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(onSuccessListener);
    }
}
