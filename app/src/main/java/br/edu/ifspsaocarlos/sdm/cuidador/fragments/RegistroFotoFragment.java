package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.RegistroActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;

/**
 * Created by ander on 02/11/2017.
 */

public class RegistroFotoFragment extends Fragment {
    private String perfilUsuario;
    private RegistroActivity activity;
    private FloatingActionButton btnTirarFoto;
    private FotoService fotoService;

    public void setPerfilUsuario(String perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }

    public static Fragment newInstance(String perfilUsuario) {
        RegistroFotoFragment fragment = new RegistroFotoFragment();
        fragment.setPerfilUsuario(perfilUsuario);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_foto, container, false);
        setHasOptionsMenu(true);

        activity = (RegistroActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.registro_foto);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTirarFoto = (FloatingActionButton) view.findViewById(R.id.btn_foto_perfil);
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fotoService = new FotoService(){

                    @Override
                    public void run(File arquivoFoto) {
                        activity.getCuidadorService().salvarFotoPerfil(arquivoFoto, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        });
                    }
                };
                fotoService.tirarFoto(activity);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_cadastro ,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.salvar:
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                break;

            case android.R.id.home:
                switch (perfilUsuario){
                    case Usuario.CUIDADOR:
                        activity.abrirFragment(RegistroCuidadorFragment.newInstance());
                        break;
                    case  Usuario.IDOSO:
                        activity.abrirFragment(RegistroIdosoFragment.newInstance());
                        break;
                    case  Usuario.CONTATO:
                        activity.abrirFragment(RegistroContatoFragment.newInstance());
                        break;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fotoService.retornaFoto(requestCode, resultCode, activity);
    }
}
