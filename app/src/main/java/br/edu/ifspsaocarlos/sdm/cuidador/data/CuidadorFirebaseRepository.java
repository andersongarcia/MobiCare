package br.edu.ifspsaocarlos.sdm.cuidador.data;

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
import java.util.Iterator;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackGenerico;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Idoso;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MensagemSet;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Remedio;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;

/**
 * Classe de acesso a dados no padrão repository com conexão ao Firebase
 *
 * @author Anderson Canale Garcia
 */

public class CuidadorFirebaseRepository {
    private static final String TAG = "Repository";

    private static CuidadorFirebaseRepository repository;

    //region Listas
    private List<Idoso> idosos;
    private List<Contato> contatos;
    private List<Remedio> remedios;
    private List<Programa> programas;
    //endregion

    private int contadorAlarme;

    //region Referencias

    private final DatabaseReference mDatabase;
    private final DatabaseReference cuidadorEndPoint;
    private final DatabaseReference idosoEndPoint;
    private final DatabaseReference contatoEndPoint;
    private final DatabaseReference remedioEndPoint;
    private final DatabaseReference programaEndPoint;
    private final DatabaseReference mensagemEndPoint;
    private final DatabaseReference contadorAlarmeEndPoint;
    private List<MensagemSet> mensagens;

    //endregion

    //region Getters e Setters

    public List<Contato> getContatos() {
        return contatos;
    }

    public List<Remedio> getRemedios() {
        return remedios;
    }

    public List<Programa> getProgramas() { return programas; }

    public List<MensagemSet> getMensagens() { return mensagens; }

    //endregion

    private CuidadorFirebaseRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        mDatabase =  firebaseDatabase.getReference();
        cuidadorEndPoint = mDatabase.child(CuidadorService.NO.getNo(CuidadorService.NO.CUIDADORES));
        idosoEndPoint = mDatabase.child(CuidadorService.NO.getNo(CuidadorService.NO.IDOSOS));
        contatoEndPoint = mDatabase.child(CuidadorService.NO.getNo(CuidadorService.NO.CONTATOS));
        remedioEndPoint = mDatabase.child(CuidadorService.NO.getNo(CuidadorService.NO.REMEDIOS));
        programaEndPoint = mDatabase.child(CuidadorService.NO.getNo(CuidadorService.NO.PROGRAMAS));
        mensagemEndPoint = mDatabase.child(CuidadorService.NO.getNo(CuidadorService.NO.MENSAGENS));
        contadorAlarmeEndPoint = mDatabase.child(CuidadorService.NO.getNo(CuidadorService.NO.CONTADOR_ALARME));

