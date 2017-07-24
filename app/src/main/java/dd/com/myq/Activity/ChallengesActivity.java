package dd.com.myq.Activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dd.com.myq.Fragment.Challenges.Challenge;
import dd.com.myq.Fragment.Challenges.ChallengeAdapter;
import dd.com.myq.R;
import dd.com.myq.Util.Questions.Question;
import dd.com.myq.Util.SessionManager;

public class ChallengesActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory {

    public static String GET_CHALLENGES_QUESTIONS_URL_PART_1 = "http://myish.com:10011/api/questions_challenges?chnumber=";
    public static String GET_CHALLENGES_URL_PART_2 = "&userId=";
    public static String GET_ALL_CHALLEGES =  "http://myish.com:10011/api/challenges";
    private String REPORT_REQUEST_URL = "http://myish.com:10011/api/questions/report";
    private String ADD_USER_TO_CHALLENGE = "http://myish.com:10011/api/adduserstochallenges";
    private String ADD_USER_TO_QUESTION  = "http://myish.com:10011/api/cquestions/answer";
    private TextView UserName;
    private String CHALLENGE_RANK_POINTS_URL = "http://myish.com:10011/api/rankuser?userid=";

    private ArrayList<String> al = new ArrayList<String>();
    private ArrayList<String> al_id = new ArrayList<String>();
    private ArrayList<String> al_correctAns = new ArrayList<String>();

    public static int index = 0;
    private static int add_flag = 0, flag;
    private static int another_flag = 0;
    int j = 0;
    ArrayAdapter<String> arrayAdapter;
    TextView text;

    int report_flag = 1;
    private int i;
    List<Question> questions;
    Button button_left, button_right;

    TextView points_view ;
    TextView rank_view ;
    TextView rank_total_view;

    ProgressDialog progress;
    public ArrayList<String> al_challenge_name = new ArrayList<String>();
    public ArrayList<String> al_challenge_number = new ArrayList<String>();
    public ArrayList<String> al_challenge_image = new ArrayList<String>();
    public ArrayList<String> al_start_time = new ArrayList<>();
    public ArrayList<String> al_end_time = new ArrayList<>();
    public ArrayList<String> al_challenge_id = new ArrayList<>();
    public ArrayList<String> al_challenge_started = new ArrayList<>();

    private TextSwitcher mSwitcher;
    Animation inAnim;
    Animation outAnim;

    ArrayList<Challenge> challenges;
    ListView listView;
    private ChallengeAdapter challengeAdapter;
    SessionManager currentSession;
    public static String challenge_number, challenge_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        points_view = (TextView) findViewById(R.id.challenge_points);
        rank_view = (TextView) findViewById(R.id.rank_textView);
        rank_total_view = (TextView) findViewById(R.id.rank_total);
        currentSession = new SessionManager(this);

        HashMap<String, String> user = currentSession.getUserDetails();
        UserName = (TextView) findViewById(R.id.user_name);
        UserName.setText(user.get(SessionManager.KEY_USERNAME));

        challenge_number = getIntent().getStringExtra("chnumber");
        challenge_id = getIntent().getStringExtra("chId");
//        Toast.makeText(getApplicationContext(),String.valueOf(challenge_number), Toast.LENGTH_SHORT).show();

        HashMap<String, String> user_details = currentSession.getUserDetails();
        final String user_id = user_details.get(SessionManager.KEY_UID);

        String user_email = user_details.get(SessionManager.KEY_EMAIL);
        String user_name = user_details.get(SessionManager.KEY_USERNAME);

        CHALLENGE_RANK_POINTS_URL = CHALLENGE_RANK_POINTS_URL + user_id +"&challengeid=" + challenge_id;
        GET_CHALLENGES_QUESTIONS_URL_PART_1 = GET_CHALLENGES_QUESTIONS_URL_PART_1 + challenge_number +
                "&userId=" + user_id;

        addUsersToChallenge(user_id, challenge_id, user_name, user_email, "", "");

        mSwitcher = (TextSwitcher)findViewById(R.id.challenges_switcher);
        mSwitcher.setFactory(this);
        inAnim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        outAnim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

        inAnim.setDuration(500);
        outAnim.setDuration(200);
        mSwitcher.setInAnimation(inAnim);
        mSwitcher.setOutAnimation(outAnim);

        text = (TextView) findViewById(R.id.challenges_questionText);
        questions = new ArrayList<>();

        button_left = (Button)findViewById(R.id.challenges_false_button);
        button_left.setBackgroundResource(R.drawable.false_btn);
        button_right = (Button)findViewById(R.id.challenges_true_button);
        button_right.setBackgroundResource(R.drawable.true_btn);

        flag = 0;
        another_flag = 0;
        report_flag = 1;

        //Just to fetch the user's data in the challenge
        UpdateChallengePoints();

        progress = new ProgressDialog(this,  ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progress.setMessage("Loading Questions...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
        challenges= new ArrayList<>();

        getChallengeQuestions(GET_CHALLENGES_QUESTIONS_URL_PART_1);

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

                        UpdateChallengePoints();
                        getChallengeQuestions(GET_CHALLENGES_QUESTIONS_URL_PART_1);
                        //Just to fetch the user's data in the challenge
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
                        UpdateChallengePoints();
                        getChallengeQuestions(GET_CHALLENGES_QUESTIONS_URL_PART_1);

                    }
                    try {
                        mSwitcher.setText(al.get(0));

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView report = (TextView)findViewById(R.id.challenges_report_question);
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChallengesActivity.this);
                    builder.setTitle("Report This Question:");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            HashMap<String, String> user_details = currentSession.getUserDetails();
                            String user_id = user_details.get(SessionManager.KEY_UID);

                            ReportAPI(user_id, al_id.get(0));

                            final Toast toast = Toast.makeText(getApplicationContext(), "Question Reported", Toast.LENGTH_SHORT);
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

    }

