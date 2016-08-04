// server.js

var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var methodOverride = require('method-override');
var mongoose = require('mongoose');
var db = require('./config/db');
var router = require('./app/router');

mongoose.connect(db.url);

app.use(bodyParser.json());
app.use(bodyParser.json({type: 'application/vnd.api+json'}));
app.use(bodyParser.urlencoded({extended: true}));
app.use(methodOverride('X-HTTP-Method-Override'));

app.use(express.static(__dirname + '/public'));

require('./app/routes')(app); // configure our routes
app.use('/api', router);

var port = process.env.PORT || 8081;
app.listen(port);
console.log('Server started on port ' + port);

module.exports = app;


