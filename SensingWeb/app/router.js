//api router

var express = require('express');
var router = express.Router();              
var Record = require('../app/models/record');

router.get('/', function (req, res) {
	res.json({message: 'welcome to our api'});
});

router.route('/wifi-names')
		.get(function (req, res) {
			Record.aggregate(
					[
						{'$unwind': '$wifi'},
						{'$group': {'_id': '$wifi.Name', 'count': {'$sum': 1}}},
						{'$match': {'_id': {'$ne': ''}}},
						{'$sort': {'_id': 1}},
						{'$project': {'_id': 1}}
					]
					,
					function (err, result) {
						if (err)
							res.send(err);
						res.json(result);
					}
			);
		});

router.route('/wifi-top/:limit')
		.get(function (req, res) {
			Record.aggregate(
					[
						{'$unwind': '$wifi'},
						{'$group': {'_id': '$wifi.Name', 'count': {'$sum': 1}}},
						{'$match': {'_id': {'$ne': ''}}},
						{'$sort': {'count': -1, '_id': 1}},
						{'$limit': Number(req.params.limit)},
						{'$project': {'_id': 1}}
					]
					,
					function (err, result) {
						if (err)
							res.send(err);
						res.json(result);
					}
			);
		});

router.route('/wifi/:wifi_id')
		.get(function (req, res) {
			Record.aggregate(
					[
						{'$unwind': '$wifi'},
						{'$match': {'wifi.Name': req.params.wifi_id}},
						{'$project': {'wifi.Value': 1, 'location': 1}}
					]
					,
					function (err, result) {
						if (err)
							res.send(err);
						res.json(result);
					}
			);
		});
		
router.route('/wifi-all')
		.get(function (req, res) {
			Record.aggregate(
					[
						{'$unwind': '$wifi'},
						{'$project': {'wifi.Value': 1, 'location': 1}}
					]
					,
					function (err, result) {
						if (err)
							res.send(err);
						res.json(result);
					}
			);
		});

module.exports = router;
