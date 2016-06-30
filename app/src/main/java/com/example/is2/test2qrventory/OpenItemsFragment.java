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
import com.example.is2.test2qrventory.connection.ItemAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OpenItemsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OpenItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenItemsFragment extends Fragment implements VolleyResponseListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    List<Item> items = new ArrayList<>();
    User user = null;
    ListView listViewOpenItems = null;
    private CustomItemListAdapter adapter;
    Event event = null;
    EventAccess eventAccess = null;

    public OpenItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpenItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OpenItemsFragment newInstance(String param1, String param2) {
        OpenItemsFragment fragment = new OpenItemsFragment();
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

        event = getActivity().getIntent().getParcelableExtra("event");
        user = getActivity().getIntent().getParcelableExtra("user");
        String userApiKey = user.getApiKey();
        ItemAccess itemAccess = new ItemAccess(userApiKey);
        itemAccess.getEventItemsThatNotExists(this, event.getIdDomain(), event.getId());

        eventAccess = new EventAccess(userApiKey);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =  inflater.inflate(R.layout.fragment_open_items, container, false);

        listViewOpenItems = (ListView) rootView.findViewById(R.id.list_view_open_items);

        adapter = new CustomItemListAdapter((TabbedEventSingleActivity) getActivity(), items); // category.getSubcategories()

        listViewOpenItems.setAdapter(adapter);

        listViewOpenItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                Item item = (Item) adapter.getItemAtPosition(position);
                //nextActivity(ItemActivity.class, item);

                Intent intent = new Intent(getActivity().getBaseContext(), ItemActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("item", item);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void nextActivity(Class activity_class, Object object) {
        Intent intent = new Intent(getActivity().getBaseContext(), activity_class); //ItemActivity.class
        //based on item add info to intent
        intent.putExtra("user", user);

        if(object.getClass() == Item.class)
            intent.putExtra("item", (Item) object);

        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onOpenItemsFragmentInteraction(uri);
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
            try {
                int size = ((List<Item>) response).size();
                if (size == 0) {
                    eventAccess.updateEventStatus(this, event.getId(), 2);
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }

                if (event.getStatus() == 1) {
                    items.clear();
                    items.addAll((List<Item>) response);

            /*for (int item_count = 0; item_count < ((List<Item>) response).size(); item_count++) {
                Item item = ((List<Item>) response).get(item_count);
                items.add(item);
            }*/

                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                boolean isUpdatedStatus = (Boolean) response;
                if(isUpdatedStatus) {

                }
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
        public void onOpenItemsFragmentInteraction(Uri uri); //Uri uri
    }
}
