package sath.com.doziman.menufragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sath.com.doziman.R;

/**
 * Created by Krishna on 12/10/2015.
 */
public class UpdateAddressFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.updateaddresslayout,container,false);
        return v;
    }
}
