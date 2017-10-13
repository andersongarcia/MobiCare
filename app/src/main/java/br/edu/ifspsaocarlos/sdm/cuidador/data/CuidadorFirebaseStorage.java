package br.edu.ifspsaocarlos.sdm.cuidador.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Classe de acesso ao storage Firebase
 *
 * @author Anderson Canale Garcia
 */
public class CuidadorFirebaseStorage {
    private static CuidadorFirebaseStorage storage;
    private final StorageReference idosoEndPoint;

    private CuidadorFirebaseStorage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        idosoEndPoint = storageRef.child("idosos");
    }

    // Singleton
    public static CuidadorFirebaseStorage getInstance(){
        if(storage == null){
            storage = new CuidadorFirebaseStorage();
        }

        return storage;
    }

    public void salvarAudioInstrucao(String idosoId, String medicacaoId, String fileName) {
        Uri uri = Uri.fromFile(new File(fileName));
        UploadTask uploadTask = idosoEndPoint.child(idosoId).child("instrucoes").child(medicacaoId).putFile(uri);

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
            }
        });
    }

    public void carregaInstrucaoURI(String idosoId, String medicacaoId, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        idosoEndPoint.child(idosoId).child("instrucoes").child(medicacaoId).getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void carregaArquivo(Uri uri, File localFile, OnSuccessListener<FileDownloadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
        reference.getFile(localFile).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }
}
