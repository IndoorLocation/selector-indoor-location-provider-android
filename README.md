# selector-indoor-location-provider-android
Provider to select the most accurate location from a set of providers

# Select the best IndoorLocation from multiple providers

This providers allows you to combine multiple indoor location sources. For example, you could have the GPS when you are outside, then beacons in a part of the building, wifi in a different part and QR-codes in the technical areas.

## Selection rules

The provider will select the best source based on the following rules:

- The IndoorLocation need to be recent enough. A validity (in milliseconds) value need to be given to the constructor. Any location older than the validity will be discarded. This is useful to detect when you leave covered areas.
- IndoorLocation with floor are prefered over location without floor. A location without floor will be used only if there is no valid location with floor. Typically, GPS locations which do not have floors are discarded if beacons, wifi or QR-Codes locations are available.
- If there are multiple valid locations, then the location with the best accuracy is used.

## Use

Instanciate the provider defining the validity of measurements in milliseconds:

```
ILSelector = new IndoorLocationProviderSelector(60*1000);
```

Add providers to be used for the selection:

```
ILSelector.addIndoorLocationProvider(gpsIndoorLocationProvider);
ILSelector.addIndoorLocationProvider(beaconIndoorLocationProvider);
ILSelector.addIndoorLocationProvider(socketIndoorLocationProvider);
ILSelector.addIndoorLocationProvider(qrcodeIndoorLocationProvider);
```

Set the provider in your Mapwize SDK:

```
mapwizePlugin.setLocationProvider(ILSelector);     
```

## Contribute

Contributions are welcome. We will be happy to review your PR.

If you fork this repository to implement differents sets of selection rules, do not hesitate to let us know and we will reference them.

## Support

For any support with this provider, please do not hesitate to contact [support@mapwize.io](mailto:support@mapwize.io)

## License

MIT
