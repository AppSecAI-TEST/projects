//database model

//{"location":{"Longitude":24.82229219,"Latitude":60.18681241}}, "light": 2, "wifi":[{"Name":aalto open, "Value":99}]}

var mongoose = require('mongoose');

var schema = new mongoose.Schema({location: {Longitude: Number, Latitude: Number}, light: Number, wifi: [{Name: String, Value: Number}], Timestamp: String});
module.exports = mongoose.model('Record', schema);


