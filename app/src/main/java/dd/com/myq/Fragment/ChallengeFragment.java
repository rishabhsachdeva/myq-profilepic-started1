package dd.com.myq.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dd.com.myq.Activity.ChallengesActivity;
import dd.com.myq.Fragment.Challenges.Challenge;
import dd.com.myq.Fragment.Challenges.ChallengeAdapter;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChallengeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFragment extends Fragment {

    View rootView;
    public static String GET_CHALLENGES_URL_PART_1 = "http://myish.com:10011/api/questions/_challenges?chnumber=";
    public static String GET_CHALLENGES_URL_PART_2 = "&userId=";
    public static String GET_ALL_CHALLEGES =  "http://myish.com:10011/api/challenges";

    ProgressDialog progress;
    public ArrayList<String> al_challenge_name = new ArrayList<String>();
    public ArrayList<String> al_challenge_number = new ArrayList<String>();
    public ArrayList<String> al_challenge_image = new ArrayList<String>();
    public ArrayList<String> al_start_time = new ArrayList<>();
    public ArrayList<String> al_end_time = new ArrayList<>();
    public ArrayList<String> al_challenge_id = new ArrayList<>();
    public ArrayList<String> al_challenge_started = new ArrayList<>();

    ArrayList<Challenge> challenges;
    ListView listView;
    public ChallengeAdapter challengeAdapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
   // private static final String ARG_PARAM3 = "ChallengeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;


    private OnFragmentInteractionListener mListener;

    public ChallengeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChallengeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChallengeFragment newInstance(String param1, String param2,String param3) {
        ChallengeFragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
     //   args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
          //  mParam3 = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_challenge, container, false);
        SessionManager currentSession = new SessionManager(getActivity());

        progress = new ProgressDialog(getContext(),  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progress.setMessage("Loading Challenges");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        listView=(ListView)rootView.findViewById(R.id.list_challenges);
        challenges= new ArrayList<>();

        getChallenges(GET_ALL_CHALLEGES);
        return rootView;
    }


    public void getChallenges(String GET_ALL_CHALLEGES){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getContext(), GET_ALL_CHALLEGES , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

                try {
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject object = responseArray.getJSONObject(i);

                        String challenge_name = object.getString("challengename");
                        String challenge_number = String.valueOf(object.getInt("challengenumber"));
                        String challenge_image = String.valueOf(object.getString("challengeimage"));
                        String challenge_id = object.getString("_id");
                        String challenge_start_time = object.getString("starttime");
                        String challenge_end_time = object.getString("endtime");
                        String isChallengeStarted = String.valueOf(object.getInt("challengeIsRecurring"));

                        Log.d("challenge name: ", challenge_name);
                        String startTime = "", endTime = "";
                        for (int j = 0; j < 10; j++) {

                            startTime += challenge_start_time.charAt(j);
                            endTime += challenge_end_time.charAt(j);
                        }
                        al_challenge_name.add(challenge_name);
                        al_challenge_number.add(challenge_number);
                        al_challenge_image.add(challenge_image);
                        al_challenge_id.add(challenge_id);
                        al_start_time.add(startTime);
                        al_end_time.add(endTime);
                        al_challenge_started.add(isChallengeStarted);
                    }
                    progress.dismiss();

                    for (int i = 0; i < al_challenge_name.size(); i++) {

                        challenges.add(new Challenge(al_challenge_name.get(i), al_challenge_number.get(i),
                                al_challenge_image.get(i),al_challenge_id.get(i), al_start_time.get(i), al_end_time.get(i), al_challenge_started.get(i)));
                    }
                    try{
                        challengeAdapter = new ChallengeAdapter(challenges, getContext());

                    }catch(NullPointerException e) {

                        e.printStackTrace();
                    }
                    listView.setAdapter(challengeAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            if(al_challenge_started.get(position).equals("1")) {
                                Intent i = new Intent(getContext(), ChallengesActivity.class);
                                i.putExtra("chnumber",String.valueOf(al_challenge_number.get(position)));
                                i.putExtra("chId", String.valueOf(al_challenge_id.get(position)));
                                startActivity(i);
                            }else{
                                Toast.makeText(getActivity(), "Challenge Ended", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                } catch (JSONException e) {
                    Log.e("QueryUtils", "Problem parsing the Challenges JSON results", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(getContext(), "Error Loading Challenges", Toast.LENGTH_SHORT).show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}