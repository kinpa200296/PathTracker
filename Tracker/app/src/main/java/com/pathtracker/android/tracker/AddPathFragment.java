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
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

public class AddPathFragment extends Fragment {

    public final static int CODE_FILE_DELETE = 3;
    public final static int CODE_FILE_ADD = 1;

    public static int VIEW_MODE_NO_CONNECTION = 0;
    public static int VIEW_MODE_FILE_LIST = 2;

    private static final String ARG_MODE = "mode";
    private OnAddPathInteractionListener mListener;

    List<String> files;

    public AddPathFragment() {
    }

    public static AddPathFragment newInstance(int mode) {
        AddPathFragment fragment = new AddPathFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO:определить xml файлы для списка
        Bundle args = getArguments();
        final View view;
        if (args.getInt(ARG_MODE) == VIEW_MODE_FILE_LIST){
            view = inflater.inflate(R.layout.fragment_path_item_list, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_no_device_connection_found, container, false);
        }
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //TODO: подать список элементов на обработку
            final AddPathRecyclerViewAdapter adapter = new AddPathRecyclerViewAdapter(mListener.getFiles(), mListener);
            recyclerView.setAdapter(adapter);
            ItemTouchHelper.SimpleCallback scb = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.RIGHT){
                        mListener.onAddPathInteraction(viewHolder.getAdapterPosition(), CODE_FILE_DELETE);
                        adapter.removeItem(viewHolder.getAdapterPosition());
                    }
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    if (dX >0)
                        c.drawRect(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom(),paint);
                    Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_remove);
                    if (dX < 0)
                        d.setBounds(itemView.getLeft(), itemView.getTop(), 0 , itemView.getBottom());
                    else
                        d.setBounds(itemView.getLeft(), itemView.getTop(), 180, itemView.getBottom());
                    d.draw(c);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

            ItemTouchHelper touchHelper = new ItemTouchHelper(scb);
            touchHelper.attachToRecyclerView((RecyclerView) view);


        }

        else if (view instanceof LinearLayout){
            Button btnSettings = (Button) view.findViewById(R.id.goto_settings);
            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onGotoSettingsInteraction();
                }
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddPathInteractionListener) {
            mListener = (OnAddPathInteractionListener) context;
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

    public interface OnAddPathInteractionListener {
        void onAddPathInteraction(int fileIndex, int interact_code);
        void onGotoSettingsInteraction();
        List<String> getFiles();
    }
}
