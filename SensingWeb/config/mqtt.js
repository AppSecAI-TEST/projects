//mqtt subscribing functionality

var mqtt = require('mqtt');
var Record = require('../app/models/record');

module.exports = {
	connect: function () {
		var client = mqtt.connect('tcp://localhost:1883');

		client.on('connect', function () {
			client.subscribe(process.env.TOPIC || 'sensing/+/data', function (err, granted) {
				if (err) {
					console.log(err);
				}else{
					console.log('subscribed to ' + granted[0].topic);
				}
			});

		});

		client.on('message', function (topic, message) {
			console.log(message + ' from topic: ' + topic)
			// message is Buffer 
			var data = JSON.parse(message.toString()).Data;
			for (var i = 0; i < data.length; i++) {
				console.log(data[i]);
				Record.create(data[i], function (err, record) {
					if (err) {
						console.log(err);
					} else {
						console.log('Saved:' + record.toString());
					}
				});
			}
		});
	}
};
