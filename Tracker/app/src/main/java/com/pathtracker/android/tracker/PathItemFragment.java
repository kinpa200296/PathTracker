package com.pathtracker.android.tracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pathtracker.android.tracker.database.PathDataContent;
import com.pathtracker.android.tracker.database.PathDataContent.PathRecord;

public class PathItemFragment extends Fragment {

    public final static int CODE_ITEM_DELETE = 3;
    public final static int CODE_ITEM_EDIT = 2;
    public final static int CODE_ITEM_OPEN = 1;

    private static final String ARG_COLUMN_COUNT = "column-count";
    PathDataContent dataContent;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public PathItemFragment() {
    }

    //TODO:may be deleted in final ver.
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

            ItemTouchHelper.SimpleCallback scb = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.LEFT) {
                        PathItemRecyclerViewAdapter.ViewHolder pathHolder = (PathItemRecyclerViewAdapter.ViewHolder) viewHolder;
                        mListener.onListFragmentInteraction(pathHolder.mPathRecord, CODE_ITEM_EDIT);
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        adapter.removeItem(viewHolder.getAdapterPosition());
                        PathItemRecyclerViewAdapter.ViewHolder pathHolder = (PathItemRecyclerViewAdapter.ViewHolder) viewHolder;
                        mListener.onListFragmentInteraction(pathHolder.mPathRecord, CODE_ITEM_DELETE);
                    }
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    if (dX > 0)
                        c.drawRect(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);
                    else if (dX < 0) {
                        paint.setColor(Color.YELLOW);
                        c.drawRect(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);
                    }
                    Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_remove);
                    if (dX < 0)
                        d.setBounds(itemView.getLeft(), itemView.getTop(), 0, itemView.getBottom());
                    else
                        d.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getBottom() - itemView.getTop(), itemView.getBottom());
                    Drawable d1 = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_edit);
                    if (dX > 0)
                        d1.setBounds(itemView.getWidth(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    else
                        d1.setBounds(itemView.getWidth() - (itemView.getBottom() - itemView.getTop()), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    if (dX < 0) {
                        d1.draw(c);
                    } else {
                        d.draw(c);
                    }
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
