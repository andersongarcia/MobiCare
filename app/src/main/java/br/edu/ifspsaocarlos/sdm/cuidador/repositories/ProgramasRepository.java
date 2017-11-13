package br.edu.ifspsaocarlos.sdm.cuidador.repositories;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.services.AlarmeService;

/**
 * Created by ander on 11/11/2017.
 */

public class ProgramasRepository {
    private static final String TAG = "RemediosRepository";

    private static ProgramasRepository repository;
    private final DatabaseReference programaEndPoint;
    private List<Programa> programas;

    private ProgramasRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        programaEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.PROGRAMAS));
        programas = new ArrayList<>();
    }

    // Singleton
    public static ProgramasRepository getInstance(){
        if(repository == null){
            repository = new ProgramasRepository();
        }

        return repository;
    }

    public List<Programa> getProgramas() {
        return programas;
    }


    private String adicionaPrograma(String idosoId, Programa programa) {
        FirebaseRepository firebaseRepository = FirebaseRepository.getInstance();
        DatabaseReference reference = programaEndPoint.child(idosoId);

        String key = reference.push().getKey();
        programa.setId(key);

        // pega código para alarme
        programa.setCodigoAlarme(firebaseRepository.incrementaContadorAlarme(idosoId));

        reference.child(key).setValue(programa);

        programas.add(programa);

        return key;
    }

    private void atualizaPrograma(String idosoId, Programa programa) {
        programaEndPoint.child(idosoId).child(programa.getId()).setValue(programa);
    }

    public void removePrograma(String idosoId, String programaId) {
        programaEndPoint.child(idosoId).child(programaId).removeValue();
        removeProgramaDaLista(programaId);
    }

    public void carregaProgramas(String idosoId, final AlarmeService alarmeService, final CallbackSimples callback) {
        final DatabaseReference reference = programaEndPoint.child(idosoId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                programas.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    Programa programa = postSnapshot.getValue(Programa.class);
                    programas.add(programa);
                    if(alarmeService != null){
                        alarmeService.atualizaAlarmePrograma(programa);
                    }
                }
                if(callback != null){
                    callback.OnComplete();
                }
                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                reference.removeEventListener(this);
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    private void removeProgramaDaLista(String programaId) {
        Iterator<Programa> i = programas.iterator();
        while (i.hasNext()) {
            Programa programa = i.next();
            if(programa.getId() == programaId)
                i.remove();
        }
    }


    public String salvaPrograma(String idosoId, Programa programa) {
        String id = programa.getId();
        if(programa.getId() == null || programa.getId().isEmpty()){
            id = adicionaPrograma(idosoId, programa);
        }else {
            atualizaPrograma(idosoId, programa);
        }

        return id;
    }
}
