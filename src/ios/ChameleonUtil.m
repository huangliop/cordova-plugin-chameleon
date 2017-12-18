/*
 Copyright 2014 Modern Alchemists OG

 Licensed under MIT.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
*/


#import "ChameleonUtil.h"
#import "CApplication.h"
#import <Cordova/CDVScreenOrientationDelegate.h>

@implementation ChameleonUtil

@synthesize command;

-(void)pluginInitialize{
    //当第一次打开时，进行初始化，拷贝WWW目录
    UIView *parentView=self.viewController.view ;
    UIImageView *imageView=[[UIImageView alloc] init];
    NSString *imageName=[self getImageName:[self getCurrentOrientation] delegate:(id<CDVScreenOrientationDelegate>)self.viewController device:[self getCurrentDevice]];
    imageView.image=[UIImage imageNamed:imageName];
    [parentView addSubview:imageView];
    
    UIActivityIndicatorView *loading=[[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    float y=[UIScreen mainScreen].bounds.size.height-100;
    float x=[UIScreen mainScreen].bounds.size.width/2-loading.frame.size.width/2;
    [loading setFrame:CGRectMake(x, y, loading.frame.size.width, loading.frame.size.height)];
    loading.color=UIColor.blueColor;
    [loading startAnimating];
    [parentView addSubview:loading];
    
    
    //开启异步来拷贝，避免中某些性能地下的手机上，由于主线程阻塞过久而崩溃的问题
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT,0), ^{
        BOOL needZip= [[CApplication sharedApplication] launch];
            dispatch_sync(dispatch_get_main_queue(), ^{
                [imageView removeFromSuperview];
                [loading removeFromSuperview];
               if(needZip) [self.viewController viewDidLoad];
            });
    });
}
- (void)removeWebviewCache:(CDVInvokedUrlCommand*)command
{
	NSLog(@"Cordova iOS Cache.clear() called.");
    
    self.command = command;

	// Arguments arenot used at the moment.
    // NSArray* arguments = command.arguments;

    [self.commandDelegate runInBackground:^{

        // clear cache
        [[NSURLCache sharedURLCache] removeAllCachedResponses];

    }];
    BOOL isQuit = [[command.arguments objectAtIndex:0] boolValue];
    if (isQuit) {
        exit(0);
    }
}

//获取当前因该显示的图片的名称
- (NSString*)getImageName:(UIInterfaceOrientation)currentOrientation delegate:(id<CDVScreenOrientationDelegate>)orientationDelegate device:(CDV_iOSDevice)device
{
    // Use UILaunchImageFile if specified in plist.  Otherwise, use Default.
    NSString* imageName = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"UILaunchImageFile"];
    
    NSUInteger supportedOrientations = [orientationDelegate supportedInterfaceOrientations];
    
    // Checks to see if the developer has locked the orientation to use only one of Portrait or Landscape
    BOOL supportsLandscape = (supportedOrientations & UIInterfaceOrientationMaskLandscape);
    BOOL supportsPortrait = (supportedOrientations & UIInterfaceOrientationMaskPortrait || supportedOrientations & UIInterfaceOrientationMaskPortraitUpsideDown);
    // this means there are no mixed orientations in there
    BOOL isOrientationLocked = !(supportsPortrait && supportsLandscape);
    
    if (imageName)
    {
        imageName = [imageName stringByDeletingPathExtension];
    }
    else
    {
        imageName = @"Default";
    }
    
    // Add Asset Catalog specific prefixes
    if ([imageName isEqualToString:@"LaunchImage"])
    {
        if (device.iPhone4 || device.iPhone5 || device.iPad) {
            imageName = [imageName stringByAppendingString:@"-700"];
        } else if(device.iPhone6) {
            imageName = [imageName stringByAppendingString:@"-800"];
        } else if(device.iPhone6Plus) {
            imageName = [imageName stringByAppendingString:@"-800"];
            if (currentOrientation == UIInterfaceOrientationPortrait || currentOrientation == UIInterfaceOrientationPortraitUpsideDown)
            {
                imageName = [imageName stringByAppendingString:@"-Portrait"];
            }
        }
    }
    
    if (device.iPhone5)
    { // does not support landscape
        imageName = [imageName stringByAppendingString:@"-568h"];
    }
    else if (device.iPhone6)
    { // does not support landscape
        imageName = [imageName stringByAppendingString:@"-667h"];
    }
    else if (device.iPhone6Plus)
    { // supports landscape
        if (isOrientationLocked)
        {
            imageName = [imageName stringByAppendingString:(supportsLandscape ? @"-Landscape" : @"")];
        }
        else
        {
            switch (currentOrientation)
            {
                case UIInterfaceOrientationLandscapeLeft:
                case UIInterfaceOrientationLandscapeRight:
                    imageName = [imageName stringByAppendingString:@"-Landscape"];
                    break;
                default:
                    break;
            }
        }
        imageName = [imageName stringByAppendingString:@"-736h"];
        
    }
    else if (device.iPad)
    {   // supports landscape
        if (isOrientationLocked)
        {
            imageName = [imageName stringByAppendingString:(supportsLandscape ? @"-Landscape" : @"-Portrait")];
        }
        else
        {
            switch (currentOrientation)
            {
                case UIInterfaceOrientationLandscapeLeft:
                case UIInterfaceOrientationLandscapeRight:
                    imageName = [imageName stringByAppendingString:@"-Landscape"];
                    break;
                    
                case UIInterfaceOrientationPortrait:
                case UIInterfaceOrientationPortraitUpsideDown:
                default:
                    imageName = [imageName stringByAppendingString:@"-Portrait"];
                    break;
            }
        }
    }
    
    return imageName;
}

