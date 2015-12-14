package sath.com.doziman.dto;

import java.util.Map;

/**
 * Created by Krishna on 10/18/2015.
 */
public class MapWrapper {
    public Map<String, String> getMyMap() {
        return myMap;
    }

    public void setMyMap(Map<String, String> myMap) {
        this.myMap = myMap;
    }

    private Map<String, String> myMap;
    // getter and setter for 'myMap'
}
