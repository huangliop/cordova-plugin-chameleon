//
//  CApplication.h
//  chameleon
//
//  Created by Justin Yip on 6/16/14.
//
//

#import <Foundation/Foundation.h>

extern NSString* const kWWWFolderName;
extern NSString* const kAppResetNotification;
@interface CApplication : NSObject

+ (instancetype)sharedApplication;

- (BOOL)launch;

@end
