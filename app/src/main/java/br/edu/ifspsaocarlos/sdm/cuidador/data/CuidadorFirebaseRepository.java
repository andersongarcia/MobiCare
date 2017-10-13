package br.edu.ifspsaocarlos.sdm.cuidador.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Idoso;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;

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

    private List<Idoso> idosos;
    private List<Contato> contatos;
    private List<Medicacao> medicacoes;
    private List<Programa> programas;

    private CuidadorFirebaseRepository() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        mDatabase =  firebaseDatabase.getReference();
        cuidadorEndPoint = mDatabase.child("cuidadores");
        idosoEndPoint = mDatabase.child("idosos");
        contatoEndPoint = mDatabase.child("contatos");
        medicacaoEndPoint = mDatabase.child("medicacoes");
        programaEndPoint = mDatabase.child("programas");

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

    public Usuario criarUsuario(String nome, String telefone, String perfil) {
        String key = cuidadorEndPoint.push().getKey();

        Usuario usuario = new Usuario();
        usuario.setId(key);
        usuario.setContato(new Contato(nome, telefone));
        usuario.setPerfil(perfil);

        cuidadorEndPoint.child(key).setValue(usuario);

        return usuario;
    }

    public void excluirUsuario(Usuario usuario) {
        cuidadorEndPoint.child(usuario.getId()).removeValue();
    }

    public Query obterReferenciaUsuario(String telefone) {
        return cuidadorEndPoint.orderByChild("telefone").equalTo(telefone);
        /*cuidadorEndPoint.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                Log.d(TAG, "Usuário: " + usuario.getContato().getNome() + ", telefone: " + usuario.getContato().getTelefone());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
    }

    public Idoso adicionarIdoso(String usuarioId, String nome, String telefone) {
        DatabaseReference reference = idosoEndPoint.child(usuarioId);
        String key = reference.push().getKey();

        Idoso idoso = new Idoso();
        idoso.setId(key);
        idoso.setContato(new Contato(nome, telefone));

        reference.child(key).setValue(idoso);

        return idoso;
    }

    public Contato adicionarContato(String idosoId, Contato contato) {
        DatabaseReference reference = contatoEndPoint.child(idosoId);

        String key = reference.push().getKey();
        contato.setId(key);
        reference.child(key).setValue(contato);

        return contato;

    }

    public void atualizarContato(String idosoId, Contato contato) {
        contatoEndPoint.child(idosoId).child(contato.getId()).setValue(contato);
    }

    public void removerContato(String idosoId, String contatoId) {
        contatoEndPoint.child(idosoId).child(contatoId).removeValue();
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

    public List<Contato> getContatos() {
        return contatos;
    }

    public List<Medicacao> getMedicacoes() {
        return medicacoes;
    }

    public List<Programa> getProgramas() {
        return programas;
    }

    public void carregarListas(String idosoId) {
        carregarContatos(idosoId);
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

    private void carregarContatos(String idosoId) {
        contatoEndPoint.child(idosoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                contatos.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    Contato contato = postSnapshot.getValue(Contato.class);
                    contatos.add(contato);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
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
}