        idosos = new ArrayList<>();
        contatos = new ArrayList<>();
        remedios = new ArrayList<>();
        programas = new ArrayList<>();
        mensagens = new ArrayList<>();
    }

    // Singleton
    public static CuidadorFirebaseRepository getInstance(){
        if(repository == null){
           repository = new CuidadorFirebaseRepository();
        }

        return repository;
    }

    public void carregaListas(String idosoId) {
        carregaContatos(idosoId);
        carregaRemedios(idosoId);
        carregaProgramas(idosoId);
        carregaMensagens(idosoId);
        carregaContadorAlarme(idosoId);
    }

    //region Cuidadores
    /**
     * Salva (cria) cuidador na base
     * @param id telefone do cuidador
     */
    public void salvaCuidador(String id) {
        cuidadorEndPoint.child(id).setValue(true);
    }
    //endregion

    //region Idosos

    public void salvaIdoso(String id) {
        idosoEndPoint.child(id).setValue(true);
    }

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
        cuidadorEndPoint.child(cuidadorId).child(CuidadorService.NO.getNo(CuidadorService.NO.IDOSOS)).child(idodoId).setValue(true);
        idosoEndPoint.child(idodoId).child("cuidadores").child(cuidadorId).setValue(true);
    }

    /**
     * Grava relacionamento ente contato e idoso (bidirecional)
     * @param contatoId
     * @param idodoId
     */
    public void relacionaContatoIdoso(final String contatoId, final String idodoId) {
        contatoEndPoint.child(contatoId).child(CuidadorService.NO.getNo(CuidadorService.NO.IDOSOS)).child(idodoId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idodoId).child(CuidadorService.NO.getNo(CuidadorService.NO.CONTATOS)).child(contatoId).setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                carregaContatos(idodoId);
                            }
                        });
            }
        });
    }

    public void buscaIdosoDoContato(String id, final CallbackGenerico<String> callback) {
        contatoEndPoint.child(id).child(CuidadorService.NO.getNo(CuidadorService.NO.IDOSOS)).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
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

    //endregion

    //region Remédios

    public String adicionaRemedio(String idosoId, Remedio remedio) {
        DatabaseReference reference = remedioEndPoint.child(idosoId);

        // cria identificador
        String key = reference.push().getKey();
        remedio.setId(key);

        // pega código para alarme
        remedio.setCodigoAlarme(incrementaContadorAlarme(idosoId));

        reference.child(key).setValue(remedio);

        remedios.add(remedio);

        return key;
    }

    public void atualizaRemedio(String idosoId, Remedio remedio) {
        remedioEndPoint.child(idosoId).child(remedio.getId()).setValue(remedio);
    }

    public void removeRemedio(String idosoId, String remedioId) {
        remedioEndPoint.child(idosoId).child(remedioId).removeValue();
        removeRemedioDaLista(remedioId);
    }

    public void carregaRemedios(String idosoId) {
        remedioEndPoint.child(idosoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                remedios.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    Remedio remedio = postSnapshot.getValue(Remedio.class);
                    remedios.add(remedio);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    private void removeRemedioDaLista(String remedioId) {
        Iterator<Remedio> i = remedios.iterator();
        while (i.hasNext()) {
            Remedio remedio = i.next();
            if(remedio.getId() == remedioId)
                i.remove();
        }
    }

    //endregion

    //region Contatos

    public void buscaContato(String id, ValueEventListener listener){
        contatoEndPoint.child(id).addListenerForSingleValueEvent(listener);
    }

    public void salvaContato(final Contato contato) {
        contatoEndPoint.child(contato.getTelefone()).setValue(contato);
    }

    public void removeContato(final String contatoId, final String idosoId, final Runnable runnable) {
        contatoEndPoint.child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idosoId).child(CuidadorService.NO.getNo(CuidadorService.NO.CONTATOS)).child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeContatoDaLista(contatoId);
                        runnable.run();
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

    private void carregaContatos(String idosoId) {
        contatos.clear();
        idosoEndPoint.child(idosoId).child(CuidadorService.NO.getNo(CuidadorService.NO.CONTATOS)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    carregaContatoNaLista(postSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    private void carregaContatoNaLista(DataSnapshot dataSnapshot) {
        String contatoId = dataSnapshot.getKey();
        contatoEndPoint.child(contatoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contato contato = dataSnapshot.getValue(Contato.class);
                contatos.add(contato);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //endregion

    //region Programas

    public String adicionaPrograma(String idosoId, Programa programa) {
        DatabaseReference reference = programaEndPoint.child(idosoId);

        String key = reference.push().getKey();
        programa.setId(key);

        // pega código para alarme
        programa.setCodigoAlarme(incrementaContadorAlarme(idosoId));

        reference.child(key).setValue(programa);

        programas.add(programa);

        return key;
    }

    public void atualizaPrograma(String idosoId, Programa programa) {
        programaEndPoint.child(idosoId).child(programa.getId()).setValue(programa);
    }

    public void removePrograma(String idosoId, String programaId) {
        programaEndPoint.child(idosoId).child(programaId).removeValue();
        removeProgramaDaLista(programaId);
    }

    public void carregaProgramas(String idosoId) {
        programaEndPoint.child(idosoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                programas.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    Programa programa = postSnapshot.getValue(Programa.class);
                    programas.add(programa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

    //endregion

    //region Mensagens

    /**
     * Carrega lista de mensagens enviadas para o idoso
     * @param idosoId
     */
    private void carregaMensagens(String idosoId) {
        mensagemEndPoint.child(idosoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // inicia com a lista de mensagens vazia
                mensagens.clear();
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    public void salvaMensagem(String idosoId, final Mensagem mensagem, final CallbackSimples callback) {
        final DatabaseReference reference = mensagemEndPoint.child(idosoId);
        final String id = reference.push().getKey();
        mensagem.setId(id);

        // busca foto do emissor
        contatoEndPoint.child(mensagem.getEmissorId()).child(CuidadorService.NO.getNo(CuidadorService.NO.FOTO_URI))
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

    //endregion

    //region Contador Alarme

    private void carregaContadorAlarme(String idosoId) {
        contadorAlarmeEndPoint.child(idosoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){   // contador já iniciado
                    try {
                        // pega contagem atual
                        contadorAlarme = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                    }catch (NumberFormatException ex){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            contadorAlarme = Integer.valueOf(String.valueOf(snapshot.getValue()));
                        }
                    }
                }else {     // contador não iniciado
                    // inicia contador em 0
                    contadorAlarme = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private int incrementaContadorAlarme(String idosoId) {
        contadorAlarme++;
        contadorAlarmeEndPoint.child(idosoId).setValue(contadorAlarme);
        return contadorAlarme;
    }

    public void salvaUri(CuidadorService.NO no, String idosoId, String id, String uri) {
        mDatabase.child(CuidadorService.NO.getNo(no))
                .child(idosoId).child(id)
                .child(CuidadorService.NO.getNo(CuidadorService.NO.FOTO_URI))
                .setValue(uri);
    }

    public void salvaUriContato(String id, String uri) {
        contatoEndPoint.child(id).child(CuidadorService.NO.getNo(CuidadorService.NO.FOTO_URI)).setValue(uri);
    }

    public void salvaUriInstrucao(String idosoId, String id, String uri) {
        remedioEndPoint.child(idosoId).child(id).child(CuidadorService.NO.getNo(CuidadorService.NO.INSTRUCAO_URI)).setValue(uri);
    }


    //endregion

}
