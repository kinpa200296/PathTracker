package com.pathtracker.android.tracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class DeviceComFragment extends Fragment implements View.OnClickListener {

    public static final byte STATUS_START = 1;
    public static final byte STATUS_STOP = 3;
    public static final byte STATUS_PAUSE = 2;

    Button btnStart, btnPause, btnStop;
    TextView tvStatus;

    private OnDeviceCommunicationListener mListener;

    public DeviceComFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_com, container, false);
        btnStart = (Button) view.findViewById(R.id.start_new_path);
        btnStart.setOnClickListener(this);
        btnPause = (Button) view.findViewById(R.id.pause_path);
        btnPause.setOnClickListener(this);
        btnStop = (Button) view.findViewById(R.id.stop_path);
        btnStop.setOnClickListener(this);
        tvStatus = (TextView) view.findViewById(R.id.path_status);
        updateStatus();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeviceCommunicationListener) {
            mListener = (OnDeviceCommunicationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeviceCommunicationListener");
        }
    }

    private void updateStatus(){
        //button and tv update methods are held here
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_new_path:
                mListener.onPathStatusChange(STATUS_START);
                break;
            case R.id.pause_path:
                mListener.onPathStatusChange(STATUS_PAUSE);
                break;
            case R.id.stop_path:
                mListener.onPathStatusChange(STATUS_STOP);
        }
    }


    public interface OnDeviceCommunicationListener {
        void onPathStatusChange(byte new_status);
    }
}
