package dd.com.myq.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dd.com.myq.App.Config;
import dd.com.myq.R;
import dd.com.myq.Util.Questions.Question;
import dd.com.myq.Util.Questions.QuestionAdapter;
import dd.com.myq.Util.SessionManager;

public class HomeFragment extends Fragment implements ViewSwitcher.ViewFactory {

    private static final String ARG_PARAM1 = "flag";
    private static final String ARG_PARAM2 = "levels";
    private static final String ARG_PARAM3 = "categories";

    private ArrayList<String> al = new ArrayList<String>();
    private ArrayList<String> al_id = new ArrayList<String>();
    private ArrayList<String> al_correctAns = new ArrayList<String>();

    public static int index = 0;
    private static int flag;
    private static int another_flag = 0;

    private TextSwitcher mSwitcher;
    Animation inAnim;
    Animation outAnim;
    ProgressDialog progress;

    int report_flag = 1;
    private int i;
    List<Question> questions;
    Button button_left, button_right;

    Context mContext;
    public QuestionAdapter questionAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    TextView text;
    SessionManager currentSession;
    private static final int QUESTION_LOADER_ID = 1;

    private TextView emptyTextView;
    private static final String LOG_TAG = HomeFragment.class.getName();
    private String QUESTIONS_REQUEST_URL = "http://myish.com:10011/api/questions/";

    private String REPORT_REQUEST_URL = "http://myish.com:10011/api/questions/report";
    private String ANOTHER_QUESTION_URL;
    private TextView mEmptyStateTextView;
    private OnFragmentInteractionListener mListener;

