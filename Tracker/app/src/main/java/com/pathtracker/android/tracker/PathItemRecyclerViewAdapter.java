package com.pathtracker.android.tracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pathtracker.android.tracker.PathItemFragment.OnListFragmentInteractionListener;
import com.pathtracker.android.tracker.database.PathDataContent.PathRecord;

import java.util.List;


public class PathItemRecyclerViewAdapter extends RecyclerView.Adapter<PathItemRecyclerViewAdapter.ViewHolder> {

    private final List<PathRecord> mValues;
    private final OnListFragmentInteractionListener mListener;

    public PathItemRecyclerViewAdapter(List<PathRecord> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_path_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mPathRecord = mValues.get(position);
        holder.mName.setText(mValues.get(position).name);
        holder.mDate.setText(mValues.get(position).startDate);
        holder.mDescription.setText(mValues.get(position).description);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mPathRecord, PathItemFragment.CODE_ITEM_OPEN);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }



    public void removeItem(int position){
        mValues.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final TextView mName;
        public final TextView mDescription;
        public final TextView mDate;
        public PathRecord mPathRecord;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.pathName);
            mDescription = (TextView) view.findViewById(R.id.pathDescription);
            mDate = (TextView) view.findViewById(R.id.pathDate);
        }

        @Override
        public String toString() {
            return super.toString() + " " + mName.getText() + "(travel path)";
        }
    }
}
