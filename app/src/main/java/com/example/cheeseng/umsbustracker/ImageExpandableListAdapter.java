package com.example.cheeseng.umsbustracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import static android.R.attr.value;

/**
 * Created by Chee Seng on 15-May-17.
 */

public class ImageExpandableListAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private Map<String, List<String>> groupCollections;
    private List<String> childList;

    public ImageExpandableListAdapter(Activity context, List<String> childList,
                                      Map<String, List<String>> groupCollections) {
        this.context = context;
        this.groupCollections = groupCollections;
        this.childList = childList;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return groupCollections.get(childList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childResource = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.schedule_child, null);
        }

        ImageView item = (ImageView) convertView.findViewById(R.id.schedule_child_image);
        int resourceId = context.getResources().
                getIdentifier(childResource, "drawable", context.getPackageName());

        item.setImageResource(resourceId);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return groupCollections.get(childList.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return childList.get(groupPosition);
    }

    public int getGroupCount() {
        return childList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.route_group,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.route);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
