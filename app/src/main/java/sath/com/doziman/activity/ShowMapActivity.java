package sath.com.doziman.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sath.com.doziman.OnInfoWindowElemTouchListener;
import sath.com.doziman.R;
import sath.com.doziman.SmartViewPager;
import sath.com.doziman.adapter.AddressListAdapter;
import sath.com.doziman.adapter.ViewPagerAdapter;
import sath.com.doziman.dto.DoziAddressDTO;
import sath.com.doziman.tabs.SlidingTabLayout;
import sath.com.doziman.utils.IntentCaller;
import sath.com.doziman.utils.StorageUtil;

//import com.google.android.gms.common.GooglePlayServicesClient;

/**
 * Created by Krishna on 10/12/2015.
 */
public class ShowMapActivity extends AppCompatActivity{
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    GoogleMap googleMap;
    Location currentLocation;
    private GoogleApiClient mGoogleApiClient = null;
    private ViewGroup infoWindow;
    private TextView infoTitle, infoSnippet,infoDoziMobile;
    private Button infoButton, toggleView;
    private OnInfoWindowElemTouchListener infoButtonListener;
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
//    CharSequence Titles[] = {"Recents","Near By", "Search"};
//    int Numboftabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        // Layout Views
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        pager = (SmartViewPager) findViewById(R.id.pager);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        Set<String> recentItems = (Set<String>)StorageUtil.getRecents(this);
        if(recentItems != null && recentItems.size() > 0) {
            int Numboftabs = 3;
            CharSequence Titles[] = {"Recents","Near By", "Search"};
            adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
            pager.setOffscreenPageLimit(3);
        } else {
            int Numboftabs = 2;
            CharSequence Titles[] = {"Near By", "Search"};
            adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
            pager.setOffscreenPageLimit(2);
        }
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.rateapp:
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
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

                    case R.id.updateAddress:
                        IntentCaller.callIntent(ShowMapActivity.this, AddCustomerAddressActivity.class);
                        return true;
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
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
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