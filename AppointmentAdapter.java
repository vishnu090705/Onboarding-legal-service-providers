package com.example.legislature.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.legislature.R;
import com.example.legislature.models.Appointment;

import java.util.List;

public class AppointmentAdapter extends BaseAdapter {

    private final Context context;
    private final List<Appointment> list;

    public AppointmentAdapter(Context context, List<Appointment> list) {
        this.context = context;
        this.list = list;
    }

    @Override public int getCount() { return list.size(); }
    @Override public Object getItem(int position) { return list.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(context).inflate(R.layout.item_appointment_card, parent, false)
                : convertView;

        Appointment a = list.get(position);

        TextView tvDate   = view.findViewById(R.id.cardDate);
        TextView tvTime   = view.findViewById(R.id.cardTime);
        TextView tvStatus = view.findViewById(R.id.cardStatus);

        tvDate.setText("Date: " + a.getDate());     // model uses String date
        tvTime.setText("Time: " + a.getTime());
        tvStatus.setText("Status: " + a.getStatus());

        return view;
    }
}
