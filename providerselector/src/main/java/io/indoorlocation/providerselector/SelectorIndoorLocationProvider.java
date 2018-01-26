package io.indoorlocation.providerselector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;
import io.indoorlocation.core.IndoorLocationProviderListener;

public class SelectorIndoorLocationProvider extends IndoorLocationProvider implements IndoorLocationProviderListener {

    private List<IndoorLocationProvider> indoorLocationProviderList;
    private Map<String, IndoorLocation> indoorLocationMap;
    private double indoorLocationValidity;
    private boolean isStarted = false;

    public SelectorIndoorLocationProvider(double indoorLocationValidity) {
        super();
        this.indoorLocationProviderList = new ArrayList<>();
        this.indoorLocationMap = new HashMap<>();
        this.indoorLocationValidity = indoorLocationValidity;
    }

    public void addIndoorLocationProvider(IndoorLocationProvider indoorLocationProvider) {
        this.indoorLocationProviderList.add(indoorLocationProvider);
        if (this.isStarted) {
            indoorLocationProvider.start();
        }
    }

    public void removeIndoorLocationProvider(IndoorLocationProvider indoorLocationProvider) {
        this.indoorLocationProviderList.remove(indoorLocationProvider);
        indoorLocationProvider.stop();
    }

    @Override
    public boolean supportsFloor() {
        for (IndoorLocationProvider indoorLocationProvider : this.indoorLocationProviderList) {
            if (indoorLocationProvider.supportsFloor()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        for (IndoorLocationProvider indoorLocationProvider : this.indoorLocationProviderList) {
            indoorLocationProvider.start();
        }
    }

    @Override
    public void stop() {
        for (IndoorLocationProvider indoorLocationProvider : this.indoorLocationProviderList) {
            indoorLocationProvider.stop();
        }
        this.isStarted = false;
    }

    @Override
    public boolean isStarted() {
        return this.isStarted;
    }

    private IndoorLocation selectIndoorLocation(List<IndoorLocation> indoorLocations) {

        List<IndoorLocation> validIndoorLocations = new ArrayList<>();
        for (IndoorLocation indoorLocation : indoorLocations) {
            if ((System.currentTimeMillis() - indoorLocation.getTime()) < this.indoorLocationValidity) {
                validIndoorLocations.add(indoorLocation);
            }
        }

        List<IndoorLocation> withFloorIndoorLocations = new ArrayList<>();
        List<IndoorLocation> withoutFloorIndoorLocations = new ArrayList<>();
        for (IndoorLocation indoorLocation : validIndoorLocations) {
            if (indoorLocation.getFloor() != null) {
                withFloorIndoorLocations.add(indoorLocation);
            }
            else {
                withoutFloorIndoorLocations.add(indoorLocation);
            }
        }

        if (withFloorIndoorLocations.size() > 0) {
            Collections.sort(withFloorIndoorLocations, new Comparator<IndoorLocation>() {
                @Override
                public int compare(IndoorLocation indoorLocation, IndoorLocation t1) {
                    return (int)(t1.getAccuracy() - indoorLocation.getAccuracy());
                }
            });
            return withFloorIndoorLocations.get(0);
        }

        if (withoutFloorIndoorLocations.size() > 0) {
            Collections.sort(withoutFloorIndoorLocations, new Comparator<IndoorLocation>() {
                @Override
                public int compare(IndoorLocation indoorLocation, IndoorLocation t1) {
                    return (int)(t1.getAccuracy() - indoorLocation.getAccuracy());
                }
            });
            return withoutFloorIndoorLocations.get(0);
        }

        return null;
    }

    /*
        Indoor location listener
     */
    @Override
    public void onProviderStarted() {
        if (!this.isStarted) {
            this.dispatchOnProviderStarted();
        }
        this.isStarted = true;
    }

    @Override
    public void onProviderStopped() {
        boolean allAreStopped = true;
        for (IndoorLocationProvider provider : indoorLocationProviderList) {
            if (provider.isStarted()) {
                allAreStopped = false;
            }
        }
        if (allAreStopped) {
            this.dispatchOnProviderStopped();
        }
    }

    @Override
    public void onProviderError(Error error) {
        this.dispatchOnProviderError(error);
    }

    @Override
    public void onIndoorLocationChange(IndoorLocation indoorLocation) {
        this.indoorLocationMap.put(indoorLocation.getProvider(), indoorLocation);
        IndoorLocation selectedIndoorLocation = this.selectIndoorLocation(new ArrayList<>(this.indoorLocationMap.values()));
        this.dispatchIndoorLocationChange(selectedIndoorLocation);
    }
}
