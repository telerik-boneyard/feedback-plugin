'use strict';

var cordova = require('cordova'),
    API_BASE_URL = 'https://platform.telerik.com/feedback/api/v1',    
    Feedback = function () {
        this.initialize = function (apiKey, options) {
            var that = this;
            if (typeof apiKey !== 'string' || apiKey === '') {
                console.log('Please use a proper apiKey for initialization');
                return;
            }

            if (options) {
                options.enableShake = options.enableShake === 'undefined' ? true : options.enableShake;
            } else {
                options = {
                    enableShake: true
                };
            }

            var apiUrl = options.apiUrl || API_BASE_URL;

            cordova.exec(function successCallback(result) {
                if (options.enableShake) {
                    var shake = new Shake();
                    shake.start();

                    window.addEventListener('shake', function () {
                        that.showFeedback();
                    }, false);
                }
            }, function errorCallback(error) {
                //not used right now. reserved for future use
            }, 'AppFeedback', 'initialize', [apiKey, apiUrl]);
        };
        this.showFeedback = function () {
            cordova.exec(function successCallback(result) {
                //not used right now. reserved for future use
            }, function errorCallback(error) {
                //not used right now. reserved for future use
            }, 'AppFeedback', 'showFeedback', []);
        };
    },
    feedback = new Feedback();
    
window.addEventListener('deviceready', function () {    
    cordova.exec(function(data) {
      if(data.productKey !== "YourKeyHere"){        
        var feedbackOptions = {
            enableShake: data.enableShake,
            apiUrl: data.apiUrl
        };
        feedback.initialize(data.productKey, feedbackOptions);        
      } 
    }, function(err) { 
       window.data = err;
       console.log('Unable to read required plugin variables: ' + err);
      }, 'AppFeedback', 'GetVariables', [ 'productKey', 'apiUrl', 'enableShake' ]);

}, true);

module.exports = feedback;