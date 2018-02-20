package com.sidewindercookie.glance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NotesViewAdapter extends RecyclerView.Adapter<NotesViewAdapter.ViewHolder> {
    private List<Note> dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, details;
        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.noteName);
            details = view.findViewById(R.id.noteDetails);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotesViewAdapter(List<Note> notes) {
        dataset = notes;
    }

    @Override
    public NotesViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_card_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = dataset.get(position);
        holder.name.setText(note.getName());
        holder.details.setText(note.getDetails());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}

