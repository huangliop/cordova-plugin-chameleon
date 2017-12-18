#!/usr/bin/env node

module.exports = function(context){ 
    if(context.opts.plugins[0]!='cordova-plugin-foss-chameleon')return;
    var fs = require('fs');
    var searchFile = function (name, dir) {
        fs.readdir(dir, function (err, files) {
            if (err) {
                return console.error(err);
            } else {
                var tDir = dir;
                for (var i = 0; i < files.length; i++) {
                    if (files[i] === name) {
                        modifyIt(tDir + "/" + name);
                    } else if (files[i].indexOf('.java') === -1) {
                        searchFile(name, tDir + "/" + files[i]);
                    }

                }

            }
        })
    }

    var modifyIt = function (file) {
        fs.readFile(file, 'utf-8', function (err, data) {
            data = data.replace('if(appView==null) init();\n\t\tInitApp.getInstace().initH5(this,preferences,launchUrl)', 'loadUrl(launchUrl)');
            data = data.replace('import com.foreveross.chameleon.cordovaplugin.InitApp;\n', '');
            fs.writeFile(file, data, 'utf-8', function (err) {
                if (err) {
                    return console.log("MainActivity.java revert Error" + err);
                } else {
                    return console.info('MainActivity.java revert Success!');
                }
            });
        })
    }


    searchFile('MainActivity.java', 'platforms/android/src');

}