package sath.com.doziman.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sath.com.doziman.R;

/**
 * Created by Krishna on 11/25/2015.
 */
public class SearchbyTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.searchbytab,container,false);
        return v;
    }
}