package br.edu.ifspsaocarlos.sdm.cuidador.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;

/**
 * Created by ander on 10/11/2017.
 */

public class FirebaseRepository {
    private static final String TAG = "FirebaseRepository";

    private static FirebaseRepository repository;
    private final DatabaseReference mDatabase;
    private final DatabaseReference contadorAlarmeEndPoint;

    private int contadorAlarme;

    private FirebaseRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference();
        contadorAlarmeEndPoint = mDatabase.child(NO.getNo(NO.CONTADOR_ALARME));
    }

    // Singleton
    public static FirebaseRepository getInstance(){
        if(repository == null){
            repository = new FirebaseRepository();
        }

        return repository;
    }

    protected int incrementaContadorAlarme(String idosoId) {
        contadorAlarme++;
        contadorAlarmeEndPoint.child(idosoId).setValue(contadorAlarme);
        return contadorAlarme;
    }

    public void salvaUri(NO no, String idosoId, String id, String uri) {
        mDatabase.child(NO.getNo(no))
                .child(idosoId).child(id)
                .child(NO.getNo(NO.FOTO_URI))
                .setValue(uri);
    }

    public static HashMap<String, Object> getTimestampNow(){
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        return timestampNow;
    }
}
