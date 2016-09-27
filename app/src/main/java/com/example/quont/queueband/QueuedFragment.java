package com.example.quont.queueband;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QueuedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QueuedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueuedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private List<Song> songs = new ArrayList<Song>();
    private String code;


    public RecyclerView recyclerView;



    public QueuedFragment() {
        // Required empty public constructor
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QueuedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QueuedFragment newInstance(String param1, String param2, String code) {
        QueuedFragment fragment = new QueuedFragment();
        fragment.setCode(code);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_queued, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        RVAdapter adapter = new RVAdapter(this.getContext(), songs);
        recyclerView.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void initializeData()    {
        songs.clear();
        recyclerView.removeAllViews();
        recyclerView.invalidate();
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "https://deciso.audio/" + code + ".json";
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            for (int i = response.getJSONArray("queue").length()-1; i >= 0; i--)  {
                                songs.add(new Song(response.getJSONArray("queue").getJSONObject(i).getString("title"),response.getJSONArray("queue").getJSONObject(i).getString("author"), response.getJSONArray("queue").getJSONObject(i).getString("source"), response.getJSONArray("queue").getJSONObject(i).getInt("points"), response.getJSONArray("queue").getJSONObject(i).getString("thumbnail"), response.getJSONArray("queue").getJSONObject(i).getString("id")));
                            }

                            recyclerView.getAdapter().notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsonObjectRequest);
        queue.start();
    }

    public void addSong(JSONObject jsonObject) throws JSONException {
        songs.add(0, new Song(jsonObject.getString("title"), jsonObject.getString("author"), jsonObject.getString("source"), jsonObject.getInt("points"), jsonObject.getString("thumbnail"), jsonObject.getString("id")));
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeData();
    }
}
