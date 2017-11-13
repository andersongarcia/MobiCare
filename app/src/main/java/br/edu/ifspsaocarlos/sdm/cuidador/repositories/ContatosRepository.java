package br.edu.ifspsaocarlos.sdm.cuidador.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;

/**
 * Created by ander on 11/11/2017.
 */

public class ContatosRepository extends Observable {
    private static final String TAG = "ContatosRepository";

    private static ContatosRepository repository;
    private final DatabaseReference contatoEndPoint;
    private final DatabaseReference cuidadorEndPoint;
    private final DatabaseReference idosoEndPoint;
    private List<Contato> contatos;

    private ContatosRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        contatoEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.CONTATOS));
        cuidadorEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.CUIDADORES));
        idosoEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.IDOSOS));
        contatos = new ArrayList<>();
    }

    // Singleton
    public static ContatosRepository getInstance(){
        if(repository == null){
            repository = new ContatosRepository();
        }

        return repository;
    }

    public List<Contato> getContatos() {
        return contatos;
    }

    public void buscaContato(String id, final CallbackGenerico<Contato> callback){
        contatoEndPoint.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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
            }
        });
    }

    public Task<Void> salvaContato(final String idosoId, final Contato contato) {
        return contatoEndPoint.child(contato.getId()).setValue(contato).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                relacionaContatoIdoso(contato.getId(), idosoId);
            }
        });
    }

    public void removeContato(final String contatoId, final String idosoId) {
        contatoEndPoint.child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idosoId).child(NO.getNo(NO.CONTATOS)).child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeContatoDaLista(contatoId);
                    }
                });
            }
        });
    }

    private void removeContatoDaLista(String contatoId) {
        Iterator<Contato> i = contatos.iterator();
        while (i.hasNext()) {
            Contato c = i.next();
            if(c.getId() == contatoId)
                i.remove();
        }
    }

    public void carregaContatos(String idosoId, final CallbackSimples callback) {
        contatos.clear();
        final DatabaseReference reference = idosoEndPoint.child(idosoId).child(NO.getNo(NO.CONTATOS));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final long total = snapshot.getChildrenCount();
                if(total == 0){
                    if(callback != null){
                        callback.OnComplete();
                    }
                }else {
                    final int[] contador = {0};
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String contatoId = postSnapshot.getKey();
                        contatoEndPoint.child(contatoId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Contato contato = dataSnapshot.getValue(Contato.class);
                                contatos.add(contato);

                                // verifica se carregou todos
                                contador[0]++;
                                if (contador[0] == total) { // se carregou tudo, chama callback
                                    if (callback != null) {
                                        callback.OnComplete();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
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

    public Contato obterContato(String contatoId) {
        for (Contato contato : contatos) {
            if(contato.getId().equals(contatoId))
                return contato;
        }

        return null;
    }

    //region Idosos

    public void buscaIdoso(String telefone, final CallbackGenerico<Boolean> callback) {
        idosoEndPoint.child(telefone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.OnComplete(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }

    /**
     * Grava relacionamento ente cuidador e idoso (bidirecional)
     * @param cuidadorId
     * @param idodoId
     */
    public void relacionaCuidadorIdoso(String cuidadorId, String idodoId) {
        cuidadorEndPoint.child(cuidadorId).child(NO.getNo(NO.IDOSOS)).child(idodoId).setValue(true);
        idosoEndPoint.child(idodoId).child(NO.getNo(NO.CUIDADORES)).child(cuidadorId).setValue(true);
    }

    /**
     * Grava relacionamento ente contato e idoso (bidirecional)
     * @param contatoId
     * @param idodoId
     */
    public void relacionaContatoIdoso(final String contatoId, final String idodoId) {
        contatoEndPoint.child(contatoId).child(NO.getNo(NO.IDOSOS)).child(idodoId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idodoId).child(NO.getNo(NO.CONTATOS)).child(contatoId).setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                carregaContatos(idodoId, new CallbackSimples() {
                                    @Override
                                    public void OnComplete() {
                                    }
                                });
                            }
                        });
            }
        });
    }

    public void buscaIdosoDoContato(String id, final CallbackGenerico<String> callback) {
        contatoEndPoint.child(id).child(NO.getNo(NO.IDOSOS)).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    // carrega contato
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // só traz 1 resultado
                        String idosoId = snapshot.getKey();
                        callback.OnComplete(idosoId);
                    }
                }else {
                    callback.OnComplete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Task<Void> salvaUriContato(String id, String uri) {
        return contatoEndPoint.child(id).child(NO.getNo(NO.FOTO_URI)).setValue(uri);
    }

    //endregion
}
