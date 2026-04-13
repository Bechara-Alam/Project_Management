package com.example.tatwa10.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.Patient;
import com.example.tatwa10.R;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    public interface OnPatientClick {
        void onClick(Patient patient);
    }

    private List<Patient> list;
    private OnPatientClick listener;

    public PatientAdapter(List<Patient> list, OnPatientClick listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Patient p = list.get(position);
        holder.name.setText(p.getFullName());

        holder.itemView.setOnClickListener(v -> {
            listener.onClick(p); // 🔥 THIS IS IMPORTANT
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}