package me.madhvani.dwells.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.madhvani.dwells.R;
import me.madhvani.dwells.data.CityLatLong;
import me.madhvani.dwells.model.Kot;
import me.madhvani.dwells.utils.BookmarkReaderWriter;

/**
 * Created by anthony on 15.17.5.
 */
public class BookmarksFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public BookmarksRecyclerAdapter aItems = null;
    private static final String TAG = "BookmarksFragment";
    private String latLng;
    private int page;
    private static RecyclerView recyclerView;
    public static Button routeViewer;
    private ArrayList<Kot> items;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookmarks_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // Setup layout manager for items
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        // Control orientation of the items
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        // Attach layout manager
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        // Bind adapter to recycler

        routeViewer = (Button) view.findViewById(R.id.tripMake);
        routeViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getBaseContext(), RouteActivity.class);
                i.putParcelableArrayListExtra("kots", items);
                i.putExtra("city", latLng);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refreshData();
        }
    }

    public void refreshData(){
        Log.v(TAG,"Fragment resumed");
        items = new ArrayList<Kot>();
        if(getActivity() == null){
            return;
        }
        try {
            items = new BookmarkReaderWriter(getActivity().getApplicationContext(),
                    ((MapsActivity) getActivity()).getSelectedItem())
                    .readItems();
            latLng = ((MapsActivity) getActivity()).getSelectedItem();
        } catch (IOException e){
            Log.e(TAG, e.getMessage() );
        }

        aItems = new BookmarksRecyclerAdapter(items);
        recyclerView.setAdapter(aItems);
    }

    public static BookmarksFragment newInstance(int page) {
        BookmarksFragment fragmentFirst = new BookmarksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(ARG_PAGE);
    }

    public static class BookmarksRecyclerAdapter extends RecyclerView.Adapter<BookmarksRecyclerAdapter.BookmarksViewHolder> {
        private List<Kot> items;

        // Provide a reference to the views for each data item
        // Provide access to all the views for a data item in a view holder

        // Provide a suitable constructor (depends on the kind of dataset)
        public BookmarksRecyclerAdapter(List<Kot> items) {
            this.items = items;
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return this.items.size();
        }

        // Create new items (invoked by the layout manager)
        // Usually involves inflating a layout from XML and returning the holder
        @Override
        public BookmarksViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.bookmarks_card, viewGroup, false);
            return new BookmarksViewHolder(itemView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(BookmarksViewHolder viewHolder, int position) {
            Kot item = items.get(position);


            String patternStr = "([^\\/]*$)";
            Pattern p = Pattern.compile(patternStr);
            Matcher m = p.matcher(item.getUrl());
            m.find();
            String output = m.group(1).substring(0, 1).toUpperCase() + m.group(1).substring(1);
            output = output.replaceAll("-", " ");

            viewHolder.location.setText(output);
            viewHolder.title.setText("€" + item.getPrice().toString());
            viewHolder.area.setText(item.getArea().toString() + "m²");
        }

        public static class BookmarksViewHolder extends RecyclerView.ViewHolder {
            protected TextView title;
            protected TextView area;
            protected TextView location;

            public BookmarksViewHolder(View v) {
                super(v);

                area = ((TextView) v.findViewById(R.id.area));
                title =  (TextView) v.findViewById(R.id.title);
                location = ((TextView) v.findViewById(R.id.location));
            }
        }
    }
}
