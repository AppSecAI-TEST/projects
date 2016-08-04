//special module listening for mqtt messages from device and putting them into
//mongodb database. Runs on its own

var express = require('express');
var app = express();
var mongoose = require('mongoose');

var db = require('./config/db');
var mqtt = require('./config/mqtt');

mongoose.connect(db.url); 
mqtt.connect();

var port = process.env.PORT || 8082;
app.listen(port);                           
console.log('Server started on port ' + port);
       
module.exports = app;

