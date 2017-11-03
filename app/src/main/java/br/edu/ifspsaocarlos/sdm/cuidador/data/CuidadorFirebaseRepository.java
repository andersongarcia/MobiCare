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
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Idoso;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
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
    private final DatabaseReference mDatabase;
    private final DatabaseReference cuidadorEndPoint;
    private final DatabaseReference idosoEndPoint;
    private final DatabaseReference contatoEndPoint;
    private final DatabaseReference remedioEndPoint;
    private final DatabaseReference programaEndPoint;
    private final DatabaseReference mensagemEndPoint;

    private List<Idoso> idosos;
    private List<Contato> contatos;
    private List<Remedio> remedios;
    private List<Programa> programas;

    public List<Contato> getContatos() {
        return contatos;
    }

    public List<Remedio> getRemedios() {
        return remedios;
    }

    public List<Programa> getProgramas() { return programas; }

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

        idosos = new ArrayList<>();
        contatos = new ArrayList<>();
        remedios = new ArrayList<>();
        programas = new ArrayList<>();
    }

    // Singleton
    public static CuidadorFirebaseRepository getInstance(){
        if(repository == null){
           repository = new CuidadorFirebaseRepository();
        }

        return repository;
    }

    /**
     * Salva (cria) cuidador na base
     * @param id telefone do cuidador
     */
    public void salvarCuidador(String id) {
        cuidadorEndPoint.child(id).setValue(true);
    }

    public void buscarContatoPeloTelefone(String telefone, ValueEventListener listener){
        contatoEndPoint.orderByChild("telefone").equalTo(telefone).limitToFirst(1).addListenerForSingleValueEvent(listener);
    }

    public void salvarIdoso(String id) {
        idosoEndPoint.child(id).setValue(true);
    }

    public void salvarContato(final Contato contato, final CallbackGenerico<Contato> callback) {
        String id = contato.getId();
        if (id == null || id.isEmpty()) {
            id = contatoEndPoint.push().getKey();
            contato.setId(id);
        }
        contatoEndPoint.child(id).setValue(contato).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.OnComplete(contato);
            }
        });
    }


    public void removerContato(final String contatoId, final String idosoId, final Runnable runnable) {
        contatoEndPoint.child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idosoId).child(CuidadorService.NO.getNo(CuidadorService.NO.CONTATOS)).child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removerContatoDaLista(contatoId);
                        runnable.run();
                    }
                });
            }
        });
    }

    public Remedio adicionarRemedio(String idosoId, Remedio remedio) {
        DatabaseReference reference = remedioEndPoint.child(idosoId);

        String key = reference.push().getKey();
        remedio.setId(key);
        reference.child(key).setValue(remedio);

        return remedio;
    }

    public void atualizarRemedio(String idosoId, Remedio remedio) {
        remedioEndPoint.child(idosoId).child(remedio.getId()).setValue(remedio);
    }

    public void removerRemedio(String idosoId, String remedioId) {
        remedioEndPoint.child(idosoId).child(remedioId).removeValue();
    }

    public Programa adicionarPrograma(String idosoId, Programa programa) {
        DatabaseReference reference = programaEndPoint.child(idosoId);

        String key = reference.push().getKey();
        programa.setId(key);
        reference.child(key).setValue(programa);

        return programa;
    }

    public void atualizarPrograma(String idosoId, Programa programa) {
        programaEndPoint.child(idosoId).child(programa.getId()).setValue(programa);
    }

    public void removerPrograma(String idosoId, String programaId) {
        programaEndPoint.child(idosoId).child(programaId).removeValue();
    }

    public void carregarListas(String idosoId) {
        carregarContatos(idosoId);
        carregarRemedios(idosoId);
        carregarProgramas(idosoId);
    }

    private void carregarRemedios(String idosoId) {
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

    private void carregarContatos(String idosoId) {
        contatos.clear();
        idosoEndPoint.child(idosoId).child(CuidadorService.NO.getNo(CuidadorService.NO.CONTATOS)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    carregarContatoNaLista(postSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    private void carregarContatoNaLista(DataSnapshot dataSnapshot) {
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

    private void removerContatoDaLista(String contatoId) {
        Iterator<Contato> i = contatos.iterator();
        while (i.hasNext()) {
            Contato c = i.next();
            if(c.getId() == contatoId)
                i.remove();
        }
    }

    private void carregarProgramas(String idosoId) {
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

    /**
     * Grava relacionamento ente cuidador e idoso (bidirecional)
     * @param cuidadorId
     * @param idodoId
     */
    public void relacionarCuidadorIdoso(String cuidadorId, String idodoId) {
        cuidadorEndPoint.child(cuidadorId).child(CuidadorService.NO.getNo(CuidadorService.NO.IDOSOS)).child(idodoId).setValue(true);
        idosoEndPoint.child(idodoId).child("cuidadores").child(cuidadorId).setValue(true);
    }

    /**
     * Grava relacionamento ente contato e idoso (bidirecional)
     * @param contatoId
     * @param idodoId
     */
    public void relacionarContatoIdoso(final String contatoId, final String idodoId) {
        contatoEndPoint.child(contatoId).child(CuidadorService.NO.getNo(CuidadorService.NO.IDOSOS)).child(idodoId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idodoId).child(CuidadorService.NO.getNo(CuidadorService.NO.CONTATOS)).child(contatoId).setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                carregarContatos(idodoId);
                            }
                        });
            }
        });
    }

    public void salvarMensagem(String idosoId, Mensagem mensagem) {
        DatabaseReference reference = mensagemEndPoint.child(idosoId);
        String id = reference.push().getKey();
        mensagem.setId(id);
        reference.child(id).setValue(mensagem).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });
    }

    public void lerNovasMensagens(String idosoId, ChildEventListener listener) {
        mensagemEndPoint.child(idosoId).addChildEventListener(listener);
    }

    public void buscarIdosoDoContato(String id, final CallbackGenerico<String> callback) {
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
}
