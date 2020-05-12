package pt.ulisboa.tecnico.cmov.foodist.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.time.temporal.TemporalAccessor;
import  java.time.Instant;
import java.time.format.DateTimeFormatter;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;

public class FoodServicesAdapter extends RecyclerView.Adapter<FoodServicesAdapter.MyViewHolder> {
    private ArrayList<FoodService> mDataset;
    private JSONArray durations;
    private JSONArray queues;
    private View.OnClickListener mOnItemClickListener;


    // Provide a reference to the views for each data item
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView openingHour;
        public TextView status;
        public TextView ETA;
        public TextView queue;
        public ImageView icon;
        public ImageView clock;
        public ImageView walk;

        public MyViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            icon = (ImageView) v.findViewById(R.id.service_photo);
            clock = (ImageView) v.findViewById(R.id.clock);
            walk = (ImageView) v.findViewById(R.id.walk);
            openingHour = (TextView) v.findViewById(R.id.openingHour);
            status = (TextView) v.findViewById(R.id.is_open);
            ETA = (TextView) v.findViewById(R.id.ETA);
            queue = (TextView) v.findViewById(R.id.queue);
            v.setTag(this);
            v.setOnClickListener(mOnItemClickListener);
        }
    }
    public FoodServicesAdapter(ArrayList<FoodService> listFoodServices) {
        mDataset = listFoodServices;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FoodServicesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_service, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.name.setText(mDataset.get(position).getName());

        if (mDataset.get(position).getType() == "RESTAURANT") {
            holder.icon.setImageResource(R.drawable.ic_restaurant);
        } else {
            holder.icon.setImageResource(R.drawable.coffee4);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        DateFormat presDateFormat = new SimpleDateFormat("HH:mm");
        Date current = new Date();   // given date

        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(mDataset.get(position).getOpeningHour());
        Instant i = Instant.from(ta);
        Date open = Date.from(i);

        ta = DateTimeFormatter.ISO_INSTANT.parse(mDataset.get(position).getClosingHour());
        i = Instant.from(ta);
        Date close = Date.from(i);

        holder.openingHour.setText(String.format("%s - %s", presDateFormat.format(open), presDateFormat.format(close)));

        if (durations != null) {
            try {
                holder.ETA.setText(getTime((int) durations.getDouble(position)));
                holder.walk.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                Log.e("MYLOGS", "could not correctly parse duration json");
                e.printStackTrace();
                holder.ETA.setText("--:--");
                holder.walk.setVisibility(View.VISIBLE);

            }
        }
        else {
            holder.ETA.setText("--:--");
            holder.walk.setVisibility(View.VISIBLE);
        }

        if (queues != null) {
            try {
                holder.queue.setText(getTime((int) queues.getDouble(position)));
                holder.clock.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                Log.e("MYLOGS", "could not correctly parse queues json");
                e.printStackTrace();
                holder.queue.setText("--:--");
                holder.clock.setVisibility(View.VISIBLE);

            }
        }
        else {
            holder.queue.setText("--:--");
            holder.clock.setVisibility(View.VISIBLE);
        }

        try {
            open = presDateFormat.parse(presDateFormat.format(open));
            close = presDateFormat.parse(presDateFormat.format(close));
            current = presDateFormat.parse(presDateFormat.format(current));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (current.compareTo(close) < 0 && current.compareTo(open) > 0) {
            holder.status.setText(R.string.Open);
            holder.status.setTextColor(0xFF00AA00);
        } else {
            holder.status.setText(R.string.Closed);
            holder.status.setTextColor(Color.RED);
        }
    }

    private String getTime(int duration) {
        return duration/60 + " mins";
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public void setDuration(String data) {
        if (data == "") {
            durations = null;
        }
        try {
            JSONObject json = new JSONObject(data);
            durations = json.getJSONArray("durations").getJSONArray(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setQueues(String data) {
        if (data == "") {
            durations = null;
        }
        try {
            JSONObject json = new JSONObject(data);
            queues = json.getJSONArray("durations").getJSONArray(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}