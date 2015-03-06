//
//  TLRKFeedback.h
//  Telerik AppFeedback Plugin
//
//  Copyright (c) 2014 Telerik. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface TLRKFeedback : CDVPlugin
-(void)initialize: (CDVInvokedUrlCommand *)command;
-(void)showFeedback: (CDVInvokedUrlCommand *)command;
@end
