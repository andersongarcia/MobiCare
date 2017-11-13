package br.edu.ifspsaocarlos.sdm.cuidador.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MensagemSet;

/**
 * Created by ander on 11/11/2017.
 */

public class MensagensRepository {
    private static final String TAG = "MensagensRepository";

    private static MensagensRepository repository;
    private final DatabaseReference mensagemEndPoint;
    private final DatabaseReference contatoEndPoint;
    private List<MensagemSet> mensagens;

    private MensagensRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mensagemEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.MENSAGENS));
        contatoEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.CONTATOS));
        mensagens = new ArrayList<>();
    }

    // Singleton
    public static MensagensRepository getInstance(){
        if(repository == null){
            repository = new MensagensRepository();
        }

        return repository;
    }

    public List<MensagemSet> getMensagens() {
        return mensagens;
    }


    /**
     * Carrega lista de mensagens enviadas para o idoso
     * @param idosoId
     * @param callback
     */
    public void carregaMensagens(String idosoId, final CallbackSimples callback) {
        final DatabaseReference reference = mensagemEndPoint.child(idosoId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // inicia com a lista de mensagens vazia
                mensagens.clear();
                final long total = snapshot.getChildrenCount();
                if(total == 0){
                    if(callback != null){
                        callback.OnComplete();
                    }
                }else {
                    final int[] contador = {0};
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        // carrega objeto de mensagem
                        final Mensagem mensagem = postSnapshot.getValue(Mensagem.class);
                        // busca dados de contato do emissor
                        contatoEndPoint.child(mensagem.getEmissorId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot emissorSnapshot) {
                                final Contato emissor = emissorSnapshot.getValue(Contato.class);
                                // busca dados de contato do destinatário
                                contatoEndPoint.child(mensagem.getDestinatarioId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot destinatarioSnapshot) {
                                        Contato destinatario = destinatarioSnapshot.getValue(Contato.class);
                                        // cria Model Set com dados da mensagem e dos contatos
                                        MensagemSet mensagemSet = new MensagemSet(mensagem, emissor, destinatario);
                                        mensagens.add(mensagemSet);

                                        contador[0]++;
                                        if (contador[0] == total) {
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

    public void salvaMensagem(String idosoId, final Mensagem mensagem, final CallbackSimples callback) {
        final DatabaseReference reference = mensagemEndPoint.child(idosoId);
        final String id = reference.push().getKey();
        mensagem.setId(id);

        // busca foto do emissor
        contatoEndPoint.child(mensagem.getEmissorId()).child(NO.getNo(NO.FOTO_URI))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){  // foto encontrada
                            mensagem.setFotoUri(String.valueOf(dataSnapshot.getValue()));
                            reference.child(id).setValue(mensagem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    callback.OnComplete();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, e.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
                    }
                });

    }

    public void leNovasMensagens(String idosoId, ChildEventListener listener) {
        mensagemEndPoint.child(idosoId).addChildEventListener(listener);
    }

}