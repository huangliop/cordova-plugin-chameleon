#!/usr/bin/env node

var fs=require('fs');
var cp=require('child_process');  
var os=require('os'); 

console.log('Zip the WWW folder...');
if(os.platform()==='darwin'){
    cp.execSync('cp -R ./www ./platforms/android/');
    cp.execSync("zip  -r -m 'web_lib.so' './www'", {cwd:'./platforms/android/assets'});
}else{
    cp.execSync('7z a -tzip ./platforms/android/assets/web_lib.so ./platforms/android/assets/www');
    try{
        cp.execSync('rmdir /s /q platforms\\android\\www');
    }catch(e){};
    cp.execSync('move  platforms\\android\\assets\\www platforms\\android');  
}
console.log('WWW compressione completata!!!');