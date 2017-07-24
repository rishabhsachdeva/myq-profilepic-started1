package dd.com.myq.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

import dd.com.myq.R;

import static com.facebook.FacebookSdk.getApplicationContext;
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {
    ImageLoader imageLoader;

    private List<Movie> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
public ImageView img;

        public MyViewHolder(View view) {
            super(view);
             img = (ImageView) view.findViewById(R.id.img);
        }
    }
    public MoviesAdapter(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
       // holder.title.setText(movie.getTitle());///////////////
       // holder.img.setImageResource(movie.getImage());

Log.d("picasso shuru ho gaya","");

        Log.d("context=", String.valueOf(holder.img.getContext()));

        Picasso.with(holder.img.getContext())
                .load(movie.getWasteImage())
                .into(holder.img);



        // holder.img.setImageResource(Integer.parseInt(movie.getWasteImage()));

//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .displayer(new FadeInBitmapDisplayer(300)).build();
//
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//                getApplicationContext())
//                .defaultDisplayImageOptions(defaultOptions)
//                .memoryCache(new WeakMemoryCache())
//                .build();

       // ImageLoader.getInstance().init(config);



//        ImageLoader imageLoader = ImageLoader.getInstance();
//
//        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
//                .resetViewBeforeLoading(true).build();
//
//        imageLoader.displayImage(movie.getWasteImage(), holder.img, options);

//        AQuery aq = new AQuery(getApplicationContext());
//        boolean memCache = true;
//        boolean fileCache = true;
//        aq.id(R.id.img).image(movie.getWasteImage(), memCache, fileCache);
//
}

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}