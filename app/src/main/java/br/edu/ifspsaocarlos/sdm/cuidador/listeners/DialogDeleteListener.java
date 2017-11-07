package br.edu.ifspsaocarlos.sdm.cuidador.listeners;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;

/**
 * Created by ander on 06/11/2017.
 */

public class DialogDeleteListener  implements View.OnClickListener {

    private AlertDialog dialog;

    public DialogDeleteListener(Context contexto, final CallbackSimples callback) {

        dialog = new AlertDialog.Builder(contexto)
                .setTitle(R.string.excluir)
                .setMessage(R.string.msg_confirma_excluir)
                .setIcon(R.drawable.ic_delete_black_36dp)

                .setPositiveButton(R.string.btn_excluir, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        callback.OnComplete();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    @Override
    public void onClick(View view) {
        dialog.show();
    }
}