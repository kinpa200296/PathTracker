package com.pathtracker.android.tracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class AddPathRecyclerViewAdapter extends RecyclerView.Adapter<AddPathRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final AddPathFragment.OnAddPathInteractionListener mListener;

    public AddPathRecyclerViewAdapter(List<String> items, AddPathFragment.OnAddPathInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mFileName.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onAddPathInteraction(holder.getLayoutPosition(), AddPathFragment.CODE_FILE_ADD);
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
        public final TextView mFileName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFileName = (TextView) view.findViewById(R.id.deviceFileName);
        }

        @Override
        public String toString() {
            return super.toString() + " " + mFileName.getText() + "(travel path)";
        }
    }
}
