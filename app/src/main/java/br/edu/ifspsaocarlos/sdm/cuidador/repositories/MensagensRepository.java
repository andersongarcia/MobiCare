package br.edu.ifspsaocarlos.sdm.cuidador.repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MensagemSet;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;

/**
 * Repositório de acesso a dados das mensagens
 *
 * @author Anderson Canale Garcia
 */public class MensagensRepository extends Observable {
    //region TAG
    private static final String TAG = "MensagensRepository";
    //endregion

    //region Atributos
    private static MensagensRepository repository;
    private final DatabaseReference mensagemEndPoint;
    private final DatabaseReference contatoEndPoint;
    private List<MensagemSet> mensagens;
    private final ArrayList<String> mKeys;
    //endregion

    //region Singleton
    private MensagensRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mensagemEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.MENSAGENS));
        contatoEndPoint = firebaseDatabase.getReference().child(NO.getNo(NO.CONTATOS));
        mensagens = new ArrayList<>();
        mKeys = new ArrayList<String>();
    }

    public static MensagensRepository getInstance(){
        if(repository == null){
            repository = new MensagensRepository();
        }

        return repository;
    }
    //endregion

    // Lista de mensagens
    public List<MensagemSet> getMensagens() {
        return mensagens;
    }

    /**
     * Carrega lista de mensagens enviadas ao idoso
     * @param idosoId Id do idoso
     */
    public void carregaMensagens(String idosoId){
        mensagens.clear();  // limpa lista de mensagens
        mKeys.clear();      // limpa lista de chavez

        DatabaseReference reference = mensagemEndPoint.child(idosoId); // referência ao nó das mensagens do idoso
        // Adiciona listener à referência para alterações nos nós filhos
        reference.addChildEventListener(new ChildEventListener() {
            // Quando um item é adicionado
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, final String previousChildName) {
                // carrega objeto de mensagem
                final Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                final String key = dataSnapshot.getKey();  // carrega chave de identificação

                // busca dados de contato do emissor no nó de contatos
                contatoEndPoint.child(mensagem.getEmissorId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot emissorSnapshot) {
                        // carrega dados do contato do emissor
                        final Contato emissor = emissorSnapshot.getValue(Contato.class);
                        // busca dados de contato do destinatário
                        contatoEndPoint.child(mensagem.getDestinatarioId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot destinatarioSnapshot) {
                                // carrega dados do contato do destinatário
                                Contato destinatario = destinatarioSnapshot.getValue(Contato.class);

                                // instancia conjunto para guardar objetos lidos
                                MensagemSet model = new MensagemSet(mensagem, emissor, destinatario);

                                int i = mKeys.indexOf(key);
                                if (i >= 0)
                                    return;
                                // Insert into the correct location, based on previousChildName
                                if (previousChildName == null) {
                                    mensagens.add(0, model);
                                    mKeys.add(0, key);
                                } else {
                                    int previousIndex = mKeys.indexOf(previousChildName);
                                    int nextIndex = previousIndex + 1;
                                    if (nextIndex == mensagens.size()) {
                                        mensagens.add(model);
                                        mKeys.add(key);
                                    } else {
                                        mensagens.add(nextIndex, model);
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
                        final String key = dataSnapshot.getKey();
                        final Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                        final int index = mKeys.indexOf(key);

                        // busca dados de contato do emissor
                        contatoEndPoint.child(mensagem.getEmissorId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot emissorSnapshot) {
                                final Contato emissor = emissorSnapshot.getValue(Contato.class);
                                // busca dados de contato do destinatário
                                contatoEndPoint.child(mensagem.getDestinatarioId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot destinatarioSnapshot) {
                                        if(index >= 0){
                                            Contato destinatario = destinatarioSnapshot.getValue(Contato.class);

                                            MensagemSet newModel = new MensagemSet(mensagem, emissor, destinatario);

                                            mensagens.set(index, newModel);

                                            setChanged();
                                            notifyObservers();
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

                mKeys.remove(index);
                mensagens.remove(index);

                setChanged();
                notifyObservers();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Listen was cancelled, no more updates will occur");
            }
        });
    }

    /**
     * Salva mensagem para idoso
     * @param idosoId Id do idoso
     * @param mensagem Instãncia de mensagem
     */
    public void salvaMensagem(String idosoId, final Mensagem mensagem) {
        final DatabaseReference reference = mensagemEndPoint.child(idosoId);

        // busca foto do emissor
        contatoEndPoint.child(mensagem.getEmissorId()).child(NO.getNo(NO.FOTO_URI))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){  // foto encontrada
                            mensagem.setFotoUri(String.valueOf(dataSnapshot.getValue()));
                            String key = reference.push().getKey();
                            mensagem.setId(key);
                            reference.child(key).setValue(mensagem)
                                    .addOnFailureListener(new OnFailureListener() {
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

}