package com.rajeman.myjournal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rajeman.myjournal.GlideApp;
import com.rajeman.myjournal.R;
import com.rajeman.myjournal.UserEntry;
import com.rajeman.myjournal.utils.DateUtils;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.EntriesViewHolder> {
    private List<UserEntry> userEntryList;
    Fragment fragment;
    Context context;

    public interface EntryClickNotifier {
        void onNotify( int position);
    }
    EntryClickNotifier entryClickNotifier;
    public EntriesAdapter(Fragment fragment, List<UserEntry> userEntryList) {
        this.fragment = fragment;
        this.userEntryList = userEntryList;
        context = fragment.getContext();
        entryClickNotifier = (EntryClickNotifier) fragment.getActivity();
    }

    public class EntriesViewHolder extends RecyclerView.ViewHolder {

        TextView  dayTextView;
        TextView  weekDayTextView;
        TextView  titleTextView;
        TextView textSummaryTextView;
        TextView locationTextView;
        TextView monthYearTextView;
        TextView timeTextView;
        ImageView entryImage;

        public EntriesViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day_text_view);
            weekDayTextView = itemView.findViewById(R.id.wk_day_text_view);
            titleTextView = itemView.findViewById(R.id.entry_title);
            textSummaryTextView = itemView.findViewById(R.id.entry_text_summary);
            locationTextView = itemView.findViewById(R.id.location_text_view);
            monthYearTextView = itemView.findViewById(R.id.month_year_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            entryImage = itemView.findViewById(R.id.entry_image_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    entryClickNotifier.onNotify(adapterPosition);
                }
            });
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


        UserEntry userEntry = userEntryList.get(position);
        holder.textSummaryTextView.setText(userEntry.getStory());
        holder.locationTextView.setText(userEntry.getLocation());
        holder.titleTextView.setText(userEntry.getTitle());

        DateUtils dateUtils = new DateUtils(userEntry.getTimestampLong());
        holder.weekDayTextView.setText(dateUtils.getWeekDay());
        holder.dayTextView.setText(dateUtils.getDay());
        holder.timeTextView.setText(dateUtils.getTime());
        holder.monthYearTextView.setText(dateUtils.getMonthYear());

        if(userEntry.getImageLink() != null){
            GlideApp.with(fragment).load(userEntry.getImageLink()).centerCrop().transition(withCrossFade()).into(holder.entryImage);
        } else{
            GlideApp.with(fragment).clear(holder.entryImage);
        }




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
