package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.util.GenericFileProvider;

import static android.app.Activity.RESULT_OK;
import static br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService.TAKE_PHOTO_CODE;

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
    protected File localFile;

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
                tirarFoto();
            }
        });

        if(getIdCadastro() != null && !getIdCadastro().isEmpty()){
            carregarAvatar();
        }

        carregarOutrasReferencias();

        return view;
    }

    public void tirarFoto(){
        try {
            localFile = FotoService.getTempFile(activity.getPackageName());

            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(activity, activity.getPackageName() + ".util.fileprovider", localFile));
            startActivityForResult(intent, TAKE_PHOTO_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void carregarAvatar() {
        if(getUriAvatar() != null && !getUriAvatar().isEmpty()){
            FotoService.carregarAvatar(service, getUriAvatar(), ivAvatar);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case TAKE_PHOTO_CODE:
                    FotoService.corrigeRotacao(activity, localFile);
                    if(localFile.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        ivAvatar.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    }

    protected abstract void salvar();

    protected abstract void excluir();

    protected abstract int getLayoutCadastro();

    /**
     * @return Id da entidade de cadastro
     */
    protected abstract String getIdCadastro();

    protected abstract String getUriAvatar();

    protected abstract void criarReferenciasLayout();

    protected abstract void carregarInformacoesCadastradas();

    protected abstract void carregarOutrasReferencias();

}