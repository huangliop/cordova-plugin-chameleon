var exec = require('cordova/exec');
       module.exports = { 
       removeWebviewCache: function(quit) {
               exec(function(){}, undefined, "ChameleonUtil", "removeWebviewCache", [quit]);
       }
   };