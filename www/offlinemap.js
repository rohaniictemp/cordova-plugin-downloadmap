var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'offlinemap', 'coolMethod', [arg0]);
};

exports.initializeDownload = function (arg0, success, error) {
    exec(success, error, 'offlinemap', 'initializeDownload', [arg0]);
}