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

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Idoso;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Mensagem;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;

/**
 * Classe de acesso a dados no padrão repository com conexão ao Firebase
 *
 * @author Anderson Canale Garcia
 */

public class CuidadorFirebaseRepository {
    private static CuidadorFirebaseRepository repository;
    private final DatabaseReference mDatabase;
    private final DatabaseReference cuidadorEndPoint;
    private final DatabaseReference idosoEndPoint;
    private final DatabaseReference contatoEndPoint;
    private final DatabaseReference medicacaoEndPoint;
    private final DatabaseReference programaEndPoint;
    private final DatabaseReference mensagemEndPoint;

    private List<Idoso> idosos;
    private List<Contato> contatos;
    private List<Medicacao> medicacoes;
    private List<Programa> programas;

    public List<Contato> getContatos() {
        return contatos;
    }

    public List<Medicacao> getMedicacoes() {
        return medicacoes;
    }

    public List<Programa> getProgramas() {
        return programas;
    }

    private CuidadorFirebaseRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        mDatabase =  firebaseDatabase.getReference();
        cuidadorEndPoint = mDatabase.child("cuidadores");
        idosoEndPoint = mDatabase.child("idosos");
        contatoEndPoint = mDatabase.child("contatos");
        medicacaoEndPoint = mDatabase.child("medicacoes");
        programaEndPoint = mDatabase.child("programas");
        mensagemEndPoint = mDatabase.child("mensagens");

        idosos = new ArrayList<>();
        contatos = new ArrayList<>();
        medicacoes = new ArrayList<>();
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

    /**
     * Exclui cuidador da base
     * @param id
     */
    public void excluirUsuario(String id) {
        cuidadorEndPoint.child(id).removeValue();
        // TODO: remover cuidador dos relacionamentos dos idosos
    }

    public void buscarContatoPeloTelefone(String telefone, ValueEventListener listener){
        contatoEndPoint.orderByChild("telefone").equalTo(telefone).addListenerForSingleValueEvent(listener);
    }

    public void salvarIdoso(String telefone) {
        idosoEndPoint.child(telefone).setValue(true);
    }

    public String salvarContato(Contato contato, OnSuccessListener listener) {
        String id = contato.getId();
        if(id == null || id.isEmpty()){
            id = contatoEndPoint.push().getKey();
            contato.setId(id);
        }
        contatoEndPoint.child(id).setValue(contato).addOnSuccessListener(listener);

        return id;
    }

    public void removerContato(final String contatoId, final String idosoId, final Runnable runnable) {
        contatoEndPoint.child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idosoId).child("contatos").child(contatoId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removerContatoDaLista(contatoId);
                        runnable.run();
                    }
                });
            }
        });
    }

    public Medicacao adicionarMedicacao(String idosoId, Medicacao medicacao) {
        DatabaseReference reference = medicacaoEndPoint.child(idosoId);

        String key = reference.push().getKey();
        medicacao.setId(key);
        reference.child(key).setValue(medicacao);

        return medicacao;
    }

    public void atualizarMedicacao(String idosoId, Medicacao medicacao) {
        medicacaoEndPoint.child(idosoId).child(medicacao.getId()).setValue(medicacao);
    }

    public void removerMedicacao(String idosoId, String medicacaoId) {
        medicacaoEndPoint.child(idosoId).child(medicacaoId).removeValue();
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
        carregarContatos(idosoId, new Runnable(){
            @Override
            public void run() {

            }
        });
        carregarMedicacoes(idosoId);
        carregarProgramas(idosoId);
    }

    private void carregarMedicacoes(String idosoId) {
        medicacaoEndPoint.child(idosoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                medicacoes.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    Medicacao medicacao = postSnapshot.getValue(Medicacao.class);
                    medicacoes.add(medicacao);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    private void carregarContatos(String idosoId, final Runnable runnable) {
        contatos.clear();
        idosoEndPoint.child(idosoId).child("contatos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    carregarContatoNaLista(postSnapshot);
                    runnable.run();
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
        cuidadorEndPoint.child(cuidadorId).child("idosos").child(idodoId).setValue(true);
        idosoEndPoint.child(idodoId).child("cuidadores").child(cuidadorId).setValue(true);
    }

    /**
     * Grava relacionamento ente contato e idoso (bidirecional)
     * @param contatoId
     * @param idodoId
     * @param runnable
     */
    public void relacionarContatoIdoso(final String contatoId, final String idodoId, final Runnable runnable) {
        contatoEndPoint.child(contatoId).child("idosos").child(idodoId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                idosoEndPoint.child(idodoId).child("contatos").child(contatoId).setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                carregarContatos(idodoId, runnable);
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
                Log.d("FIREBASE", e.getMessage());
            }
        });
    }

    public void lerNovasMensagens(String idosoId, ChildEventListener listener) {
        mensagemEndPoint.child(idosoId).addChildEventListener(listener);
    }
}
