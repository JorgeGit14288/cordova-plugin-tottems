//
//  
//  CordovaLib
//
//  
//
//

#import "CDVCrypt.h"
#import "CDVCryptURLProtocol.h"

@implementation CDVCrypt

- (void)pluginInitialize
{
    [NSURLProtocol registerClass:[CDVCryptURLProtocol class]];
}

@end
