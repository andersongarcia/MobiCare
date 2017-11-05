package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Created by ander on 29/10/2017.
 */

public class IMService {

    private final static String TAG = "IMService";

    public static void subscribe(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public static void unsubscribe(){
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getToken(Context contexto){
        // Get token
        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        String msg = contexto.getString(R.string.msg_token_fmt, token);
        Log.d(TAG, msg);

        return token;
    }

}
