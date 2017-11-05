package br.edu.ifspsaocarlos.sdm.cuidador.util;

import android.view.View;
import android.widget.CheckedTextView;

import br.edu.ifspsaocarlos.sdm.cuidador.R;

/**
 * Created by ander on 04/11/2017.
 */

public class CheckedTextViewHelper {
    private static View.OnClickListener toogleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toogle(view);
        }
    };

    public static void toogle(View view){
        CheckedTextView ctv = (CheckedTextView) view;
        if (ctv.isSelected())
        {
            setChecked(ctv, false);
        }
        else
        {
            setChecked(ctv, true);
        }
    }

    public static void setChecked(CheckedTextView ctv, boolean checked) {
        ctv.setChecked(checked);
        ctv.setSelected(checked);
        if(checked)
            ctv.setCheckMarkDrawable(R.drawable.ic_check_black_36dp);
        else
            ctv.setCheckMarkDrawable(null);
    }

    public static View.OnClickListener getToogleListener() {
        return toogleListener;
    }
}
