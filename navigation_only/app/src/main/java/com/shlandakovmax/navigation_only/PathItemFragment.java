package com.shlandakovmax.navigation_only;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shlandakovmax.navigation_only.database.PathDataContent;
import com.shlandakovmax.navigation_only.database.PathDataContent.PathRecord;

public class PathItemFragment extends Fragment {

    public final static int CODE_ITEM_DELETE = 3;
    public final static int CODE_ITEM_EDIT = 2;
    public final static int CODE_ITEM_OPEN = 1;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    PathDataContent dataContent;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public PathItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PathItemFragment newInstance(int columnCount) {
        PathItemFragment fragment = new PathItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_path_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final PathItemRecyclerViewAdapter adapter = new PathItemRecyclerViewAdapter(PathDataContent.records, mListener);
            recyclerView.setAdapter(adapter);

            ItemTouchHelper.SimpleCallback scb = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.LEFT){
                        PathItemRecyclerViewAdapter.ViewHolder pathHolder = (PathItemRecyclerViewAdapter.ViewHolder) viewHolder;
                        mListener.onListFragmentInteraction(pathHolder.mPathRecord, CODE_ITEM_EDIT);
                    }
                    else if (direction == ItemTouchHelper.RIGHT){
                        adapter.removeItem(viewHolder.getAdapterPosition());
                        PathItemRecyclerViewAdapter.ViewHolder pathHolder = (PathItemRecyclerViewAdapter.ViewHolder) viewHolder;
                        mListener.onListFragmentInteraction(pathHolder.mPathRecord, CODE_ITEM_DELETE);
                    }
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    View itemView = viewHolder.itemView;
                    Log.d("len:", String.valueOf(itemView.getWidth()- (int)dX));
                    Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_remove);
                    if (dX < 190)
                        d.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX , itemView.getBottom());
                    else
                        d.setBounds(itemView.getLeft(), itemView.getTop(), 180, itemView.getBottom());
                    Drawable d1 = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_edit);
                    if (dX > - 190)
                        d1.setBounds(itemView.getWidth()+ (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    else d1.setBounds(itemView.getWidth() - 180, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    d1.draw(c);
                    d.draw(c);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

            ItemTouchHelper touchHelper = new ItemTouchHelper(scb);
            touchHelper.attachToRecyclerView((RecyclerView) view);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PathRecord item, int interact_code);
    }
}
