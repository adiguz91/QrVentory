package com.example.is2.test2qrventory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.is2.test2qrventory.connection.EventAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.User;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FinishedEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FinishedEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinishedEventFragment extends Fragment implements VolleyResponseListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    User user = null;
    List<Event> events = new ArrayList<>();
    ListView listViewFinishedEvents = null;
    private CustomEventListAdapter adapter;
    Domain domain = null;

    public FinishedEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FinishedEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FinishedEventFragment newInstance(String param1, String param2) {
        FinishedEventFragment fragment = new FinishedEventFragment();
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
        domain = getActivity().getIntent().getParcelableExtra("domain");
        EventAccess eventAccess = new EventAccess(user.getApiKey());
        eventAccess.getEventsFromDomain(this, domain.getIdDomain());

        adapter = new CustomEventListAdapter((TabbedEventsActivity) getActivity(), events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_finished_event, container, false);
        listViewFinishedEvents = (ListView) rootView.findViewById(R.id.list_view_finished_events);

        //adapter = new CustomEventListAdapter((TabbedEventsActivity) getActivity(), events); // category.getSubcategories()

        listViewFinishedEvents.setAdapter(adapter);

        listViewFinishedEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                Event event = (Event) adapter.getItemAtPosition(position);
                nextActivity(EventSingleActivity.class, event);
            }
        });

        return rootView;
    }

    private void nextActivity(Class activity_class, Object object) {
        Intent intent = new Intent(getActivity().getBaseContext(), activity_class); //ItemActivity.class
        //based on item add info to intent
        intent.putExtra("user", user);

        if(object.getClass() == Event.class)
            intent.putExtra("event", (Event) object);

        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFinishedFragmentInteraction(uri);
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
        if(response != null)
        {
            events.clear();
            //events.addAll((List<Event>) response);

            for (int event_count = 2; event_count < ((List<Event>) response).size(); event_count++) {
                Event event = ((List<Event>) response).get(event_count);
                int event_status = event.getStatus();
                if (event_status == 2) {
                    events.add(event);
                }
            }

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            adapter.notifyDataSetChanged();
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
        public void onFinishedFragmentInteraction(Uri uri); //Uri uri
    }
}
