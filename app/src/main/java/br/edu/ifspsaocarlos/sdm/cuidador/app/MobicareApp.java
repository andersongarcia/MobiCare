package br.edu.ifspsaocarlos.sdm.cuidador.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import br.edu.ifspsaocarlos.sdm.cuidador.services.UsuarioService;

public class MobicareApp extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        // inicia alertas de remédios e programas
        UsuarioService usuarioService = new UsuarioService(getBaseContext());
        usuarioService.sincronizarRemedios();
        usuarioService.sincronizarProgramas();
    }
}