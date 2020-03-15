package pt.ulisboa.tecnico.cmov.foodist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<FoodService> mDataset;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView openingHour;
        public TextView closingHour;
        public TextView ETA;

        public MyViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            openingHour = (TextView) v.findViewById(R.id.openingHour);
            closingHour = (TextView) v.findViewById(R.id.closingHour);
            ETA = (TextView) v.findViewById(R.id.ETA);
        }
    }
    public MyAdapter(ArrayList<FoodService> listFoodServices) {
        mDataset = listFoodServices;
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.setText(mDataset.get(position).getName());
        holder.openingHour.setText(mDataset.get(position).getOpeningHour());
        holder.closingHour.setText(mDataset.get(position).getClosingHour());
        holder.ETA.setText(mDataset.get(position).getETA());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}