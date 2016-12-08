#import "IDScannerPlugin.h"
#import "ScannerViewController.h"
#import "DriverLicenseParser.h"
#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>

@implementation IDScannerPlugin
@synthesize callbackId;
@synthesize cameraKey;
@synthesize parserKey;
/*
- (CDVPlugin*) initWithWebView:(UIWebView*)theWebView
{
    self = (IDScannerPlugin*)[super initWithWebView:theWebView];
    return self;
}
*/
- (void)scan:(CDVInvokedUrlCommand*)command
{
    self.cameraKey = [command.arguments objectAtIndex:0];    
    self.parserKey = [command.arguments objectAtIndex:1];    
    self.callbackId = command.callbackId;

    // BarcodeScanner sdk and DriverLicenseParser sdk require these activation codes
    NSLog(@"IDScannerPlugin: cameraKey=%@",self.cameraKey);
    NSLog(@"IDScannerPlugin: parserKey=%@",self.parserKey);
    [[NSUserDefaults standardUserDefaults] setValue:self.cameraKey forKey:@"cameraKey"];
    [[NSUserDefaults standardUserDefaults] setValue:self.parserKey forKey:@"DriverLicenseParserCurrentSerial"];
    [[NSUserDefaults standardUserDefaults] synchronize];

    // create scanner controller, set our plugin as a delegate so it can call us with the result.
    ScannerViewController* scannerViewController = [[ScannerViewController alloc] init];
    scannerViewController.delegate = self;
    NSLog(@"IDScannerPlugin: Starting camera scanner...");
    [self.viewController presentViewController:scannerViewController animated:YES completion:nil];
}

- (void)returnScanResult:(ScannerViewController *)controller scanResult:(NSString *)scanResult
{
    CDVPluginResult* pluginResult = nil;

    if (scanResult != nil){
        NSLog(@"IDScannerPlugin: Raw scan was returned from camera scanner: %@",scanResult);
        NSLog(@"IDScannerPlugin: Calling DriverLicenseParser library...");
        DriverLicense *dl = [[DriverLicense alloc] init];
        if ([dl parseDLString:scanResult hideSerialAlert:NO] == NO){
            NSLog(@"IDScannerPlugin ERROR: DriverLicenseParser returned nothing");
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"DriverLicenseParser error parsing scanned input"];
        }else{
            AudioServicesPlayAlertSoundWithCompletion(kSystemSoundID_Vibrate,nil);
            NSLog(@"IDScannerPlugin: DriverLicenseParser success, fullName from DL=%@",dl.fullName);
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:[dl fields]];
        }
    }else{
        NSLog(@"IDScannerPlugin: Camera scan was cancelled or returned nil");
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Camera scan was cancelled or returned nil"];
    }
            
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

@end
