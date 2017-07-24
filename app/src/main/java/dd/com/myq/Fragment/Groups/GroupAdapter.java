package dd.com.myq.Fragment.Groups;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dd.com.myq.R;

public class GroupAdapter extends ArrayAdapter<Group> {

    private ArrayList<Group> dataSet;
    Context mContext;
    private static class ViewHolder {

        TextView name;
        TextView count;
        TextView since;
    }

    public GroupAdapter(ArrayList<Group> data, Context context) {
        super(context, R.layout.groups_list, data);
        this.dataSet = data;
        this.mContext = context;
    }

    private int lastPosition = -1;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Group group = getItem(position);

        Log.d("PPPOOOSSSIIITTTON", String.valueOf(position));

        // Check if an existing view is being reused, otherwise inflate the view
        GroupAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.groups_list, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.group_name);
            viewHolder.count = (TextView) convertView.findViewById(R.id.group_members_count);
            viewHolder.since = (TextView) convertView.findViewById(R.id.group_since);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.name.setText(group.getName());
        viewHolder.count.setText(group.getCount());
        viewHolder.since.setText(group.getSince());

        return convertView;
    }

}