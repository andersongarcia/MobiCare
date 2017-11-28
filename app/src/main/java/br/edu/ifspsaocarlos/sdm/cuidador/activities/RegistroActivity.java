package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.Usuario;
import br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService;
import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;

import static br.edu.ifspsaocarlos.sdm.cuidador.services.FotoService.TAKE_PHOTO_CODE;


/**
 * Activity para registro do usuário
 *
 * @author Anderson Canale Garcia
 */
public class RegistroActivity extends RegistroBaseActivity {

    private File localFile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_registro);
        super.onCreate(savedInstanceState);

        ListView listView = (ListView) findViewById(R.id.lv_perfis);

        List<String> perfis = Arrays.asList( Usuario.CUIDADOR, Usuario.IDOSO, Usuario.CONTATO);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, perfis);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                String perfil = (String)adapterView.getItemAtPosition(position);

                switch (perfil){
                    case Usuario.CUIDADOR:
                        Intent intentCuidador = new Intent(RegistroActivity.this, RegistroCuidadorActivity.class);
                        startActivity(intentCuidador);
                        break;
                    case Usuario.IDOSO:
                        Intent intentIdoso = new Intent(RegistroActivity.this, RegistroIdosoActivity.class);
                        startActivity(intentIdoso);
                        break;
                    case Usuario.CONTATO:
                        Intent intentContato = new Intent(RegistroActivity.this, RegistroContatoActivity.class);
                        startActivity(intentContato);
                        break;
                }
            }
        });
    }

    public void abrirFragment(Fragment fragment){
        //getFragmentManager().beginTransaction().replace(R.id.frame_registro, fragment).commit();
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

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public File getLocalFile() {
        return localFile;
    }
}