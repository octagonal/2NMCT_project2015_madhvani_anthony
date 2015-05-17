package me.madhvani.dwells.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.madhvani.dwells.R;

/**
 * Created by anthony on 15.17.5.
 */
public class BookmarksFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int page;
    private static RecyclerView recyclerView;

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
        ArrayList<String> items = new ArrayList<String>();
        items.add(0, "lol");
        items.add(1, "lel");
        items.add(2, "kek");

        BookmarksRecyclerAdapter aItems = new BookmarksRecyclerAdapter(items);
        recyclerView.setAdapter(aItems);
        return view;
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
        private List<String> items;

        // Provide a reference to the views for each data item
        // Provide access to all the views for a data item in a view holder

        // Provide a suitable constructor (depends on the kind of dataset)
        public BookmarksRecyclerAdapter(List<String> items) {
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
            String item = items.get(position);
            viewHolder.title.setText(item);
        }

        public static class BookmarksViewHolder extends RecyclerView.ViewHolder {
            protected TextView title;

            public BookmarksViewHolder(View v) {
                super(v);
                title =  (TextView) v.findViewById(R.id.title);
            }
        }
    }
}
