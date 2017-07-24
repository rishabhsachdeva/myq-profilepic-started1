package dd.com.myq.Activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dd.com.myq.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] itemsData;

    public MyAdapter(String[] itemsData) {
        this.itemsData = itemsData;
    }

    // inner class to hold a reference to each item of RecyclerView
    public  class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgViewIcon;
        public  TextView text;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            text = (TextView) itemLayoutView.findViewById(R.id.title);

            //        imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.image);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popup_layout, parent, false);

        return new ViewHolder(itemView);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        // holder.imgViewIcon.setImageResource(R.drawable.avatars);
        holder.text.setText("fku");

    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}