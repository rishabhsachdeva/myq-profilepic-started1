        package dd.com.myq.Fragment;

        import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import dd.com.myq.Activity.FindFriendsActivity;
import dd.com.myq.Activity.Movie;
import dd.com.myq.Activity.MoviesAdapter;
import dd.com.myq.Fragment.Friends.Friend;
import dd.com.myq.Fragment.Friends.FriendAdapter;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

public class FriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static RecyclerView recyclerView;

    ArrayList<String> result,result1,result2;

    static Bundle args;
    TextView index;
    static MoviesAdapter mAdapter;
    private static List<Movie> movieList = new ArrayList<>();
    static String name,id_for_badges,points;


    private RelativeLayout Home, Point, Account, Friends, Levels, Categories;

    private static final int FRIEND_LOADER_ID = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootView;
    View popupView;
    public int flag=0;


    private ArrayList<String> al_name = new ArrayList<String>();
    private ArrayList<String> al_points = new ArrayList<String>();
    public  ArrayList<String> al_friend_id = new ArrayList<String>();


    ArrayList<Friend> friends;
    static String user_id;
    ListView listView;
    static ListView simpleList;
    private static FriendAdapter friendAdapter;

    ProgressDialog progress;

    //    SessionManager currentSession;
    public String REQUEST_GET_FRIENDS2 = "http://myish.com:10011/api/getfriends2/";
    public String  UPDATE_FRIENDS = "http://myish.com:10011/api/updatefriends/";

    private OnFragmentInteractionListener mListener;

    public FriendFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
        args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_friend, container, false);

        //  popupView = inflater.inflate(R.layout.popup_layout, container, false);////////////////////////////

        //recyclerView = (RecyclerView) rootView.findViewById(R.id.mylist);







        SessionManager currentSession = new SessionManager(getActivity());

        HashMap<String, String> user_details = currentSession.getUserDetails();
        user_id = user_details.get(SessionManager.KEY_UID);
      //  REQUEST_GET_FRIENDS = REQUEST_GET_FRIENDS + user_id;
        REQUEST_GET_FRIENDS2 = REQUEST_GET_FRIENDS2 + user_id;

        UPDATE_FRIENDS = UPDATE_FRIENDS + user_id;
        getUpdateFriends(UPDATE_FRIENDS);

      //  Log.d("GET FRIENDS FRAGMENT ", REQUEST_GET_FRIENDS);

        listView=(ListView)rootView.findViewById(R.id.list);

