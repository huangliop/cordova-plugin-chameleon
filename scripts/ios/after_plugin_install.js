#!/usr/bin/env node

module.exports = function (context) {
     var fs = require('fs');
    var searchFile = function (name, dir,callback) {
        fs.readdir(dir, function (err, files) { 
            if (err) {
                
                return ;
            } else {
                var tDir = dir;
                for (var i = 0; i < files.length; i++) {
                    if (files[i] === name) {
                        callback(tDir + "/" + name);
                    } else if (files[i].indexOf('.m') === -1) {
                        searchFile(name, tDir + "/" + files[i],callback);
                    }

                }

            }
        })
    }

   
    var modifyMainViewController = function (file) {
        fs.readFile(file, 'utf-8', function (err, data) {
            // data = data.replace('// _commandDelegate = [[MainCommandDelegate alloc] initWithViewController:self];'
            //                     , '   _commandDelegate = [[MainCommandDelegate alloc] initWithViewController:self];');
            data = data.replace('return [super pathForResource:resourcepath];'
                                , 'NSURL* url=[[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];\n'
        +'\turl=[[url URLByAppendingPathComponent:@"www" isDirectory:YES] URLByAppendingPathComponent:resourcepath];\n'
        +'\treturn [url relativePath];');
            
             fs.writeFile(file, data, 'utf-8', function (err) {
                if (err) {
                    return console.log("ViewController.m Modify Error" + err);
                } else {
                    return console.info('ViewController.m Modify Success!');
                }
            });
        })
    }  
    searchFile('MainViewController.m', 'platforms/ios',modifyMainViewController);
}