    public void addUsersToChallenge(String userid, String challenge_id, String user_name, String email_address,
                                    String profilepictureURL, String points){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", userid);
        requestParams.put("challengeid", challenge_id);
        requestParams.put("username", user_name);
        requestParams.put("emailaddress", email_address);
        requestParams.put("profilepictureURL", "0");
        requestParams.put("points", 0);

        client.post(getApplicationContext(),ADD_USER_TO_CHALLENGE, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("REPORT Success",response.toString());
                add_flag = 10;
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("REPORT Error",errorResponse.toString());
            }
        });

    }

    public void ReportAPI(String userid, String questionid){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", userid);
        requestParams.put("qid", questionid);

        client.post(getApplicationContext(),REPORT_REQUEST_URL, requestParams, new JsonHttpResponseHandler() {
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


    public void AddQuestion(String questionid,String userid, String questioncorrectanswer, String answercorrectness){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("uid", userid);
        requestParams.put("qid", questionid);
        requestParams.put("questioncorrectanswer", questioncorrectanswer);
        requestParams.put("answercorrectness", answercorrectness);

        client.post(getApplicationContext(),ADD_USER_TO_QUESTION, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("Add User TO Question: ", response.toString());
                UpdateChallengePoints();

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ResponsePoint Error",errorResponse.toString());
            }
        });
    }


    public void getChallengeQuestions(String QUESTIONS_REQUEST_URL){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, QUESTIONS_REQUEST_URL , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                try {
                    for(int i=0;i<responseArray.length();i++){

                        JSONObject currentNews = responseArray.getJSONObject(i);
                        String text = currentNews.getString("text");
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

                    mSwitcher.setText(" ");
                    Toast.makeText(getApplicationContext(), "Well Done, Challenge completed!!", Toast.LENGTH_SHORT).show();
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

            final Toast toast = Toast.makeText(this, correctness, Toast.LENGTH_SHORT);
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
            correctness = "incorrect";

            final Toast toast = Toast.makeText(this, correctness, Toast.LENGTH_SHORT);
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

            AddQuestion( al_id.get(0),user_id, al_correctAns.get(0), correctness);
            UpdateChallengePoints();

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
            final Toast toast = Toast.makeText(this, correctness, Toast.LENGTH_SHORT);
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

            correctness = "incorrect";

            final Toast toast = Toast.makeText(this, correctness, Toast.LENGTH_SHORT);
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
            AddQuestion( al_id.get(0),user_id, al_correctAns.get(0), correctness);
            UpdateChallengePoints();
            al.remove(0);
            al_id.remove(0);
            al_correctAns.remove(0);

        }catch(IndexOutOfBoundsException e){
        }
    }

    public void UpdateChallengePoints(){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, CHALLENGE_RANK_POINTS_URL , new JsonHttpResponseHandler() {

            String rank, total_members, points;
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseObject) {
                try {

                    Log.e("POINTS: RANK: ", String.valueOf(responseObject));

                    rank = responseObject.getString("Rank");
                    total_members = responseObject.getString("Total_Members");
                    points = responseObject.getString("Points");

                    if(rank.equals("1")){

                        ImageView medal_bronze = (ImageView) findViewById(R.id.medal_bronze);
                        medal_bronze.setVisibility(View.INVISIBLE);
                        ImageView medal_silver = (ImageView) findViewById(R.id.medal_silver);
                        medal_silver.setVisibility(View.INVISIBLE);
                        ImageView medal_gold = (ImageView) findViewById(R.id.medal_gold);
                        medal_gold.setVisibility(View.VISIBLE);

                    }else
                        if(rank.equals("2")){

                            ImageView medal_gold = (ImageView) findViewById(R.id.medal_gold);
                            medal_gold.setVisibility(View.INVISIBLE);
                            ImageView medal_bronze = (ImageView) findViewById(R.id.medal_bronze);
                            medal_bronze.setVisibility(View.INVISIBLE);
                            ImageView medal_silver = (ImageView) findViewById(R.id.medal_silver);
                            medal_silver.setVisibility(View.VISIBLE);

                        }else
                            if(rank.equals("3")){

                                ImageView medal_gold = (ImageView) findViewById(R.id.medal_gold);
                                medal_gold.setVisibility(View.INVISIBLE);
                                ImageView medal_silver = (ImageView) findViewById(R.id.medal_silver);
                                medal_silver.setVisibility(View.INVISIBLE);
                                ImageView medal_bronze = (ImageView) findViewById(R.id.medal_bronze);
                                medal_bronze.setVisibility(View.VISIBLE);

                            }

                    points_view.setText(points);
                    rank_view.setText(rank);
                    rank_total_view.setText(total_members);

                    button_right.setEnabled(true);
                    button_left.setEnabled(true);


                } catch (JSONException e){
                    Log.e("QueryUtils", "Problem parsing the Questions JSON results", e);
                }


            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        GET_CHALLENGES_QUESTIONS_URL_PART_1 = "http://myish.com:10011/api/questions_challenges?chnumber=";
        REPORT_REQUEST_URL = "http://myish.com:10011/api/questions/report";
        ADD_USER_TO_CHALLENGE = "http://myish.com:10011/api/adduserstochallenges";
        ADD_USER_TO_QUESTION  = "http://myish.com:10011/api/cquestions/answer";
        CHALLENGE_RANK_POINTS_URL = "http://myish.com:10011/api/rankuser?userid=";
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public View makeView() {
        TextView t = new TextView(getApplicationContext());;
        t.setGravity(Gravity.CENTER);
        t.setTextSize(24);
        t.setTextColor(Color.GRAY);
        return t;
    }
}
