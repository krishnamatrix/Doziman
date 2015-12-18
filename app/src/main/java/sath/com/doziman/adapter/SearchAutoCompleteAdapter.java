package sath.com.doziman.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

import sath.com.doziman.integration.HttpDataPostImpl;

/**
 * Created by Krishna on 12/17/2015.
 */
public class SearchAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> resultList;

    public SearchAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private List<String> autocomplete(String input) {
        List<String> resultList = HttpDataPostImpl.searchLocations(input);
        return resultList;
    }
}