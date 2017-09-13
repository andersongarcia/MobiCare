package br.edu.ifspsaocarlos.sdm.cuidador.data;

import android.content.Context;
import android.content.SharedPreferences;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Contato;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Idoso;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Medicacao;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Programa;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Repositório de acesso a dados para Usuarios, idosos e contatos
 *
 * @author Anderson Canale Garcia
 */
public class CuidadorRepository {
    private final Realm realm;

    public CuidadorRepository(Context context) {
        // Get a Realm instance for this thread
        this.realm = Realm.getDefaultInstance();
    }

    private void inserirUsuario(Usuario usuario) {
        realm.beginTransaction();
        realm.copyToRealm(usuario);
        realm.commitTransaction();
    }

    public void excluirUsuario(Usuario usuario) {
        RealmResults<Usuario> results = realm.where(Usuario.class).equalTo("contato.telefone", usuario.getContato().getTelefone()).findAll();

        realm.beginTransaction();
        results.deleteFirstFromRealm();
        realm.commitTransaction();
    }

    public Usuario buscaUsuarioPeloTelefone(String telefone) {
        return realm.where(Usuario.class).equalTo("contato.telefone", telefone).findFirst();
    }

    public Usuario criarUsuario(String nome, String telefone) {
        Contato contato = new Contato();
        contato.setNome(nome);
        contato.setTelefone(telefone);
        Usuario usuario = new Usuario();
        usuario.setContato(contato);
        usuario.setIdosos(new RealmList<Idoso>());
        this.inserirUsuario(usuario);

        return usuario;
    }

    public void adicionarIdoso(final Usuario usuario, final String nome, final String telefone) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Contato contato = new Contato();
                contato.setNome(nome);
                contato.setTelefone(telefone);
                final Contato managedContato = realm.copyToRealmOrUpdate(contato); // Persist unmanaged objects
                Idoso idoso = new Idoso();
                idoso.setContato(managedContato);
                final Idoso managedIdoso = realm.copyToRealm(idoso); // Persist unmanaged objects
                usuario.getIdosos().add(managedIdoso);
            }
        });
    }

    public Idoso buscaIdosoPeloTelefone(String telefone) {
        return realm.where(Idoso.class).equalTo("contato.telefone", telefone).findFirst();
    }

    public void adicionarContato(final Idoso idoso, final Contato contato) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final Contato managedContato = realm.copyToRealmOrUpdate(contato); // Persist unmanaged objects
                idoso.getContatos().add(managedContato);
            }
        });

    }

    public void removerContato(String telefone) {
        RealmResults<Contato> results = realm.where(Contato.class).equalTo("telefone", telefone).findAll();

        realm.beginTransaction();
        results.deleteFirstFromRealm();
        realm.commitTransaction();
    }

    public void adicionarMedicacao(final Idoso idoso, final Medicacao medicacao) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final Medicacao managedMedicacao = realm.copyToRealmOrUpdate(medicacao); // Persist unmanaged objects
                idoso.getMedicacoes().add(managedMedicacao);
            }
        });
    }
    public void removerMedicacao(String nome) {
        RealmResults<Medicacao> results = realm.where(Medicacao.class).equalTo("nome", nome).findAll();

        realm.beginTransaction();
        results.deleteFirstFromRealm();
        realm.commitTransaction();
    }

    public void adicionarPrograma(final Idoso idoso, final Programa programa) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final Programa managedPrograma = realm.copyToRealmOrUpdate(programa); // Persist unmanaged objects
                idoso.getProgramas().add(managedPrograma);
            }
        });
    }
}
