# cordova-plugin-idscan
A Cordova plugin for the camera scanner and ID parser SDK's from [IDScan.net](http://IDScan.net). Use it to scan drivers licenses and other IDs with the device camera, and parse out the data. Android and iOS are supported.

A valid license key is required for both SDKs from IDScan.net for the plugin to work. You only need keys for the platform you are using.

I wrote this code for a project for [DPM Solutions, Inc](http://dpmsinc.net). I provide it here for anyone to use as a starting point if they have a similar need.

To install the plugin directly from here, use the Cordova CLI command:

    cordova plugin add https://github.com/amHammock/cordova-plugin-idscan

Or you can clone the repository locally if you would like to modify the code and install from there. The plugin is exposed to your Cordova javascript code as the `IDScanner` object. Simply call the `scan()` function like so:

    //  
    // for all fields available on the result object, refer to DriverLicenseParser.h in the iOS sdk
    // e.g. firstName, lastName, birthdate, city, postalCode, licenseNumber, expirationDate, etc.
    //
    function successCallback(result) {
      console.log("Successfully scanned ID for " + result.fullName);
    }

    function errorCallback(errorMsg) {
      console.log(errorMsg);
    }

    //
    // You must get valid license keys from IDScan.net for this to work
    //
    IDScanner.scan(successCallback, errorCallback, cameraKey, parserKey);

If you need to update the SDK files from IDScan.net, you can just drop new ones in the sdk folders of the iOS and Android source.

