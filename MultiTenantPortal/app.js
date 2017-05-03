var express = require('express');
var bodyParser = require('body-parser');
var routes = require('./app/routes');
var morgan = require('morgan');
path = require('path');

var app = express()

port = process.env.PORT || 3000;
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');

app.use(bodyParser.urlencoded({'extended':'true'}));            // parse application/x-www-form-urlencoded
app.use(bodyParser.json());                                     // parse application/json
app.use(bodyParser.json({ type: 'application/vnd.api+json' })); // parse application/vnd.api+json as json
app.use(morgan('tiny'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(function(req, res, next) {
	  req.headers['if-none-match'] = 'no-match-for-this';
	  next();    
});

app.use('/',routes);

app.listen(port, function () {
  console.log('Server running at port: '+port);
});