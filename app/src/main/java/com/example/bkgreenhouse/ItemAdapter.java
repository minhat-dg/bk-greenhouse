package com.example.bkgreenhouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<SpinnerItem> {
    public ItemAdapter(@NonNull Context context, int resource, @NonNull List<SpinnerItem> objects) {
        super(context, resource, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spiner_selected, parent, false);

        TextView tvSelected = convertView.findViewById(R.id.tv_selected);
        SpinnerItem item = this.getItem(position);

        if(item != null){
            tvSelected.setText(item.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spiner, parent, false);

        TextView tvSpinner = convertView.findViewById(R.id.tv_spinner);
        SpinnerItem item = this.getItem(position);

        if(item != null){
            tvSpinner.setText(item.getName());
        }
        return convertView;
    }
}
