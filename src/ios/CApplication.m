//
//  CApplication.m
//  chameleon
//
//  Created by Justin Yip on 6/16/14.
//
//

#import "CApplication.h"

static NSString *kInstalledVersionKey = @"InsatalldVersion";
static NSString *kInstalled = @"Installed";

NSString* const kAppResetNotification = @"kAppResetNotification";
NSString* const kWWWFolderName = @"www";

@implementation CApplication

+ (instancetype)sharedApplication
{
    static CApplication *instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[CApplication alloc] init];
    });
    
    return instance;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reset) name:kAppResetNotification object:nil];
    }
    return self;
}


/**
 * 将应用包里的预装目录复制到Documents里
 * TODO：预装模块如果版本大于现有版本，提示后覆盖更新
 */
-(BOOL)launch
{
    NSFileManager *fs = [NSFileManager defaultManager];
    NSDictionary *appInfoDict = NSBundle.mainBundle.infoDictionary;
    NSString *currentVersion = appInfoDict[@"CFBundleVersion"];
    
    NSError *error = nil;
    NSURL *runtimeWWWURL = [self webRuntimeDirectory];
    NSLog(@"%@",[runtimeWWWURL absoluteString]);
    BOOL success = [runtimeWWWURL setResourceValue: [NSNumber numberWithBool: YES]
                                            forKey: NSURLIsExcludedFromBackupKey error: &error];
    if(!success){
        NSLog(@"Error excluding %@ from backup %@", [runtimeWWWURL lastPathComponent], error);
    }
    
    NSString *installedFlagFilePath = [[[self documentsDirectory] URLByAppendingPathComponent:@"Installed"] path];
    if ([fs fileExistsAtPath:installedFlagFilePath]) {
        // 获取版本
        NSString* str = [NSString stringWithContentsOfURL:[NSURL fileURLWithPath:installedFlagFilePath] encoding:NSUTF8StringEncoding error:nil];
        if ([str isEqualToString:currentVersion]) {
            return NO;
        }
    };
    
    NSLog(@"Install...");
    
    // 删除app目录
    if ([fs fileExistsAtPath:[self webRuntimeDirectory].path]) {
        [fs removeItemAtURL:[self webRuntimeDirectory] error:nil];
    }
    
    [self installWWWAndApp];
    
    //标记为已安装,并写入版本号
    if (![fs fileExistsAtPath:installedFlagFilePath]) {
        [[NSFileManager defaultManager] createFileAtPath:installedFlagFilePath contents:nil attributes:nil];
    }
    if ([fs fileExistsAtPath:installedFlagFilePath]) {
        [currentVersion writeToFile:installedFlagFilePath atomically:YES encoding:NSUTF8StringEncoding error:nil];
    }
    return YES;
}

- (void)installWWWAndApp
{
    BOOL result = YES;
    NSError *error = nil;
    NSFileManager *fs = [NSFileManager defaultManager];
    
    //应用内www目录
    NSURL *bundledWWWURL = [self bundledWWWDirectory];
    //Documents/www目录(H5沙箱运行时)
    NSURL *runtimeWWWURL = [self webRuntimeDirectory];
    
    if ([fs fileExistsAtPath:bundledWWWURL.path]) {
        NSLog(@"copy www to Documents/%@", kWWWFolderName);
        result = [fs copyItemAtURL:bundledWWWURL toURL:runtimeWWWURL error:&error];
    }
    // 设置Document目录下的App目录不要同步至iCloud
    BOOL success = [runtimeWWWURL setResourceValue: [NSNumber numberWithBool: YES]
                                            forKey: NSURLIsExcludedFromBackupKey error: &error];
    if(!success){
        NSLog(@"Error excluding %@ from backup %@", [runtimeWWWURL lastPathComponent], error);
    }
}
//应用文档根目录
- (NSURL *)documentsDirectory
{
    return [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
}

- (NSURL *)bundledWWWDirectory
{
    return [[NSBundle mainBundle] URLForResource:kWWWFolderName withExtension:nil];
}

- (NSURL *)bundledAppDirectory
{
    return [[NSBundle mainBundle] URLForResource:kWWWFolderName withExtension:nil];
}

- (NSURL *)webRuntimeDirectory
{
    return [[self documentsDirectory] URLByAppendingPathComponent:kWWWFolderName isDirectory:YES];
}

@end
