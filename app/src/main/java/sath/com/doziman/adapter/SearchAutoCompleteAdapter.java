package sath.com.doziman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import sath.com.doziman.R;
import sath.com.doziman.integration.PlaceAPI;

/**
 * Created by Krishna on 12/17/2015.
 */
public class SearchAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> resultList;
    Context mContext;
    PlaceAPI mPlaceAPI = new PlaceAPI();

    public SearchAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
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
                    resultList = mPlaceAPI.autocomplete(constraint.toString());
                    // Footer
                    resultList.add("footer");
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (position != (resultList.size() - 1)){
            view = inflater.inflate(R.layout.searchlistview, null);
            TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
            autocompleteTextView.setText(resultList.get(position));
        } else {
            view = inflater.inflate(R.layout.searchlistgooglelogo, null);
        }
        return view;
    }
    /*private List<String> autocomplete(String input) {
        List<String> resultList = HttpDataPostImpl.searchLocations(input);
        return resultList;
    }*/
}