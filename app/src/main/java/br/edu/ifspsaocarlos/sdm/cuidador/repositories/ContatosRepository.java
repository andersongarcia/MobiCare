package br.edu.ifspsaocarlos.sdm.cuidador.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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
    private final ArrayList<String> mKeys;
    private List<Contato> contatos;
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, final String previousChildName) {
            contatoEndPoint.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contato model = dataSnapshot.getValue(Contato.class);
                    String key = dataSnapshot.getKey();

                    int i = mKeys.indexOf(key);
                    if (i >= 0)
                        return;
                    // Insert into the correct location, based on previousChildName
                    if (previousChildName == null) {
                        contatos.add(0, model);
                        mKeys.add(0, key);
                    } else {
                        int previousIndex = mKeys.indexOf(previousChildName);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == contatos.size()) {
                            contatos.add(model);
                            mKeys.add(key);
                        } else {
                            contatos.add(nextIndex, model);
                            mKeys.add(nextIndex, key);
                        }
                    }

                    setChanged();
                    notifyObservers();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, final String previousChildName) {
            contatoEndPoint.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // One of the mModels changed. Replace it in our list and name mapping
                    String key = dataSnapshot.getKey();
                    Contato newModel = dataSnapshot.getValue(Contato.class);
                    int index = mKeys.indexOf(key);

                    contatos.set(index, newModel);

                    setChanged();
                    notifyObservers();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

            // A model was removed from the list. Remove it from our list and the name mapping
            String key = dataSnapshot.getKey();
            int index = mKeys.indexOf(key);

            if(index >= 0){
                mKeys.remove(index);
                contatos.remove(index);

                setChanged();
                notifyObservers();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Listen was cancelled, no more updates will occur");
        }
    };


    private ContatosRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        contatoEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.CONTATOS));
        cuidadorEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.CUIDADORES));
        idosoEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.IDOSOS));
        contatos = new ArrayList<>();
        mKeys = new ArrayList<String>();
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

    public void removeContato(final String contatoId, final String idosoId, final CallbackSimples callback) {
        idosoEndPoint.child(idosoId).child(NO.getNo(NO.CONTATOS)).child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                contatoEndPoint.child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeContatoDaLista(contatoId);
                        if(callback != null)
                            callback.OnComplete();
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

    public void carregarContatosIdoso(String idosoId){
        contatos.clear();
        mKeys.clear();
        DatabaseReference reference = idosoEndPoint.child(idosoId).child(NO.getNo(NO.CONTATOS));
        reference.addChildEventListener(childEventListener);
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
                idosoEndPoint.child(idodoId).child(NO.getNo(NO.CONTATOS)).child(contatoId).setValue(true);
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

    public void removeListener(String idosoId){
        DatabaseReference reference = idosoEndPoint.child(idosoId).child(NO.getNo(NO.CONTATOS));
        reference.removeEventListener(childEventListener);
    }

    //endregion
}
