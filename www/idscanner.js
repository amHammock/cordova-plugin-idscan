// 
//  This plugin object is defined in plugin.xml to be available to the javascript as:
//      IDScanner
//
//  So to scan an id the javascript must call:
//      IDScanner.scan() using the parameters described below
//
module.exports = {  
  scan: function(
      successCallback,  // called with a javascript object containing the driver license fields
      failureCallback,  // called with error message if scanning failed
      cameraKey,        // activation key from IDScan.net for their iOS or Android Camera Scanning SDK
      parserKey         // activation key from IDScan.net for their iOS or Android ID Parsing SDK
      ) {
      if (typeof failureCallback != "function") {
          console.log("IdScannerPlugin ERROR: failureCallback is not a function");
      }
      else if (typeof successCallback != "function") {
          var msg = "IdScannerPlugin ERROR: successCallback is not a function";
          console.log(msg);
          failureCallback(msg);
      }
      else if (typeof cameraKey != "string" || typeof parserKey != "string") {
          var msg = "IdScannerPlugin ERROR: both cameraKey and parserKey activation keys from IDScan.net are required";
          console.log(msg);
          failureCallback(msg);
      }
      else {
          console.log("IdScannerPlugin: calling idScanner plugin with cameraKey="+cameraKey+", parserKey="+parserKey);
          return cordova.exec(successCallback, failureCallback, "IDScanner","scan",[cameraKey, parserKey]);
      }
  }
};
