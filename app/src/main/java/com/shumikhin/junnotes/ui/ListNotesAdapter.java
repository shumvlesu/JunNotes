package com.shumikhin.junnotes.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.shumikhin.junnotes.R;
import com.shumikhin.junnotes.data.NotesData;
import com.shumikhin.junnotes.data.NotesSource;

import java.text.SimpleDateFormat;


//это класс для управления адаптером
public class ListNotesAdapter extends RecyclerView.Adapter<ListNotesAdapter.ViewHolder> {

    private OnItemClickListener itemClickListener;
    private NotesSource dataSource;
    private final Fragment fragment;
    private int menuPosition;


    public ListNotesAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setDataSource(NotesSource dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(dataSource.getNotesData(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }


    // Этот класс хранит связь между данными и элементами View
    // Сложные данные могут потребовать несколько View на один пункт списка
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;
        private final TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            CardView cardViewItem = itemView.findViewById(R.id.cardViewItem);
            registerContextMenu(itemView);

            cardViewItem.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            //долгое нажатие
            cardViewItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    menuPosition = getLayoutPosition();
                    itemView.showContextMenu(10, 10);
                    return true;
                }
            });


        }

        private void registerContextMenu(@NonNull View itemView) {
            if (fragment != null) {
                itemView.setOnLongClickListener(v -> {
                    menuPosition = getLayoutPosition();
                    return false;
                });
                fragment.registerForContextMenu(itemView);
            }
        }

        public void setData(NotesData notesData) {
            title.setText(notesData.getTitleNote());
            description.setText(notesData.getDescriptionNote());
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("E dd.MM.yyyy 'Время:' hh:mm:ss");
            date.setText(String.valueOf(formatForDateNow.format(notesData.getDateNote())));
        }

    }

    //Область текста ответственная на обработку нажатий+++++++
    // Сеттер слушателя нажатий
    public void SetOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Интерфейс для обработки нажатий, как в ListView
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public int getMenuPosition() {
        return menuPosition;
    }

}