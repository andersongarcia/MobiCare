package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.MainActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.activities.RegistroActivity;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.util.GenericFileProvider;

import static br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService.TAKE_PHOTO_CODE;

/**
 * Created by ander on 02/11/2017.
 */

public class RegistroFotoFragment extends Fragment {

    protected static final int REQUEST_IMAGE_CAPTURE_PERMISSION = 201;
    protected static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 202;

    // Permissões a serem solicitadas
    private boolean permissionToPhotoAccepted = false;
    private boolean permissionToExternalStorageAccepted = false;
    protected String [] permissions = { android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private String perfilUsuario;
    private RegistroActivity activity;
    private FloatingActionButton btnTirarFoto;

    public void setPerfilUsuario(String perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }

    public static Fragment newInstance(String perfilUsuario) {
        RegistroFotoFragment fragment = new RegistroFotoFragment();
        fragment.setPerfilUsuario(perfilUsuario);
        return fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE_PERMISSION:
                permissionToPhotoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                permissionToExternalStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToPhotoAccepted || !permissionToExternalStorageAccepted) getActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_foto, container, false);
        setHasOptionsMenu(true);

        activity = (RegistroActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.registro_foto);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_IMAGE_CAPTURE_PERMISSION);

        btnTirarFoto = (FloatingActionButton) view.findViewById(R.id.btn_foto_perfil);
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto();
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
                if(activity.getLocalFile() != null && activity.getLocalFile().exists()){
                    activity.getCuidadorService().salvaFotoPerfil(activity.getLocalFile());
                }
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

    public void tirarFoto(){
        try {
            activity.setLocalFile(FotoService.getTempFile(activity.getPackageName()));

            /*activity.setLocalFileUri(getActivity().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));*/

            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(activity, activity.getPackageName() + ".util.fileprovider", activity.getLocalFile()));
            activity.startActivityForResult(intent, TAKE_PHOTO_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
