#!/usr/bin/env node

module.exports = function (context) {
    if (context.opts.plugins[0] != 'cordova-plugin-foss-chameleon') return;
    var fs = require('fs');
    var searchFile = function (name, dir,cb) {
        fs.readdir(dir, function (err, files) {
            if (err) {
                return ;
            } else {
                var tDir = dir;
                for (var i = 0; i < files.length; i++) {
                    if (files[i] === name) {
                        cb(tDir + "/" + name);
                    } else if (files[i].indexOf('.m') === -1) {
                        searchFile(name, tDir + "/" + files[i],cb);
                    }

                }

            }
        })
    }
 
    var modifyMainViewController = function (file) {
        fs.readFile(file, 'utf-8', function (err, data) {
            // data = data.replace('   _commandDelegate = [[MainCommandDelegate alloc] initWithViewController:self];'
            //                     , '// _commandDelegate = [[MainCommandDelegate alloc] initWithViewController:self];');
            data = data.replace( 'NSURL* url=[[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];\n'
        +'\turl=[[url URLByAppendingPathComponent:@"www" isDirectory:YES] URLByAppendingPathComponent:resourcepath];\n'
        +'\treturn [url relativePath];'
                                ,'return [super pathForResource:resourcepath];');
            
             fs.writeFile(file, data, 'utf-8', function (err) {
                if (err) {
                    return console.log("ViewController.m revert Error" + err);
                } else {
                    return console.info('ViewController.m revert Success!');
                }
            });
        })
    }
 
    searchFile('MainViewController.m', 'platforms/ios',modifyMainViewController);
}