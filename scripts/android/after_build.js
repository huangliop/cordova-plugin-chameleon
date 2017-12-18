#!/usr/bin/env node

/**
    将删除后的www文件拷贝回来，为了解决在删除插件的时候，由于没有assets/www目录而无法删除插件的问题
*/
module.exports = function (context) {
    var exec = require('child_process').execSync;

    var s = context.cmdLine.split('\\');
    var isRelease = (s[s.length - 1]).includes('release');
    var edit = isRelease ? 'release' : 'debug';
    var apkName=buildAppName()
        try{exec('mkdir outputs',{stdio:[0]});}catch(e){};
    if (process.platform == 'win32') {
        exec('move platforms\\android\\www platforms\\android\\assets\\');
        exec('del platforms\\android\\assets\\web_lib.so');
        try {
            exec('copy platforms\\android\\build\\outputs\\apk\\android-' + edit + '.apk .\\outputs\\'+apkName+'_'+edit+'.apk',{stdio:[0]})
        } catch (e) {
            exec('copy platforms\\android\\build\\outputs\\apk\\android-armv7-' + edit + '.apk .\\outputs\\'+apkName+'_'+edit+'.apk',{stdio:[0]})
        }
    } else {
        exec('mv ./platforms/android/www ./platforms/android/assets',{stdio:[0]});
        try{
            exec('cp ./platforms/android/build/outputs/apk/android-'+edit+'.apk ./outputs/'+apkName+'_'+edit+'.apk',{stdio:[0]});
        }catch(e){
            exec('cp ./platforms/android/build/outputs/apk/android-armv7-'+edit+'.apk ./outputs/'+apkName+'_'+edit+'.apk',{stdio:[0]});
        }
        exec('rm -rf ./platforms/android/assets/web_lib.so',{stdio:[0]});
    }
}

function buildAppName(){
    var fs=require('fs');
    var manifestFile=process.platform=='win32'?'platforms\\android\\AndroidManifest.xml':'./platforms/android/AndroidManifest.xml';
    var manifast=fs.readFileSync(manifestFile,'utf-8');

    var tempS1=manifast.split('android:versionCode="')[1];
    var versionCode=tempS1.substring(0,tempS1.indexOf('"'));
    var tempS2=manifast.split('android:versionName="')[1];
    var version=tempS2.substring(0,tempS2.indexOf('"'));

    var config=fs.readFileSync('config.xml','utf-8');
    var tempS=config.split('<name>')[1];
    var name=tempS.substring(0,tempS.indexOf('</name>'));
    return name+'_'+version+'_'+versionCode+'_'+Math.floor(new Date().getTime()/60000);
}