- (UIInterfaceOrientation)getCurrentOrientation
{
    UIInterfaceOrientation iOrientation = [UIApplication sharedApplication].statusBarOrientation;
    UIDeviceOrientation dOrientation = [UIDevice currentDevice].orientation;
    
    bool landscape;
    
    if (dOrientation == UIDeviceOrientationUnknown || dOrientation == UIDeviceOrientationFaceUp || dOrientation == UIDeviceOrientationFaceDown) {
        // If the device is laying down, use the UIInterfaceOrientation based on the status bar.
        landscape = UIInterfaceOrientationIsLandscape(iOrientation);
    } else {
        // If the device is not laying down, use UIDeviceOrientation.
        landscape = UIDeviceOrientationIsLandscape(dOrientation);
        
        // There's a bug in iOS!!!! http://openradar.appspot.com/7216046
        // So values needs to be reversed for landscape!
        if (dOrientation == UIDeviceOrientationLandscapeLeft)
        {
            iOrientation = UIInterfaceOrientationLandscapeRight;
        }
        else if (dOrientation == UIDeviceOrientationLandscapeRight)
        {
            iOrientation = UIInterfaceOrientationLandscapeLeft;
        }
        else if (dOrientation == UIDeviceOrientationPortrait)
        {
            iOrientation = UIInterfaceOrientationPortrait;
        }
        else if (dOrientation == UIDeviceOrientationPortraitUpsideDown)
        {
            iOrientation = UIInterfaceOrientationPortraitUpsideDown;
        }
    }
    
    return iOrientation;
}
- (CDV_iOSDevice) getCurrentDevice
{
    CDV_iOSDevice device;
    
    UIScreen* mainScreen = [UIScreen mainScreen];
    CGFloat mainScreenHeight = mainScreen.bounds.size.height;
    CGFloat mainScreenWidth = mainScreen.bounds.size.width;
    
    int limit = MAX(mainScreenHeight,mainScreenWidth);
    
    device.iPad = (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad);
    device.iPhone = (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone);
    device.retina = ([mainScreen scale] == 2.0);
    device.iPhone4 = (device.iPhone && limit == 480.0);
    device.iPhone5 = (device.iPhone && limit == 568.0);
    // note these below is not a true device detect, for example if you are on an
    // iPhone 6/6+ but the app is scaled it will prob set iPhone5 as true, but
    // this is appropriate for detecting the runtime screen environment
    device.iPhone6 = (device.iPhone && limit == 667.0);
    device.iPhone6Plus = (device.iPhone && limit == 736.0);
    
    return device;
}
@end
