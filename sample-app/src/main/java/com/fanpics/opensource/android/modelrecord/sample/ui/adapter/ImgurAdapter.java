package com.fanpics.opensource.android.modelrecord.sample.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanpics.opensource.android.modelrecord.sample.R;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImgurAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private List<ImgurItem> items;

    public ImgurAdapter(LayoutInflater inflater, Context context) {
        items = new ArrayList<>();
        this.inflater = inflater;
        this.context = context;
    }

    public void setImgurItems(List<ImgurItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createView(parent);
        }

        return setupView(position, convertView);
    }

    private View createView(ViewGroup parent) {
        final View view = inflater.inflate(R.layout.list_item_imgur, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    private View setupView(int position, View convertView) {
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final ImgurItem item = items.get(position);

        Picasso.with(context).load(item.getImageUrl()).into(holder.image);
        holder.title.setText(item.getTitle());

        return convertView;
    }

    private class ViewHolder {
        public final ImageView image;
        public final TextView title;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.image);
            this.title = (TextView) view.findViewById(R.id.title);
        }
    }
}
