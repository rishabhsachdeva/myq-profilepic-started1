package dd.com.myq.Fragment.Challenges;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import dd.com.myq.R;

public class ChallengeAdapter extends ArrayAdapter<Challenge> {

    private ArrayList<Challenge> dataSet;
    Context mContext;
    private static class ViewHolder {
        TextView challenge_name;
        ImageView start_challenge_button;
        ImageView challenge_image;
        TextView start_time;
        TextView end_time;
    }
    public ChallengeAdapter(ArrayList<Challenge> data, Context context) {

        super(context, R.layout.challenges_list, data);
        this.dataSet = data;
        this.mContext = context;
    }
    private int lastPosition = -1;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Challenge challenge = getItem(position);
        Log.d("Postition: ", String.valueOf(position));
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.challenges_list, parent, false);

            viewHolder.challenge_name = (TextView) convertView.findViewById(R.id.challengeName);
            viewHolder.challenge_image = (ImageView) convertView.findViewById(R.id.challenge_image);
            viewHolder.start_time = (TextView) convertView.findViewById(R.id.startTime);
            viewHolder.end_time = (TextView) convertView.findViewById(R.id.endTime);
            viewHolder.start_challenge_button = (ImageView) convertView.findViewById(R.id.start_challenge_buttpn);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.challenge_name.setText(challenge.getName());
        if(challenge.getIsChallengeStarted() == String.valueOf(1)){

            viewHolder.challenge_image.setImageResource(R.drawable.challenge_iconmdpi);
        }else
        if(challenge.getIsChallengeStarted() == String.valueOf(0)){

            viewHolder.challenge_image.setImageResource(R.drawable.challenge_icon_greymdpi);
        }


        if(challenge.getIsChallengeStarted() == String.valueOf(1)){

            viewHolder.start_challenge_button.setImageResource(R.drawable.start_challenge_xhdpi);
        }else
        if(challenge.getIsChallengeStarted() == String.valueOf(0)){

            viewHolder.start_challenge_button.setImageResource(R.drawable.start_challenge_greyxhdpi);
        }
        viewHolder.start_time.setText(challenge.getStart_time());
        viewHolder.end_time.setText(challenge.getEnd_time());
        return convertView;
    }
}
