package sath.com.doziman;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

import sath.com.doziman.dto.DoziAddressDTO;
import sath.com.doziman.tabs.SlidingTabLayout;
import sath.com.doziman.tabs.ViewPagerAdapter;

//import com.google.android.gms.common.GooglePlayServicesClient;

/**
 * Created by Krishna on 10/12/2015.
 */
public class ShowMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {// implements LocationListener
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    GoogleMap googleMap;
    Location currentLocation;
    private GoogleApiClient mGoogleApiClient = null;
    private ViewGroup infoWindow;
    private TextView infoTitle, infoSnippet,infoDoziMobile;
    private Button infoButton, toggleView;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private TransparentProgressDialog pd;
    ListView lstTest;
    AddressListAdapter arrayAdapter;
    List<DoziAddressDTO> addressDTOList = null;
    RelativeLayout listView, mapView;
    Map<String, String> HtKpi;
    private Map<Marker, String> allMarkersMap;
    Toolbar toolbar;
    SmartViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Recents","Near By", "Search"};
    int Numboftabs =3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        // Layout Views
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        /*listView = (RelativeLayout)findViewById(R.id.listView);
        mapView = (RelativeLayout)findViewById(R.id.mapView);

        //linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        pd = new TransparentProgressDialog(this, R.drawable.loadingspinner);
*/
        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            SupportMapFragment supportMapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
            googleMap = supportMapFragment.getMap();
        } else{
            SupportMapFragment supportMapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
            googleMap = supportMapFragment.getMap();
        }*/

  /*      SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);
*/
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (SmartViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.rateapp:
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                        /*Toast.makeText(getApplicationContext(),"Inbox Selected",Toast.LENGTH_SHORT).show();
                        ContentFragment fragment = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,fragment);
                        fragmentTransaction.commit();
                        return true;*/

                    // For rest of the options we just show a toast on click

                    case R.id.updateAddress:
                        IntentCaller.callIntent(ShowMapActivity.this, AddCustomerAddressActivity.class);
                        return true;
                    /*case R.id.sent_mail:
                        Toast.makeText(getApplicationContext(),"Send Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.drafts:
                        Toast.makeText(getApplicationContext(),"Drafts Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.allmail:
                        Toast.makeText(getApplicationContext(),"All Mail Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(),"Trash Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.spam:
                        Toast.makeText(getApplicationContext(),"Spam Selected",Toast.LENGTH_SHORT).show();
                        return true;*/
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();



        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
  /*      this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.mapinfolayout, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoDoziMobile = (TextView)infoWindow.findViewById(R.id.doziMobile);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);
        allMarkersMap = new HashMap<Marker, String>();
        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.btn),
                getResources().getDrawable(R.drawable.btn)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Gson gson = new Gson();
                SharedPreferences settings = getSharedPreferences("customer",
                        Context.MODE_PRIVATE);
                String wrapperStr = settings.getString("customeraddress", null);
                MapWrapper wrapper = gson.fromJson(wrapperStr, MapWrapper.class);
                HtKpi = wrapper.getMyMap();
                final Marker currentMarker = marker;
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMapActivity.this);
                builder.setMessage(HtKpi.get("custaddress1") + "\n" + HtKpi.get("custaddress2")).setTitle(R.string.areyousure);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        smsDTO smsdto = new smsDTO();
                        smsdto.setName(HtKpi.get("custname"));
                        //smsdto.setMobileNo(HtKpi.get("custphone"));
                        smsdto.setMobileNo(allMarkersMap.get(currentMarker));
                        smsdto.setAddress(HtKpi.get("custaddress1") + "\n" + HtKpi.get("custaddress2"));
                        new SendSMSTask().execute(smsdto);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        Button updateAddress = (Button) findViewById(R.id.updateAddress);
        updateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentCaller.callIntent(ShowMapActivity.this, AddCustomerAddressActivity.class);
            }
        });
        Button addDhobi = (Button) findViewById(R.id.addDhobi);
        addDhobi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentCaller.callIntent(ShowMapActivity.this, AddDhobiAddressActivity.class);
            }
        });
        Button updateAddress1 = (Button) findViewById(R.id.listViewUpdateAddress);
        updateAddress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentCaller.callIntent(ShowMapActivity.this, AddCustomerAddressActivity.class);
            }
        });
        Button addDhobi1 = (Button) findViewById(R.id.listViewAddDhobi);
        addDhobi1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentCaller.callIntent(ShowMapActivity.this, AddDhobiAddressActivity.class);
            }
        });

        toggleView = (Button) findViewById(R.id.toggleView);
        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapView.getVisibility() == View.GONE){
                    mapView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    toggleView.setText(R.string.list_view);
                } else {
                    listView.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);
                    toggleView.setText(R.string.map_view);
                }
            }
        });

        // Initializing the List view
        lstTest= (ListView)findViewById(R.id.listFrame);
        addressDTOList = new ArrayList<DoziAddressDTO>();
        arrayAdapter = new AddressListAdapter(this, R.layout.childlistview, addressDTOList);
        lstTest.setAdapter(arrayAdapter);*/
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setMyLocationEnabled(true);

        /*final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39 + 20));
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
        });*/
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
        //locationTv.setText("Latitude:" + currentLocation.getLatitude() + ", Longitude:" + currentLocation.getLongitude());
        if(currentLocation != null){
            new RetrieveMarkersTask().execute(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        } else {
            new RetrieveMarkersTask().execute(new LatLng(12.81, 88.100));
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
            pd.show();
        }

        protected void onPostExecute(List<DoziAddressDTO> markerList) {
            //locationTv.setText("After getting markers" + markerList);
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.listFrame);
            View v = null;
            //v.getParent().removeAllViews();
            //((ViewManager)insertPoint)).removeView(v);
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
            arrayAdapter.notifyDataSetChanged();
            if(currentLocation != null){
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
            } else {
                new RetrieveMarkersTask().execute(new LatLng(12.81, 88.100));
            }
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            //Hide the Spinner
            pd.hide();
        }

        @Override
        protected List<DoziAddressDTO> doInBackground(LatLng... params) {
            //locationTv.setText("Before getting markers");
            return HttpDataPostImpl.getDoziMarkerData(params[0].latitude, params[0].longitude);
        }
    }
    class SendSMSTask extends AsyncTask<smsDTO, Void, String> {

        private Exception exception;
        protected void onPostExecute(String status) {
            Toast.makeText(ShowMapActivity.this, R.string.dhobi_sms_sent, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(smsDTO... params) {
            HttpDataPostImpl.postSMS(params[0].getMobileNo(), params[0].getName() + "\n" + params[0].getAddress());
            return "";
        }
    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }


    public void resetViewPager(){
        tabs.resetViewPageListener();
    }
    /*@Override
    public void onResume() {
        super.onResume();

        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        //getListView().removeCallbacks(this);
        mGoogleApiClient.disconnect();

        super.onPause();
    }*/
}