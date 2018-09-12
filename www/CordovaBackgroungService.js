var exec = require('cordova/exec');

exports.runService = function (arg0, success, error) {
    exec(success, error, 'CordovaBackgroungService', 'runService', [arg0]);
};
