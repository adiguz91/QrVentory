package com.example.is2.test2qrventory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.is2.test2qrventory.connection.EventAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventInfoFragment extends Fragment implements VolleyResponseListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView textViewTitle = null;
    TextView textViewDescription = null;
    Button buttonEventStart = null;
    User user = null;
    Event singleEvent = null;
    List<Item> items = new ArrayList<>();
    Domain domain;
    EventAccess eventAccess = null;

    View rootView;

    private OnFragmentInteractionListener mListener;

    public EventInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventInfoFragment newInstance(String param1, String param2) {
        EventInfoFragment fragment = new EventInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        user = getActivity().getIntent().getParcelableExtra("user");
        singleEvent = getActivity().getIntent().getParcelableExtra("event");
        //domain = getActivity().getIntent().getParcelableExtra("domain");
        //long domain_id = domain.getIdDomain();
        String userApiKey = user.getApiKey();
        eventAccess = new EventAccess(userApiKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_event_info, container, false);

        updateEvent(rootView);

        /*TabHost tabHost = (TabHost) getActivity().findViewById(android.R.id.tabhost);
        tabHost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
        tabHost.getTabWidget().getChildTabViewAt(3).setEnabled(false);*/

        return rootView;
    }

    private void updateEvent(View rootView) {
        textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) rootView.findViewById(R.id.textViewDescription);
        buttonEventStart = (Button) rootView.findViewById(R.id.button_event_start);
        buttonEventStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventStatus(singleEvent.getId(), 1);

            }
        });


        if (singleEvent.getStatus() == 1) {
            buttonEventStart.setVisibility(View.INVISIBLE);
        }


        textViewTitle.setText(singleEvent.getName());
        textViewDescription.setText(singleEvent.getDescription());
    }

    public void setEventStatus(long eventId, int status) {
        eventAccess.updateEventStatus(this, eventId, status);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onEventInfoFragmentInteraction(uri);
        }
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
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response) {

        if (response != null) {
            try {
                boolean isUpdatedStatus = (Boolean) response;
                if(isUpdatedStatus) {
                    eventAccess.getEvent(this, singleEvent.getId());
                }
            } catch (Exception ex) {
                singleEvent = (Event) response;
                updateEvent(rootView);

            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onEventInfoFragmentInteraction(Uri uri); //Uri uri
    }
}
