package com.android.bsb.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.android.bsb.R;

import java.util.Calendar;

public class DatePickerDialog extends AlertDialog implements
        DialogInterface.OnClickListener,
        DatePicker.OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private DatePicker mDatePicker;



    private android.app.DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    // Clearing focus forces the dialog to commit any pending
                    // changes, e.g. typed text in a NumberPicker.
                    mDatePicker.clearFocus();
                    mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(),
                            mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }


    public void updateDate(int year,int monthfYear,int dayOfmonth){
        mDatePicker.updateDate(year,monthfYear,dayOfmonth);
    }


    public DatePickerDialog(Context context){
        super(context);
        init(context,-1,-1,-1);
    }

    public void setOnDateSetListener(android.app.DatePickerDialog.OnDateSetListener listener) {
        mDateSetListener = listener;
    }


    private void init(Context context,int year,int month,int day){
        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.layout_date_picker,null);
        setView(view);
        mDatePicker = view.findViewById(R.id.datePicker);

        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        setButton(BUTTON_POSITIVE,"确定",this);

        setButton(BUTTON_NEGATIVE,"取消",this);
        mDatePicker.init(year,month,day,this);
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

}
