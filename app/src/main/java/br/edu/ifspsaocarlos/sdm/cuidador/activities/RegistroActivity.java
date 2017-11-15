package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.RegistroPerfilFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.CuidadorService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;

import static br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService.TAKE_PHOTO_CODE;


/**
 * Activity para registro do usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroActivity extends AppCompatActivity {

    private CuidadorService cuidadorService;
    private File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean("authFirebase", true)){
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Intent intent = new Intent(this, PhoneAuthActivity.class);
                startActivity(intent);
            }
        }

        verificaLogado();

        setContentView(R.layout.activity_registro);

        cuidadorService = new CuidadorService(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        abrirFragment(RegistroPerfilFragment.newInstance());
    }

    private void verificaLogado() {
        CuidadorService service = new CuidadorService(this);

        if(service.verificaUsuarioLogado()){
            // Se não estiver logado, redireciona para tela de registro do usuário
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void abrirFragment(Fragment fragment){
        getFragmentManager().beginTransaction().replace(R.id.frame_registro, fragment).commit();
    }

    public CuidadorService getCuidadorService() {
        return cuidadorService;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case TAKE_PHOTO_CODE:
                    FotoService.corrigeRotacao(this, localFile);
                    if(localFile.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        ImageView ivFotoPerfil = (ImageView)findViewById(R.id.iv_foto_perfil);
                        ivFotoPerfil.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public File getLocalFile() {
        return localFile;
    }
}