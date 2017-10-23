package br.edu.ifspsaocarlos.sdm.cuidador.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

// seletor de hora
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private int viewId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        ((EditText)getActivity().findViewById(viewId)).setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public static TimePickerFragment newInstance(int viewId) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setViewId(viewId);

        return timePickerFragment;
    }
}
