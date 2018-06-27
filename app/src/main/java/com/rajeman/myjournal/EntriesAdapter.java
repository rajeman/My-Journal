package com.rajeman.myjournal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.EntriesViewHolder> {
    private List<UserEntry> userEntryList;
    Fragment fragment;
    Context context;
    private final int totalAttachmentImageViews = 4;

    public EntriesAdapter(Fragment fragment, List<UserEntry> userEntryList) {
        this.fragment = fragment;
        this.userEntryList = userEntryList;
        context = fragment.getContext();
    }

    public class EntriesViewHolder extends RecyclerView.ViewHolder {

        TextView  dayTextView;
        TextView  weekDayTextView;
        TextView  titleTextView;
        TextView textSummaryTextView;
        TextView locationTextView;
        TextView monthYearTextView;
        ImageView entryImage;
        public EntriesViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day_text_view);
            weekDayTextView = itemView.findViewById(R.id.wk_day_text_view);
            titleTextView = itemView.findViewById(R.id.entry_title);
            textSummaryTextView = itemView.findViewById(R.id.entry_text_summary);
            locationTextView = itemView.findViewById(R.id.location_text_view);
            monthYearTextView = itemView.findViewById(R.id.month_year_text_view);
            entryImage = itemView.findViewById(R.id.entry_image_view);

        }
    }


    @NonNull
    @Override
    public EntriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =  inflater.inflate(R.layout.item_journal_entries, parent, false );
        return new EntriesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EntriesViewHolder holder, int position) {

        String day, wkDay, title, summary, location, monthYear;
        holder.textSummaryTextView.setText(userEntryList.get(position).getSummary());

    }

    @Override
    public int getItemCount() {
        return userEntryList.size();
    }

    public void setNewItems(List<UserEntry> entryList){
        this.userEntryList.clear();
        this.userEntryList.addAll(entryList);

    }

}