    View view;
    public HomeFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2, String param3) {

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
        currentSession = new SessionManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        HashMap<String, String> user_details = currentSession.getUserDetails();

        final String user_id = user_details.get(SessionManager.KEY_UID);
        QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;

        mSwitcher = (TextSwitcher) view.findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
        inAnim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
        outAnim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);

        inAnim.setDuration(500);
        outAnim.setDuration(200);
        mSwitcher.setInAnimation(inAnim);
        mSwitcher.setOutAnimation(outAnim);

        text = (TextView) view.findViewById(R.id.questionText);
        questions = new ArrayList<>();

        button_left = (Button) view.findViewById(R.id.false_button);
        button_left.setBackgroundResource(R.drawable.false_btn);
        button_right = (Button) view.findViewById(R.id.true_button);
        button_right.setBackgroundResource(R.drawable.true_btn);

        flag = 0;
        another_flag = 0;
        report_flag = 1;

        progress = new ProgressDialog(getContext(),  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progress.setMessage("Loading Questions...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        if (mParam2 == null && mParam3 == null) {

            getQuestions(QUESTIONS_REQUEST_URL);
            Log.e("Params NULL","...");

        } else if (mParam2 != null && !mParam2.isEmpty()) {

            Log.e("mPARAM: 2",mParam2);

            String qapi = "http://myish.com:10011/api/questions_levels?";

            if (mParam2.equals("E")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=E";

            } else if (mParam2.equals("M")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=M";

            } else if (mParam2.equals("H")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=H";

            } else {
                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
            }
            mParam2 = null;

            if (flag == 0) {
                getQuestions(QUESTIONS_REQUEST_URL);

            }
        } else if (mParam3 != null && !mParam3.isEmpty()) {

            Log.e("mParam3 : ", mParam3);

            String qapi = "http://myish.com:10011/api/questions_category?";
            if (mParam3.equals("PHP")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=PHP";
            } else if (mParam3.equals("C")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C";
            } else if (mParam3.equals("SOFTWARE ENGINEERING")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=software";
            }
            else if (mParam3.equals("CLOUD COMPUTING")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=CLOUD%20COMPUTING";
            } else if (mParam3.equals("ROBOTICS AND AI")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=ROBOTICS%20AND%20AI";
            } else if (mParam3.equals("CYBER SECURITY")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=%20CYBER%20SECURITY";
            } else if (mParam3.equals("C++")) {
                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C%2B%2B";
            } else {
                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
            }
            mParam3 = null;
            if (flag == 0) {

                getQuestions(QUESTIONS_REQUEST_URL);
            }
        }

        try {
            button_right.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    try {
                        button_right.setEnabled(false);
                        button_left.setEnabled(false);

                        checkQuestionRight(0);

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    if (al.size() == 0) {
                        flag = 10;
                        progress.show();

                        if (mParam2 == null && mParam3 == null) {

                            getQuestions(QUESTIONS_REQUEST_URL);
                            Log.e("GETTINGS MORE ","NORMAL QUESTIONS");

                        }else if (mParam2 != null && !mParam2.isEmpty()) {

                            Log.e("mParam2 : ", mParam2);
                            String qapi = "http://myish.com:10011/api/questions_levels?";

                            if (mParam2.equals("E")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=E";
                            } else if (mParam2.equals("M")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=M";
                            } else if (mParam2.equals("H")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=H";
                            } else {
                                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
                            }
                            mParam2 = null;

                            if (flag == 0) {
                                getQuestions(QUESTIONS_REQUEST_URL);
                            }

                        }else if (mParam3 != null && !mParam3.isEmpty()) {

                            Log.e("mParam3 : ", mParam3);
                            String qapi = "http://myish.com:10011/api/questions_category?";

                            if (mParam3.equals("PHP")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=PHP";
                            } else if (mParam3.equals("C")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C";
                            } else if (mParam3.equals("SOFTWARE ENGINEERING")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=software";
                            }
                            else if (mParam3.equals("CLOUD COMPUTING")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=CLOUD%20COMPUTING";
                            } else if (mParam3.equals("ROBOTICS AND AI")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=ROBOTICS%20AND%20AI";
                            } else if (mParam3.equals("CYBER SECURITY")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=%20CYBER%20SECURITY";
                            } else if (mParam3.equals("C++")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C%2B%2B";
                            } else {
                                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
                            }
                            mParam3 = null;
                            if (flag == 0) {
                                getQuestions(QUESTIONS_REQUEST_URL);
                            }
                        }
                    }

                    try {

                        mSwitcher.setText(al.get(0));

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });

            button_left.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    try {
                        button_right.setEnabled(false);
                        button_left.setEnabled(false);

                        checkQuestionLeft(0);

                    } catch (IndexOutOfBoundsException e) {
                    }

                    if (al.size() == 0) {
                        flag = 10;
                        progress.show();

                        if (mParam2 == null && mParam3 == null) {

                            getQuestions(QUESTIONS_REQUEST_URL);
                            Log.e("GETTINGS MORE ","NORMAL QUESTIONS");

                        }else if (mParam2 != null && !mParam2.isEmpty()) {

                            Log.e("mParam2 : ", mParam2);
                            String qapi = "http://myish.com:10011/api/questions_levels?";

                            if (mParam2.equals("E")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=E";
                            } else if (mParam2.equals("M")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=M";
                            } else if (mParam2.equals("H")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&level=H";
                            } else {
                                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
                            }
                            mParam2 = null;

                            if (flag == 0) {

                                getQuestions(QUESTIONS_REQUEST_URL);
                            }

                        }else if (mParam3 != null && !mParam3.isEmpty()) {

                            Log.e("mParam3 : ", mParam3);

                            String qapi = "http://myish.com:10011/api/questions_category?";

                            if (mParam3.equals("PHP")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=PHP";
                            } else if (mParam3.equals("C")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C";
                            } else if (mParam3.equals("SOFTWARE ENGINEERING")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=software";
                            }
                            else if (mParam3.equals("CLOUD COMPUTING")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=CLOUD%20COMPUTING";
                            } else if (mParam3.equals("ROBOTICS AND AI")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=ROBOTICS%20AND%20AI";
                            } else if (mParam3.equals("CYBER SECURITY")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=%20CYBER%20SECURITY";
                            } else if (mParam3.equals("C++")) {
                                QUESTIONS_REQUEST_URL = qapi + "userId=" + user_id + "&category=C%2B%2B";
                            } else {
                                QUESTIONS_REQUEST_URL = QUESTIONS_REQUEST_URL + user_id;
                            }
                            mParam3 = null;

                            if (flag == 0) {
                                getQuestions(QUESTIONS_REQUEST_URL);
                            }
                        }
                    }
                    try {
                        mSwitcher.setText(al.get(0));

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView report = (TextView) view.findViewById(R.id.report_question);
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Report This Question:");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            HashMap<String, String> user_details = currentSession.getUserDetails();
                            String user_id = user_details.get(SessionManager.KEY_UID);

                            ReportAPI(user_id, al_id.get(0));

                            final Toast toast = Toast.makeText(getActivity(),
                                    "Question Reported", Toast.LENGTH_SHORT);
                            toast.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 500);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        Log.e("RETURNEDD ", String.valueOf(al.size()));
        return view;
    }

    public void ReportAPI(String userid, String questionid){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", userid);
        requestParams.put("qid", questionid);

        client.post(getActivity(),REPORT_REQUEST_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("REPORT Success",response.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("REPORT Error",errorResponse.toString());
            }
        });
    }

    public void AddQuestion(String userid,String questionid, String questiontext, String questionimage, String questioncorrectanswer, String questionpoints, String answercorrectness){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("questionid", questionid);
        requestParams.put("questiontext", questiontext);
        requestParams.put("questionimage", questionimage);
        requestParams.put("questioncorrectanswer", questioncorrectanswer);
        requestParams.put("questionpoints", questionpoints);
        requestParams.put("answercorrectness", answercorrectness);

        client.post(getActivity(), Config.AddQuestionUrl , requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("ResponsePoint Success",response.toString());

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());
            }
        });
    }

    public void AddUserToQuestion(String uid,String qid, String questioncorrectanswer){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", uid);
        requestParams.put("qid", qid);
        requestParams.put("answer", questioncorrectanswer);

        client.post(getActivity(), Config.AddQuestionToUser, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.e("Add User TO Question: ", response.toString());
                button_right.setEnabled(true);
                button_left.setEnabled(true);

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());
            }
        });
    }

    public void getQuestions(String QUESTIONS_REQUEST_URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), QUESTIONS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                try {
                    for(int i=0;i<responseArray.length();i++){

                        JSONObject currentNews = responseArray.getJSONObject(i);
                        String text = currentNews.getString("text");
                        String level = currentNews.getString("level");
                        String id = currentNews.getString("_id");
                        String correctAnswer = currentNews.getString("correctAnswere");
                        al_id.add(id);
                        al.add(text);
                        al_correctAns.add(correctAnswer);
                        Log.d("QUESTIONS: "+ String.valueOf(i), text);
                    }

                    Log.e("al.size():GETqUSTONS() ", String.valueOf(al.size()));
                    progress.dismiss();

                } catch (JSONException e){
                    Log.e("QueryUtils", "Problem parsing the Questions JSON results", e);
                }

                try {

                    mSwitcher.setText(al.get(0));

                }catch(IndexOutOfBoundsException e){
                    mSwitcher.setText("");
                    Toast.makeText(getActivity(), "Well done, you've answered all the questions.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
            }
        });
    }

    public void checkQuestionRight(int another_index){

        HashMap<String, String> user_details = currentSession.getUserDetails();
        String user_id = user_details.get(SessionManager.KEY_UID);

        String correctness;
        if (al_correctAns.get(0).equals("YES")) {

            correctness = "Correct";

            final Toast toast = Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.circle_orange);
            toastView.setPadding(30,30,30,30);

            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 900);

        } else {
            correctness = "Incorrect";

            final Toast toast = Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.circle_purple);
            toastView.setPadding(30,30,30,30);

            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 900);
        }
        try {

            AddQuestion(user_id, al_id.get(0), al.get(0), "", al_correctAns.get(0), "2", correctness);
            AddUserToQuestion(user_id, al_id.get(0), al_correctAns.get(0));

            al.remove(0);
            al_id.remove(0);
            al_correctAns.remove(0);

        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void checkQuestionLeft(int another_index){

        HashMap<String, String> user_details = currentSession.getUserDetails();
        String user_id = user_details.get(SessionManager.KEY_UID);
        String correctness;
        if (al_correctAns.get(0).equals("NO")) {

            correctness = "Correct";

            final Toast toast = Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.circle_orange);
            toastView.setPadding(30,30,30,30);

            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 900);

        } else {

            correctness = "Incorrect";
            final Toast toast = Toast.makeText(getActivity(), correctness, Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            toastView.setBackgroundResource(R.drawable.circle_purple);
            toastView.setPadding(30,30,30,30);

            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 900);


        }
        try {
            AddQuestion(user_id, al_id.get(0), al.get(0), "", al_correctAns.get(0), "2", correctness);
            AddUserToQuestion(user_id, al_id.get(0), al_correctAns.get(0));

            al.remove(0);
            al_id.remove(0);
            al_correctAns.remove(0);

        }catch(IndexOutOfBoundsException e){

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public View makeView() {
        TextView t = new TextView(getContext());;
        t.setGravity(Gravity.CENTER);
        t.setTextSize(24);
        t.setTextColor(Color.GRAY);
        return t;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}