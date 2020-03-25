package pt.ulisboa.tecnico.cmov.foodist;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<FoodService> mDataset;
    private JSONArray durations;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView openingHour;
        public TextView status;
        public TextView ETA;
        public ImageView icon;

        public MyViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            icon = (ImageView) v.findViewById(R.id.service_photo);
            openingHour = (TextView) v.findViewById(R.id.openingHour);
            status = (TextView) v.findViewById(R.id.is_open);
            ETA = (TextView) v.findViewById(R.id.ETA);
        }
    }
    public MyAdapter(ArrayList<FoodService> listFoodServices, String data) {
        mDataset = listFoodServices;
        try {
            JSONObject json = new JSONObject(data);
            durations = json.getJSONArray("durations").getJSONArray(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
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
        holder.openingHour.setText(mDataset.get(position).getOpeningHour() + " - " + mDataset.get(position).getClosingHour());
        try {
            holder.ETA.setText(getTime((int) durations.getDouble(position)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int minutes = calendar.get(Calendar.MINUTE);
        String[] openingSplit = mDataset.get(position).getOpeningHour().split(":");
        String[] closingSplit = mDataset.get(position).getClosingHour().split(":");
        Log.i("MYLOGS", openingSplit[0] + " " + closingSplit[0]);
        Log.i("MYLOGS", openingSplit[1] + " " + closingSplit[1]);
        Log.i("MYLOGS", hours + " " + minutes);
        if (Integer.parseInt(openingSplit[0]) == hours && Integer.parseInt(openingSplit[1]) <= minutes) {
            holder.status.setText("Open");
            holder.status.setTextColor(0xFF00AA00);

        } else if (Integer.parseInt(closingSplit[0]) == hours && Integer.parseInt(closingSplit[1]) > minutes ) {
            holder.status.setText("Open");
            holder.status.setTextColor(0xFF00AA00);

        } else if( Integer.parseInt(openingSplit[0]) < hours &&
                    Integer.parseInt(closingSplit[0]) > hours ) {
            holder.status.setText("Open");
            holder.status.setTextColor(0xFF00AA00);
        } else {
            holder.status.setText("Closed");
            holder.status.setTextColor(Color.RED);
        }
    }

    private String getTime(int duration) {
        Log.i("MYLOGS", duration +"");
        return duration/60 + " mins";
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}