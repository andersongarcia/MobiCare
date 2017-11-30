package br.edu.ifspsaocarlos.sdm.cuidador.repositories;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.enums.NO;
import br.edu.ifspsaocarlos.sdm.cuidador.services.AlarmeService;

/**
 * Repository para programas favoritos
 *
 * @author Anderson Canale Garcia
 */
public class ProgramasRepository extends Observable {
    private static final String TAG = "ProgramasRepository";

    private static ProgramasRepository repository;
    private final DatabaseReference programaEndPoint;
    private List<Programa> programas;

    /**
     * Construtor
     */
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

    /**
     * Adiciona novo programa na base
     * @param idosoId Id do idoso
     * @param programa Instância do programa favorito
     * @return Id gerado do novo programa
     */
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

    /**
     * Atualiza programa favorito
     * @param idosoId Id do idoso
     * @param programa Instância do programa favorito
     */
    private void atualizaPrograma(String idosoId, Programa programa) {
        programaEndPoint.child(idosoId).child(programa.getId()).setValue(programa);
    }

    /**
     * Remove programa favorito
     * @param idosoId Id do idoso
     * @param programaId Id do programa a ser removido
     */
    public void removePrograma(String idosoId, String programaId) {
        programaEndPoint.child(idosoId).child(programaId).removeValue();
        removeProgramaDaLista(programaId);
    }

    /**
     * Carrega lista de programas favoritos
     * @param idosoId Id do idoso
     * @param alarmeService Instância do serviço de alarme, se for necessária reprogramação
     */
    public void carregaProgramas(String idosoId, final AlarmeService alarmeService) {
        // Pega referência para programas do idosos selecionado
        final DatabaseReference reference = programaEndPoint.child(idosoId);
        // Faz o carregamento inicial e adiciona listener para modificações em tempo real
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                programas.clear(); // limpa lista de programas
                // Para cada programa lido do banco, cria instância e insere na lista
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    // Lê dados do snapshot e converte para programa
                    Programa programa = postSnapshot.getValue(Programa.class);
                    if(programa.getId() != null){
                        programas.add(programa);
                        // Se foi passado o serviço de alarme, atualiza
                        if(alarmeService != null){
                            alarmeService.atualizaAlarmePrograma(programa);
                        }
                    }
                }
                // notifica observers que houve alteração na lista
                setChanged();
                notifyObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                reference.removeEventListener(this);
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Remove programa da lista carregada
     * @param programaId Id do programa
     */
    private void removeProgramaDaLista(String programaId) {
        Iterator<Programa> i = programas.iterator();
        while (i.hasNext()) {
            Programa programa = i.next();
            if(programa.getId() == programaId)
                i.remove();
        }
    }

    /**
     * Façade para salvar programa. Adiciona se for novo, atualiza se já existente.
     * @param idosoId Id do idoso selecionado
     * @param programa Instância do programa a ser salvo
     * @return Id do programa salvo, inclusive se for novo
     */
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
