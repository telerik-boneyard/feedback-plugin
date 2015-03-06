//
//  TLRKFeedback.m
//  Telerik AppFeedback Plugin
//
//  Copyright (c) 2014 Telerik. All rights reserved.
//

#import "TLRKFeedback.h"
#import <Cordova/UIDevice+Extensions.h>
#import <TelerikAppFeedback/AppFeedback.h>

@implementation TLRKFeedback

@synthesize webView;

-(void)initialize: (CDVInvokedUrlCommand *)command
{
    NSString *apiKey = command.arguments[0];
    NSString *apiUrl = command.arguments[1];
    NSString *uid = [[[UIDevice currentDevice] uniqueAppInstanceIdentifier] lowercaseString];
    
    TKFeedback.dataSource = [[TKPlatformFeedbackSource alloc] initWithKey:apiKey uid:uid apiBaseURL:apiUrl parameters:NULL];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

-(void)showFeedback: (CDVInvokedUrlCommand *)command
{
    [TKFeedback showFeedback];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}
@end