////////



        progress = new ProgressDialog(getContext(),  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);

        progress.setMessage("Loading Friends...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        for(int i=0;i<3;i++){

            getUpdateFriends(UPDATE_FRIENDS);
        }

        progress.setCancelable(false);
        progress.show();
        friends= new ArrayList<>();


        getUpdateFriends(UPDATE_FRIENDS);

        if(flag==0) {

            getUpdateFriends(UPDATE_FRIENDS);
            getAddedFriends(REQUEST_GET_FRIENDS2);

        }
        TextView findFriends = (TextView) rootView.findViewById(R.id.add_friends_text);
        findFriends.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FindFriendsActivity.class);
                startActivity(intent);

            }
        });

        return rootView;
    }

    public void getUpdateFriends(String UPDATE_FRIENDS) {

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getActivity(), UPDATE_FRIENDS , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {

                //NO RESPONSE HERE

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                super.onFailure(statusCode, headers, throwable, object);

                Toast.makeText(getActivity(), "Error Loading Friends", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void getAddedFriends(final String REQUEST_GET_FRIENDS_URL){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getActivity(), REQUEST_GET_FRIENDS_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

                if(flag==1){

                    friendAdapter.clear();
                    al_name.clear();
                    al_points.clear();
                    al_friend_id.clear();

                }

                try {

                    for (int i = 0; i < responseArray.length(); i++) {

                        JSONObject responseObject = responseArray.getJSONObject(i);

                        String name = responseObject.getString("friendname");
                        points = responseObject.getString("friendpoints");
                        String id = responseObject.getString("userId");

                        al_points.add(points);
                        al_name.add(name);
                        al_friend_id.add(id);

                    }

                    progress.dismiss();
                    Log.d("al_name", String.valueOf(al_name.size()));
                    Log.d("al_points", String.valueOf(al_points.size()));
                    Log.d("al_group_id", String.valueOf(al_friend_id.size()));

                    for (int i = 0; i < al_name.size(); i++) {

                        friends.add(new Friend(al_name.get(i), al_points.get(i)));
                    }

                    friendAdapter = new FriendAdapter(friends, getContext());

                    listView.setAdapter(friendAdapter);
                    flag=1;


                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {


                            name=al_name.get(position);
                            id_for_badges=al_friend_id.get(position);
                            points=al_points.get(position);
Log.d("friendfragmentid=",id_for_badges);

                       //     args.putString("id",id_for_badges);
//                            MyDialogFragment nextFragment = new MyDialogFragment();
//                            nextFragment.setArguments(args);
//
//                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                            fragmentManager.beginTransaction().replace(R.id.content_drawer, nextFragment).commit();

//                            Movie m=new Movie();
//                            m.setId(id_for_badges);

                            index = (TextView) rootView.findViewById(R.id.index);
                            index.setText(id_for_badges);

                            FragmentManager fm = getFragmentManager();
                            MyDialogFragment dialogFragment = new MyDialogFragment ();
                            dialogFragment.show(fm, "Sample Fragment");
                        }
                    });


                } catch (JSONException e) {

                    Log.e("QueryUtils", "Problem parsing the friends JSON results", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                progress.dismiss();
                Toast.makeText(getActivity(), "Error Loading Friends", Toast.LENGTH_SHORT).show();

            }
        });

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getFragmentManager().findFragmentByTag("MyFragment") != null) {
            getFragmentManager().findFragmentByTag("MyFragment").setRetainInstance(true);

        }
    }
    @Override
    public void onResume() {
        super.onResume();

        getUpdateFriends(UPDATE_FRIENDS);

        if (flag==1) {

            progress.show();

            friendAdapter.clear();
            al_name.clear();
            al_points.clear();

            Log.d("ON RESUME() ", "kkkkkkkkkk");

            getUpdateFriends(UPDATE_FRIENDS);

            getAddedFriends(REQUEST_GET_FRIENDS2);

        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    public static class MyDialogFragment extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            LayoutInflater inflaterr = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View viewtemplelayout = inflaterr.inflate(R.layout.popup_layout, null);
//            ImageView i = (ImageView) viewtemplelayout.findViewById(R.id.image);//and set image to image view
//
//            mAdapter = new MoviesAdapter(movieList);
//
//
//            recyclerView.setHasFixedSize(true);
//
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), HORIZONTAL, true));
//            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
//
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.setAdapter(mAdapter);
//
//            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
//                @Override
//                public void onClick(View view, int position) {
//
//                    Movie movie = movieList.get(position);
//                    Toast.makeText(getActivity().getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onLongClick(View view, int position) {
//
//                }
//            }));
//            //      simpleList = (ListView)viewtemplelayout.findViewById(R.id.mylist);
////            simpleList.setAdapter(mAdapter);
//
//            // mAdapter.notifyDataSetChanged();
//
//
//            prepareMovieData();
//
////
////            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                @Override
////                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
////                }
////            });
//
//
//            builder.setView(viewtemplelayout);//add your view to alert dilaog
//
//            builder.setIcon(R.drawable.avatars);
//            FriendFragment f = new FriendFragment();
//            builder.setTitle(f.name);
//            builder.setMessage("Points=" + f.points);
//
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dismiss();
//                }
//            });
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dismiss();
//                }
//            });
//
//            return builder.create();
//        }
//
//        private void prepareMovieData() {
////            Movie movie = new Movie("Mad Max: Fury Road", R.drawable.avatars);//, "Action & Adventure", "2015");
////            movieList.add(movie);
////
////            movie = new Movie("Inside Out", R.drawable.arrow_right);//, "Animation, Kids & Family", "2015");
////            movieList.add(movie);
////
////            movie = new Movie("Star Wars: Episode VII - The Force Awakens", R.drawable.b5friends);//, "Action", "2015");
////            movieList.add(movie);
////
////            movie = new Movie("Shaun the Sheep", R.drawable.friends);//, "Animation", "2015");
////            movieList.add(movie);
////
////            movie = new Movie("The Martian", R.drawable.true_btn);//, "Science Fiction & Fantasy", "2015");
////            movieList.add(movie);
////
////            movie = new Movie("Mission: Impossible Rogue Nation", R.drawable.dd_logo);//, "Action", "2015");
////            movieList.add(movie);
////
////            movie = new Movie("Up", R.drawable.eye);//, "Animation", "2009");
////            movieList.add(movie);
//
//
//            AsyncHttpClient client = new AsyncHttpClient();
//            String addbadgeurl = "http://myish.com:10011/api/addbadges/" + user_id;
//
//            client.get(getActivity(), addbadgeurl, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject responseArray) {
//                    try {
//                        callgetapi();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.d("exception_add=", String.valueOf(e));
//
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//
//                    Log.d("errorinadd=", String.valueOf(errorResponse));
//                }
//
//            });
//
//
//            mAdapter.notifyDataSetChanged();
//        }
//
//
//        public  void  callgetapi()
//        {
//            AsyncHttpClient client = new AsyncHttpClient();
//            String getbadgeurl="http://myish.com:10011/api/getbadges/"+user_id;
//
//            client.get(getActivity(),getbadgeurl, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers,JSONArray responseArray) {
//                    try {
//                        Log.d("badgekaarray", String.valueOf(responseArray));
//                        fn(responseArray);
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                        Log.d("exception_get=", String.valueOf(e));
//
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//                    Log.d("erroringet=", String.valueOf(errorResponse));
//                }
//            });
//        }
//
//        public void fn(JSONArray responseArray) {
//            Movie movie;
//            for (int i = 0; i < responseArray.length(); i++) {
//                JSONObject object,object1;
//                String badgedescription1 = null;
//                String badgename1 = null;
//                String badgeimage1= null;
//
//                try {
//                    object = responseArray.getJSONObject(i);
//                    JSONArray badges = object.getJSONArray("badges");
//
//                    Log.d("badgekaarray", String.valueOf(badges));
//
//                    if (badges.length() == 0) {
//                        Log.e("NO BADGES : ", "");
//                    }
//                    else {
//
//                        for (int j = 0; j < badges.length(); j++) {
//                            object1 = badges.getJSONObject(j);
//
//                            badgename1 = object1.getString("badgename");
//                            badgedescription1 = object1.getString("badgedescription");
//                            badgeimage1 = object1.getString("badgeimage");
//                            String url="http://myish.com:10011/images/badgeserver/"+badgeimage1;
//
//                            Log.d("pic url=",url);
//
//                            movie = new Movie("The Martian",url);//, "Science Fiction & Fantasy", "2015");
//                            movieList.add(movie);
//                        }
//                    }
//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }

}