package sath.com.doziman.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sath.com.doziman.MapWrapperLayout;
import sath.com.doziman.OnInfoWindowElemTouchListener;
import sath.com.doziman.R;
import sath.com.doziman.TransparentProgressDialog;
import sath.com.doziman.activity.ShowMapActivity;
import sath.com.doziman.adapter.AddressListAdapter;
import sath.com.doziman.dto.DoziAddressDTO;
import sath.com.doziman.integration.HttpDataPostImpl;
import sath.com.doziman.utils.StorageUtil;

/**
 * Created by Krishna on 11/25/2015.
 */
public class RecentsTab extends Fragment implements OnMapReadyCallback/*, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/  {
    ListView listView;
    AddressListAdapter arrayAdapter;
    List<DoziAddressDTO> addressDTOList = null;
    GoogleMap googleMap;
    Location currentLocation;
    private TransparentProgressDialog pd;
    private Map<Marker, String> allMarkersMap;
    ShowMapActivity currentActivity;
    ImageButton FAB;
    MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    private TextView infoTitle, infoSnippet,infoDoziMobile;
    private Button infoButton, toggleView;
    private OnInfoWindowElemTouchListener infoButtonListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        allMarkersMap = new HashMap<Marker, String>();
        View v =inflater.inflate(R.layout.recentstab,container,false);
        /*SupportMapFragment supportMapFragment =
                (SupportMapFragment) currentActivity.getSupportFragmentManager().findFragmentById(R.id.googleMap); */
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);
        mapWrapperLayout = (MapWrapperLayout)v.findViewById(R.id.mapFrame);
        pd = new TransparentProgressDialog(currentActivity, R.drawable.loadingspinner);
        addressDTOList = new ArrayList<DoziAddressDTO>();
        arrayAdapter = new AddressListAdapter(currentActivity, R.layout.childlistview, addressDTOList);
        listView = (ListView)v.findViewById(R.id.listFrame);
        listView.setAdapter(arrayAdapter);
        mapWrapperLayout.setVisibility(View.GONE);

        Set<String> doziIds = StorageUtil.getRecents(currentActivity);
        if(doziIds != null){
            List<String> doziIdsList = new ArrayList<String>();
            doziIdsList.addAll(doziIds);
            new RetrieveDoziIdsMarkersTask().execute(doziIdsList);
        }

        FAB = (ImageButton) v.findViewById(R.id.imageButton);
            FAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mapWrapperLayout.getVisibility() == View.GONE){
                        mapWrapperLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        FAB.setImageResource(R.drawable.linkedin);
                    } else {
                        listView.setVisibility(View.VISIBLE);
                        mapWrapperLayout.setVisibility(View.GONE);
                        currentActivity.resetViewPager();
                        FAB.setImageResource(R.drawable.mappointer);
                    }
                }
            });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        currentActivity = (ShowMapActivity)activity;
        super.onAttach(activity);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMyLocationEnabled(true);
        mapWrapperLayout.init(googleMap, getPixelsFromDp(currentActivity, 39 + 20));
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
    }

    class RetrieveDoziIdsMarkersTask extends AsyncTask<List<String>, Void, List<DoziAddressDTO>> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        protected void onPostExecute(List<DoziAddressDTO> markerList) {
            DoziAddressDTO doziAddressDTO = null;
            Marker marker = null;
            for(int i=0; i < markerList.size(); i++){
                doziAddressDTO = markerList.get(i);
                marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(doziAddressDTO.getDoziLatitude(), doziAddressDTO.getDoziLongitude()))
                        .title(doziAddressDTO.getDoziName() + " " + doziAddressDTO.getDoziAddress()));
                allMarkersMap.put(marker, doziAddressDTO.getDoziMobile());
                addressDTOList.add(doziAddressDTO);
            }
            arrayAdapter.notifyDataSetChanged();
            if(currentLocation != null){
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                builder.setMessage(R.string.no_current_location).setTitle(R.string.success);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            //Hide the Spinner
            pd.hide();
        }

        @Override
        protected List<DoziAddressDTO> doInBackground(List<String>... params) {
            return HttpDataPostImpl.getDoziMarkerDataFromIds(params[0], currentLocation.getLatitude(), currentLocation.getLongitude());
        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}