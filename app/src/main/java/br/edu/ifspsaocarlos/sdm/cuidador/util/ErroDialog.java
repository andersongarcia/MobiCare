package br.edu.ifspsaocarlos.sdm.cuidador.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by ander on 28/11/2017.
 */

public class ErroDialog {
    private final AlertDialog.Builder messageBox;
    private final Activity contexto;

    public ErroDialog(final Activity contexto, String tag, String mensagem){
        Log.d(tag,  mensagem);

        this.contexto = contexto;

        messageBox = new AlertDialog.Builder(contexto);
        messageBox.setTitle(tag);
        messageBox.setMessage(mensagem);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                contexto.finish();
            }
        });
    }

    public void exibe(){
        messageBox.show();
    }
}
