package com.pathtracker.android.tracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreatePathFragment extends Fragment implements View.OnClickListener{

    public final static int RESULT_OK = 1;
    public final static int RESULT_CANCEL = 0;
    public final static byte CALL_FOR_EDIT = 2;
    public final static byte CALL_FOR_CREATE = 3;
    public final static String BUNDLE_KEY_NAME = "name";
    public final static String BUNDLE_KEY_DESC = "description";
    public final static String BUNDLE_KEY_MODE = "mode";

    EditText etName, etDescription;
    public byte calling_method;

    private OnFragmentInteractionListener mListener;

    public CreatePathFragment() {
    }

    public static CreatePathFragment newInstance(String name, String description, byte mode) {

        Bundle args = new Bundle();
        if (name == null)
            args.putString(BUNDLE_KEY_NAME, "");
        else args.putString(BUNDLE_KEY_NAME, name);
        if (description == null)
            args.putString(BUNDLE_KEY_DESC, "");
        else args.putString(BUNDLE_KEY_DESC, description);
        args.putByte(BUNDLE_KEY_MODE, mode);
        CreatePathFragment fragment = new CreatePathFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle =  getArguments();
        View v = inflater.inflate(R.layout.fragment_create_path, container, false);

        etName = (EditText) v.findViewById(R.id.edit_name_create);
        etDescription = (EditText) v.findViewById(R.id.edit_desc_create);

        etName.setText(bundle.getString(BUNDLE_KEY_NAME));
        etDescription.setText(bundle.getString(BUNDLE_KEY_DESC));
        calling_method = bundle.getByte(BUNDLE_KEY_MODE);

        Button button = (Button) v.findViewById(R.id.btnOk_create);
        button.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.btnCancel_create);
        button.setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk_create:
                String name = etName.getText().toString();
                if (name.equals("") || name.length() > 50) return;
                String description = etDescription.getText().toString();
                if (description.length() > 500) return;
                mListener.onCreateFragmentInteraction(name, description, RESULT_OK);
                return;
            case R.id.btnCancel_create:
                mListener.onCreateFragmentInteraction(null, null, RESULT_CANCEL);
        }
        return;
    }

    public interface OnFragmentInteractionListener {
        void onCreateFragmentInteraction(String name, String description, int result_code);
    }
}
