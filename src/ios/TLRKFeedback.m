//
//  TLRKFeedback.m
//  Telerik AppFeedback Plugin
//
//  Copyright (c) 2014 Telerik. All rights reserved.
//

#import "TLRKFeedback.h"
#import "CDVDevice.h"
#import <TelerikAppFeedback/AppFeedback.h>
#import <objc/message.h>

@implementation TLRKFeedback

@synthesize webView;

-(void)initialize: (CDVInvokedUrlCommand *)command
{
    NSString *apiKey = command.arguments[0];
    NSString *apiUrl = command.arguments[1];
    NSString *uid;

    CDVDevice *device = CDVDevice.new;
    SEL devicePropertiesSEL = NSSelectorFromString(@"deviceProperties");
    SEL identifierForVendorSEL = NSSelectorFromString(@"identifierForVendor");

    if ([device respondsToSelector:devicePropertiesSEL]) {
        // Use the core plugin 'cordova-plugin-device' to get the uid value.
        // For more details see: https://github.com/apache/cordova-ios/blob/master/guides/API%20changes%20in%204.0.md
        NSDictionary *properties = objc_msgSend(device, devicePropertiesSEL);
        uid = properties[@"uuid"];
    } else if ([UIDevice.currentDevice respondsToSelector:identifierForVendorSEL]) {
        // Fallback to UIDevice's 'identifierForVendor' method.
        uid = objc_msgSend(objc_msgSend(UIDevice.currentDevice, identifierForVendorSEL), @selector(UUIDString));
    }

    TKFeedback.dataSource = [[TKPlatformFeedbackSource alloc] initWithKey:apiKey uid:uid apiBaseURL:apiUrl parameters:NULL];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

-(void)showFeedback: (CDVInvokedUrlCommand *)command
{
    [TKFeedback showFeedback];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

-(void)GetVariables:(CDVInvokedUrlCommand*)command
{
    NSMutableDictionary *values = [NSMutableDictionary dictionary];
    int i;
    for (i = 0; i < [command.arguments count]; i++)
    {
        @try
        {
            NSString *variableName = [command argumentAtIndex:i];
            NSString *variableValue = [[[NSBundle mainBundle] infoDictionary] objectForKey:variableName];
            [values setObject:variableValue forKey:variableName];
        }
        @catch (NSException *exception)
        {
        }
    }

    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:values];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

@end