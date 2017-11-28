package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.RegistroPerfilFragment;
import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;

import static br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService.TAKE_PHOTO_CODE;


/**
 * Activity para registro do usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroActivity extends AppCompatActivity {

    private UsuarioService usuarioService;
    private File localFile;
    private android.support.v4.app.Fragment contentRegistroCuidador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            contentRegistroCuidador = getSupportFragmentManager().getFragment(savedInstanceState, "RegistroCuidadorFragment");
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean("authFirebase", false)){
            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Intent intent = new Intent(this, AutenticaSMSActivity.class);
                startActivity(intent);
            }
        }

        verificaLogado();

        setContentView(R.layout.activity_registro);

        usuarioService = new UsuarioService(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        abrirFragment(RegistroPerfilFragment.newInstance());
    }

    private void verificaLogado() {
        UsuarioService service = new UsuarioService(this);

        if(service.verificaUsuarioLogado()){
            // Se não estiver logado, redireciona para tela de registro do usuário
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void abrirFragment(Fragment fragment){
        getFragmentManager().beginTransaction().replace(R.id.frame_registro, fragment).commit();
    }

    public UsuarioService getUsuarioService() {
        return usuarioService;
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "RegistroCuidadorFragment", contentRegistroCuidador);
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public File getLocalFile() {
        return localFile;
    }
}