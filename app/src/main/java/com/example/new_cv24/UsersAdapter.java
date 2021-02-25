package com.example.new_cv24;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CustomViewHolder> {
    private ArrayList<TotalData> mList = null;
    private Activity context = null;

    public UsersAdapter(Activity context, ArrayList<TotalData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView num;
        protected TextView time;
        protected TextView address;

        public CustomViewHolder(View view) {
            super(view);
            this.num = (TextView) view.findViewById(R.id.textView_list_num);
            this.time = (TextView) view.findViewById(R.id.textView_list_time);
            this.address = (TextView) view.findViewById(R.id.textView_list_address);
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        viewholder.num.setText(mList.get(position).getMember_num());
        viewholder.time.setText(mList.get(position).getMember_time());
        viewholder.address.setText(mList.get(position).getMember_address());
    }
    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}
