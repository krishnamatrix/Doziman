package sath.com.doziman.tabs;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

import sath.com.doziman.AddressListAdapter;
import sath.com.doziman.HttpDataPostImpl;
import sath.com.doziman.MapWrapperLayout;
import sath.com.doziman.OnInfoWindowElemTouchListener;
import sath.com.doziman.R;
import sath.com.doziman.ShowMapActivity;
import sath.com.doziman.dto.DoziAddressDTO;

/**
 * Created by Krishna on 11/25/2015.
 */
public class RecentsTab extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    ListView listView;
    AddressListAdapter arrayAdapter;
    List<DoziAddressDTO> addressDTOList = null;
    GoogleMap googleMap;
    Location currentLocation;
    private GoogleApiClient mGoogleApiClient = null;
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

        addressDTOList = new ArrayList<DoziAddressDTO>();
        arrayAdapter = new AddressListAdapter(currentActivity, R.layout.childlistview, addressDTOList);
        listView = (ListView)v.findViewById(R.id.listFrame);
        listView.setAdapter(arrayAdapter);
        mapWrapperLayout.setVisibility(View.GONE);
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
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.e("","Came to onConnected" + currentLocation);
        //locationTv.setText("Latitude:" + currentLocation.getLatitude() + ", Longitude:" + currentLocation.getLongitude());
        if(currentLocation != null){
            new RetrieveMarkersTask().execute(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        } else {
            new RetrieveMarkersTask().execute(new LatLng(10.9555, 77.6904));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    class RetrieveMarkersTask extends AsyncTask<LatLng, Void, List<DoziAddressDTO>> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            //linlaHeaderProgress.setVisibility(View.VISIBLE);
            //pd.show();
        }

        protected void onPostExecute(List<DoziAddressDTO> markerList) {

            DoziAddressDTO doziAddressDTO = null;
            Marker marker = null;

            for(int i=0; i < markerList.size(); i++){
                doziAddressDTO = markerList.get(i);
                marker =googleMap.addMarker(new MarkerOptions().position(new LatLng(doziAddressDTO.getDoziLatitude(), doziAddressDTO.getDoziLongitude()))
                        .title(doziAddressDTO.getDoziName() + " " + doziAddressDTO.getDoziAddress()));
                allMarkersMap.put(marker, doziAddressDTO.getDoziMobile());
                addressDTOList.add(doziAddressDTO);
                Log.i("", "The addresslist inside:" + addressDTOList.get(i).getDoziAddress());
            }

            for(int j=0; j< addressDTOList.size(); j++){
                Log.i("", "After Interation inside:" + addressDTOList.get(j).getDoziAddress());
            }
            Log.i("", "After Interation address:" + currentLocation);
            arrayAdapter.notifyDataSetChanged();
            if(currentLocation != null){
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
            } else {
                new RetrieveMarkersTask().execute(new LatLng(12.9555, 77.6904));
            }
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            //Hide the Spinner
            //pd.hide();
        }

        @Override
        protected List<DoziAddressDTO> doInBackground(LatLng... params) {
            //locationTv.setText("Before getting markers");
            Log.i("","Came to do background" + params[0].latitude);
            return HttpDataPostImpl.getDoziMarkerData(params[0].latitude, params[0].longitude);
        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}