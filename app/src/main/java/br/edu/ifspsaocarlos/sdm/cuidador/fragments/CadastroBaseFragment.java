package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;

/**
 * Created by ander on 28/10/2017.
 */

public abstract class CadastroBaseFragment extends Fragment {

    protected static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    protected static final int REQUEST_IMAGE_CAPTURE_PERMISSION = 201;
    protected static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 202;
    private final Fragment fragmentLista;
    private final CuidadorService.NO no;

    protected CuidadorService service;
    MainActivity activity;
    protected View view;

    // Permissões a serem solicitadas
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToPhotoAccepted = false;
    private boolean permissionToExternalStorageAccepted = false;
    protected String [] permissions = {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    protected FloatingActionButton btnTirarFoto;
    protected ImageView ivAvatar;
    private FotoService fotoService;

    public CadastroBaseFragment(Fragment fragmentLista, CuidadorService.NO no) {
        this.fragmentLista = fragmentLista;
        this.no = no;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutCadastro(), container, false);
        setHasOptionsMenu(true);

        activity = (MainActivity) getActivity();
        activity.showBackButton(true);

        ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);
        criarReferenciasLayout();
        carregarInformacoesCadastradas();

        btnTirarFoto = (FloatingActionButton) view.findViewById(R.id.btn_add_avatar);
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.setFotoService(new FotoService() {

                    @Override
                    public void run(File arquivoFoto) {
                        service.salvarFoto(no, getIdCadastro(), arquivoFoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                FotoService.carregarAvatar(service, no, getIdCadastro(), ivAvatar);
                            }
                        });
                    }
                });
                activity.getFotoService().tirarFoto(activity);
            }
        });

        if(getIdCadastro() != null && !getIdCadastro().isEmpty()){
            carregarAvatar();
        }

        carregarOutrasReferencias();

        return view;
    }

    private void carregarAvatar() {
        FotoService.carregarAvatar(service, no, getIdCadastro(), ivAvatar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case REQUEST_IMAGE_CAPTURE_PERMISSION:
                permissionToPhotoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                permissionToExternalStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;

        }
        if (!permissionToRecordAccepted || !permissionToPhotoAccepted || !permissionToExternalStorageAccepted ) getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cadastro ,menu);
        menu.findItem(R.id.excluir).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.salvar:
                salvar();
                redirecionaParaLista();
                break;
            case R.id.excluir:
                excluir();
                redirecionaParaLista();
                break;
            case android.R.id.home:
                redirecionaParaLista();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void redirecionaParaLista() {
        activity.showBackButton(false);
        activity.openFragment(fragmentLista);
    }
    protected abstract void salvar();

    protected abstract void excluir();

    protected abstract int getLayoutCadastro();

    /**
     * @return Id da entidade de cadastro
     */
    protected abstract String getIdCadastro();

    protected abstract void criarReferenciasLayout();

    protected abstract void carregarInformacoesCadastradas();

    protected abstract void carregarOutrasReferencias();

}
