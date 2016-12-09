//
//  BarcodeScanner.h
//  BarcodeScanner
//
//  Created by Александр Ушаков on 06.01.15.
//  Copyright (c) 2015 tauruna. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Barcode2DScanner : NSObject {
    bool isDemo;
}

-(void) registerCode: (NSString*) code;
-(NSString*) scanGrayscaleImage: (uint8_t*) pp_image Width: (int) width Height: (int) height Encoding: (NSStringEncoding) encoding;

@end
