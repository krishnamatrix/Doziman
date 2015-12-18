package sath.com.doziman.integration;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import sath.com.doziman.dto.DoziAddressDTO;

/**
 * Created by Krishna on 10/9/2015.
 */
public class HttpDataPostImpl {

    public static String insertDhobiData(List<NameValuePair> nameValuePairs) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://doziman.net84.net/insertaddress");

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            Log.i("","got response" + response.getStatusLine());
            return extractData(response);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }
    public static List<DoziAddressDTO> getDoziMarkerData(double curLat, double curLong) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://doziman.net84.net/getdozidata.php");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("custlat", String.valueOf(curLat)));
            nameValuePairs.add(new BasicNameValuePair("custlong", String.valueOf(curLong)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.i("","Came to posting request");
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.i("","got response" + response.getStatusLine());
            return getJsonMapData(response);
        } catch (ClientProtocolException e) {
            Log.i("", "ClientProtocolException");
        } catch (IOException e) {
            Log.i("", "IOException");
        }
        return null;
    }
    public static List<DoziAddressDTO> getDoziMarkerDataFromIds(List<String> doziIdsList, double curLat, double curLong) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://doziman.net84.net/getdozibyid.php");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            JSONArray jsArray = new JSONArray(doziIdsList);
            nameValuePairs.add(new BasicNameValuePair("custlat", String.valueOf(curLat)));
            nameValuePairs.add(new BasicNameValuePair("custlong", String.valueOf(curLong)));
            nameValuePairs.add(new BasicNameValuePair("doziidlist",jsArray.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            return getJsonMapData(response);
        } catch (ClientProtocolException e) {
            Log.i("", "ClientProtocolException");
        } catch (IOException e) {
            Log.i("", "IOException");
        }
        return null;
    }
    public static List<DoziAddressDTO> getDoziMarkerDataByLocation(String[] location) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://doziman.net84.net/getdozibylocation.php");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("location", location[0]));
            nameValuePairs.add(new BasicNameValuePair("custlat", location[1]));
            nameValuePairs.add(new BasicNameValuePair("custlong", location[2]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            return getJsonMapData(response);
        } catch (ClientProtocolException e) {
            Log.i("", "ClientProtocolException");
        } catch (IOException e) {
            Log.i("", "IOException");
        }
        return null;
    }
    public static void postSMS(String mobileNumber, String custaddress) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://api.msg91.com/api/sendhttp.php";

        try {
            Log.i("", "Sending SMS to:" + mobileNumber);
            Log.i("", "Sending SMS message:" + custaddress);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("authkey", "95027AQC5bgcn561c836c"));
            nameValuePairs.add(new BasicNameValuePair("mobiles", "91" + mobileNumber));
            nameValuePairs.add(new BasicNameValuePair("message", URLEncoder.encode(custaddress, "UTF-8")));
            nameValuePairs.add(new BasicNameValuePair("sender", "121212"));
            nameValuePairs.add(new BasicNameValuePair("route", "1"));
            nameValuePairs.add(new BasicNameValuePair("country", "91"));

            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.i("","got response" + response.getStatusLine());
            //return getJsonMapData(response);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        //return null;
    }
    private static List<DoziAddressDTO> getJsonMapData(HttpResponse httpResponse){
        List<DoziAddressDTO> markerList = new ArrayList<DoziAddressDTO>();//HashMap<Marker, Fermata> hashMap = new HashMap<Marker, Data>();
        Double lati, longi;
        DoziAddressDTO doziAddressDTO = null;
        try {
            JSONObject jsonRootObject = new JSONObject(extractData(httpResponse));

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("dozimarkers");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                doziAddressDTO = new DoziAddressDTO();
                doziAddressDTO.setDoziLatitude(Double.parseDouble(jsonObject.optString("latitude").toString()));
                doziAddressDTO.setDoziLongitude(Double.parseDouble(jsonObject.optString("longitude").toString()));
                doziAddressDTO.setDoziName(jsonObject.optString("name").toString());
                doziAddressDTO.setDoziAddress(jsonObject.optString("address").toString());
                doziAddressDTO.setDoziMobile(jsonObject.optString("mobile").toString());
                doziAddressDTO.setDoziDistance(jsonObject.optString("distance").toString());
                doziAddressDTO.setDoziId(jsonObject.optString("doziid").toString());

                markerList.add(doziAddressDTO);
                //markerList.add(new MarkerOptions().position(new LatLng(lati, longi)).title("Dozi Location"));
                /*Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lati, longi)));
                hashMap.put(marker, data);*/
            }
            //output.setText(data);
        } catch (JSONException e) {e.printStackTrace();}
        return markerList;
    }

    private static String extractData(HttpResponse httpResponse){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = httpResponse.getEntity().getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String bufferedStrChunk = null;

            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }
            Log.i("","The output" + stringBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<String> searchLocations(String input) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://doziman.net84.net/insertaddress");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("searchkey", input));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            Log.i("","got response" + response.getStatusLine());
            return getJsonSearchData(response);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }
    private static List<String> getJsonSearchData(HttpResponse httpResponse){
        List<String> locationList = new ArrayList<String>();
        try {
            JSONObject jsonRootObject = new JSONObject(extractData(httpResponse));

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("locations");

            ArrayList<String> list = new ArrayList<String>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    list.add(jsonArray.get(i).toString());
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
        return locationList;
    }
    //http://doziman.net84.net/getdozidata.php?custlat=12.9702205&custlong=80.251317
}
