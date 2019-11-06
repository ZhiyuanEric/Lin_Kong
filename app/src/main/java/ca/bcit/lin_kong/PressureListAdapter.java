package ca.bcit.lin_kong;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PressureListAdapter extends ArrayAdapter<Pressure> {
    private Activity context;
    private List<Pressure> pressureList;

    public PressureListAdapter(Activity context, List<Pressure> pressureList) {
        super(context, R.layout.list_layout, pressureList);
        this.context = context;
        this.pressureList = pressureList;
    }

    public PressureListAdapter(Context context, int resource, List<Pressure> objects, Activity context1, List<Pressure> pressureList) {
        super(context, resource, objects);
        this.context = context1;
        this.pressureList = pressureList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView tvUserId = listViewItem.findViewById(R.id.textViewUserId);
        TextView tvSystolic = listViewItem.findViewById(R.id.textViewSystolic);
        TextView tvDiastolic = listViewItem.findViewById(R.id.textViewDiastolic);
        TextView tvReadDate = listViewItem.findViewById(R.id.textViewReadDate);
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd ");

        Pressure pressure = pressureList.get(position);
        tvUserId.setText(pressure.getUserId());
        tvSystolic.setText(pressure.getSystolic());
        tvDiastolic.setText(pressure.getDiastolic());
        tvReadDate.setText(formatter.format(pressure.getReadDate()));

        return listViewItem;
    }

}