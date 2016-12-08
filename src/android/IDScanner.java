package com.dpms.idscan;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import net.idscan.android.dlparser.DLParser;
import net.idscan.android.pdf417scanner.PDF417ScanActivity;


public class IDScanner extends CordovaPlugin {
    private CallbackContext callbackContext;
    private String cameraKey, parserKey;
    
    private static final String TAG = "IDScannerPlugin";
    private final static int SCAN_ACTIVITY_CODE = 0x001;
    
    final String test_data = "%MNBURNSVILLE^HOMER J. SYMPSON^13225 MADRID RD^?\n\n;636038326007403611=12091991090106?\n\n%\" 55306      D               F064124   HZL                           X\"+H)     ?";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {        
        Log.d(TAG, "IDScanner method starting: "+action);

        if (args.length() < 2) {
            callbackContext.error("Must provide activation keys for IDScan.net's camera scanner and id parser SDKs");
            return false;
        } else {
            this.cameraKey = args.getString(0);
            this.parserKey = args.getString(1);
            if (this.cameraKey == null || this.cameraKey.length() == 0 || this.parserKey == null || this.parserKey.length() == 0) {
                callbackContext.error("Must provide activation keys for IDScan.net's camera scanner and id parser SDKs");
                return false;
            }
            Log.d(TAG, "camera scanner key: "+this.cameraKey);
            Log.d(TAG, "id parser key: "+this.parserKey);
        }

        if ("scan".equals(action)) {
            this.callbackContext = callbackContext;
                        
            Intent i = new Intent(this.cordova.getActivity(), PDF417ScanActivity.class);
            i.putExtra(PDF417ScanActivity.EXTRA_LICENSE_KEY, this.cameraKey);

            Log.d(TAG, "starting camera scanner activity...");
            this.cordova.startActivityForResult((CordovaPlugin) this, i, SCAN_ACTIVITY_CODE);
        } else {
            Log.d(TAG, "invalid action");
            return false;
        }

        return true;
    }

    // called when PDF417ScanActivity returns with scan result or error
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_ACTIVITY_CODE) {
            if (resultCode == PDF417ScanActivity.RESULT_OK) {
                if (data != null) {
                    parse(data.getStringExtra(PDF417ScanActivity.BARCODE_DATA));
                } else {
                    callbackContext.error("ID camera scanner returned no data.");
                }
            } else if (resultCode == PDF417ScanActivity.ERROR_INVALID_CAMERA_NUMBER) {
                callbackContext.error("Invalid camera number.");
            } else if (resultCode == PDF417ScanActivity.ERROR_CAMERA_NOT_AVAILABLE) {
                callbackContext.error("Camera not available.");
            } else if (resultCode == PDF417ScanActivity.ERROR_INVALID_CAMERA_ACCESS) {
                callbackContext.error("Invalid camera access.");
            } else if (resultCode == PDF417ScanActivity.ERROR_INVALID_LICENSE_KEY) {
                callbackContext.error("Invalid camera scanner license key.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
            
    private void parse(String scanResult) {
        DLParser parser = new DLParser();
        try {
            Context context=this.cordova.getActivity().getApplicationContext();
            parser.setup(context, this.parserKey);
            DLParser.DLResult res = parser.parse(scanResult.getBytes("UTF8"));
            
            // Note that there are more fields if we need them, 
            // refer to DriverLicenseParser.h in iOS code
            JSONObject parseData = new JSONObject();
            parseData.put("fullName", res.fullName);
            parseData.put("firstName", res.firstName);
            parseData.put("middleName", res.middleName);
            parseData.put("lastName", res.lastName);
            parseData.put("nameSuffix", res.nameSuffix);
            parseData.put("namePrefix", res.namePrefix);
            parseData.put("address1", res.address1);
            parseData.put("address2", res.address2);
            parseData.put("city", res.city);
            parseData.put("postalCode", res.postalCode);
            parseData.put("country", res.country);
            parseData.put("birthdate", res.birthdate);
            parseData.put("issueDate", res.issueDate);
            parseData.put("expirationDate", res.expirationDate);
            parseData.put("licenseNumber", res.licenseNumber);
            parseData.put("issuedBy", res.issuedBy);
            parseData.put("gender", res.gender);
        
            this.callbackContext.success(parseData);
        } catch (JSONException e) {
            this.callbackContext.error(e.toString());
        } catch (DLParser.DLParserException e) {
            this.callbackContext.error(e.toString());
        } catch (UnsupportedEncodingException e) {
            this.callbackContext.error(e.toString());
        }
    }
}