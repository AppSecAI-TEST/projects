//request router

module.exports = function (app) {

	app.get('/', function (req, res) {
		res.sendFile('public/views/index.html', {root: __dirname + '/../'});
	});

	app.get('/api*', function (req, res, next) {
		res.header("Access-Control-Allow-Origin", "*");
		res.header("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
		res.header("Access-Control-Allow-Methods", "GET, PUT, POST");
		return next();
	});

};
