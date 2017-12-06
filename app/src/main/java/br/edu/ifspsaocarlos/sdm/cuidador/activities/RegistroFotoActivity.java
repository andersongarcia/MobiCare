package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.data.PreferenciaHelper;
import br.edu.ifspsaocarlos.sdm.cuidador.repositories.ContatosRepository;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.util.GenericFileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService.CAMERA_REQUEST;

/**
 * Activity para registro da foto de perfil do usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroFotoActivity extends RegistroBaseActivity {
    protected static final int REQUEST_PERMISSIONS = 201;
    private static final String TAG = "RegistroFoto";

    // Permissões a serem solicitadas
    private boolean permissionToPhotoAccepted = false;
    private boolean permissionToExternalStorageAccepted = false;
    protected String [] permissions = { android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @BindView(R.id.ll_empty_view)
    View emptyView;
    @BindView(R.id.tv_empty_view)
    TextView tvEmptyView;
    @BindView(R.id.tv_empty_view_help)
    TextView tvEmptyViewHelp;


    private FloatingActionButton btnTirarFoto;

    private File localFile;

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public File getLocalFile() {
        return localFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSIONS: // verifica se possui as permissões necessárias
                permissionToPhotoAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToExternalStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        // Se não possuir permissões, finaliza activity
        if (!permissionToPhotoAccepted || !permissionToExternalStorageAccepted) this.finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro_foto);
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this, this);

        // Configura toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.registro_foto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Chama verificação das permissões
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        }

        // Define texto exibido enquanto não há foto
        tvEmptyView.setText(R.string.nenhuma_foto);
        tvEmptyViewHelp.setText(R.string.foto_empty);

        // Seta ação do fab para abrir câmera
        btnTirarFoto = (FloatingActionButton) findViewById(R.id.btn_foto_perfil);
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro, menu);
        menu.findItem(R.id.pular).setVisible(true);
        menu.findItem(R.id.salvar).setVisible(false);
        return true;
    }

    /**
     * Implementa ações da toolbar
     * @param item item do menu clicado
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(RegistroFotoActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (item.getItemId()) {

            // Ação salvar
            case R.id.salvar:
                // Verifica se arquivo da foto foi salvo corretamente
                if(getLocalFile() != null && getLocalFile().exists()){
                    // Salva foto do perfil no storage
                    usuarioService.salvaFotoPerfil(getLocalFile()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            PreferenciaHelper preferencias = new PreferenciaHelper(getBaseContext());
                            // Atualiza URI da foto de perfil no database
                            ContatosRepository.getInstance().salvaUriContato(preferencias.getUsuarioLogadoId(), taskSnapshot.getDownloadUrl().toString());
                        }
                    });
                }
                // Redireciona para activity principal
                startActivity(intent);
                break;

            // Ação pular
            case R.id.pular:
                startActivity(intent);
                break;

            // Ação voltar
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Captura retorno da câmera
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case CAMERA_REQUEST:
                    // Corrige a rotação da foto (bug em alguns aparelhos)
                    FotoService.corrigeRotacao(this, localFile);
                    if(localFile.exists()){
                        // Exibe foto na tela
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        ImageView ivFotoPerfil = (ImageView)findViewById(R.id.iv_foto_perfil);
                        ivFotoPerfil.setImageBitmap(bitmap);
                        emptyView.setVisibility(View.INVISIBLE);
                        toolbar.getMenu().findItem(R.id.salvar).setVisible(true);
                        toolbar.getMenu().findItem(R.id.pular).setVisible(false);
                    }
                    break;
            }
        }
    }

    /**
     * Abre a câmera
     */
    public void tirarFoto(){
        try {
            setLocalFile(FotoService.getTempFile(getPackageName()));

            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(this, getPackageName() + ".util.fileprovider", getLocalFile()));
            intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
