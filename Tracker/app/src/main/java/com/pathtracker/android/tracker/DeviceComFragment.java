package com.pathtracker.android.tracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class DeviceComFragment extends Fragment implements View.OnClickListener {

    public static final byte STATUS_START = 1;
    public static final byte STATUS_PAUSE = 2;
    public static final byte STATUS_RESUME = 3;
    public static final byte STATUS_STOP = 4;

    Button btnStart, btnPause, btnResume, btnStop;
    TextView tvStatus;
    EditText etTag;

    private OnDeviceCommunicationListener mListener;
    private byte _currentStatus;
    private String _tag;

    public DeviceComFragment() {
        // Required empty public constructor
        _currentStatus = STATUS_STOP;
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
        btnResume = (Button) view.findViewById(R.id.resume_path);
        btnResume.setOnClickListener(this);
        btnStop = (Button) view.findViewById(R.id.stop_path);
        btnStop.setOnClickListener(this);
        tvStatus = (TextView) view.findViewById(R.id.path_status);
        etTag = (EditText) view.findViewById(R.id.edit_tag);
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

    private void updateStatus() {
        switch (_currentStatus) {
            case STATUS_PAUSE:
                btnPause.setEnabled(false);
                btnResume.setEnabled(true);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                break;
            case STATUS_RESUME:
                btnPause.setEnabled(true);
                btnResume.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                break;
            case STATUS_START:
                btnPause.setEnabled(true);
                btnResume.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                break;
            case STATUS_STOP:
                btnPause.setEnabled(false);
                btnResume.setEnabled(false);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_new_path:
                if (etTag.getText().length() > 20) {
                    Toast.makeText(getActivity(), "Tag too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mListener.onPathStatusChange(STATUS_START, etTag.getText().toString())) {
                    _currentStatus = STATUS_START;
                    _tag = etTag.getText().toString();
                    tvStatus.setText(getActivity().getString(R.string.status_writing) + " " + _tag);
                }
                break;
            case R.id.pause_path:
                if (mListener.onPathStatusChange(STATUS_PAUSE, "")) {
                    _currentStatus = STATUS_PAUSE;
                    tvStatus.setText(getActivity().getString(R.string.status_paused) + " " + _tag);
                }
                break;
            case R.id.resume_path:
                if (mListener.onPathStatusChange(STATUS_RESUME, "")) {
                    _currentStatus = STATUS_RESUME;
                    tvStatus.setText(getActivity().getString(R.string.status_writing) + " " + _tag);
                }
                break;
            case R.id.stop_path:
                if (mListener.onPathStatusChange(STATUS_STOP, "")) {
                    _currentStatus = STATUS_STOP;
                    tvStatus.setText(R.string.status_hint);
                }
        }
        updateStatus();
    }


    public interface OnDeviceCommunicationListener {
        boolean onPathStatusChange(byte new_status, String tag);
    }
}
