package dd.com.myq.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dd.com.myq.Activity.Movie;
import dd.com.myq.Activity.MoviesAdapter;
import dd.com.myq.Activity.RecyclerTouchListener;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
public class MyDialogFragment extends DialogFragment {

     String user_id;

    MoviesAdapter mAdapter;
    private  List<Movie> movieList = new ArrayList<>();

    private  RecyclerView recyclerView;
    TextView index;
    public boolean isClickable = true;
    private void prepareMovieData() {
        AsyncHttpClient client = new AsyncHttpClient();
        String addbadgeurl = "http://myish.com:10011/api/addbadges/" + user_id;

        client.get(getActivity(), addbadgeurl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseArray) {
                try {
                    callgetapi();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("exception_add=", String.valueOf(e));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Log.d("errorinadd=", String.valueOf(errorResponse));
            }

        });
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        mAdapter.notifyDataSetChanged();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflaterr = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflaterr.inflate(R.layout.popup_layout, null);
        ImageView i = (ImageView) popupView.findViewById(R.id.image);//and set image to image view

        index = (TextView) getActivity().findViewById(R.id.index);
         user_id= index.getText().toString();

        Log.d("userid=",user_id);

        recyclerView = (RecyclerView) popupView.findViewById(R.id.mylist);

        mAdapter = new MoviesAdapter(movieList);
        prepareMovieData();

        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), HORIZONTAL, false));


        builder.setView(popupView);//add your view to alert dilaog

        builder.setIcon(R.drawable.avatars);
        FriendFragment f = new FriendFragment();
        builder.setTitle(f.name);
        //builder.setMessage("\n"+"         Points " + f.points);

//        TextView points = (TextView) getActivity().findViewById(R.id.points);
//        points.setText("  "+f.points+" points");

        builder.setMessage(Html.fromHtml("<br><font color='#FF8C00'>"+f.points+"<font color='#FF8C00'>  points</b></font>"));

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }


    public  void  callgetapi()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        String getbadgeurl="http://myish.com:10011/api/getbadges/"+user_id;

        client.get(getActivity(),getbadgeurl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,JSONArray responseArray) {
                try {
                    Log.d("badgekaarray", String.valueOf(responseArray));
                    fn(responseArray);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d("exception_get=", String.valueOf(e));

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("erroringet=", String.valueOf(errorResponse));
            }
        });
    }

    public void fn(JSONArray responseArray) {
        Movie movie;
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject object,object1;
            String badgedescription1 = null;
            String badgename1 = null;
            String badgeimage1= null;

            try {
                object = responseArray.getJSONObject(i);
                JSONArray badges = object.getJSONArray("badges");

                Log.d("badgekaarray", String.valueOf(badges));

                if (badges.length() == 0) {
                    Log.e("NO BADGES : ", "");
                }
                else {

                    for (int j = 0; j < badges.length(); j++) {
                        object1 = badges.getJSONObject(j);

                        badgename1 = object1.getString("badgename");
                        badgedescription1 = object1.getString("badgedescription");
                        badgeimage1 = object1.getString("badgeimage");
                        String url="http://myish.com:10011/images/badgeserver/"+badgeimage1;

                        Log.d("pic url=",url);

                        movie = new Movie("The Martian",url);//, "Science Fiction & Fantasy", "2015");
                        movieList.add(movie);
                        mAdapter.notifyDataSetChanged();///////////

                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}