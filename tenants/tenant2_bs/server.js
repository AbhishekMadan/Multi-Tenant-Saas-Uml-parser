/**
 * Bhakti
 */
var express = require('express');
var bodyParser = require('body-parser');
var formidable = require('formidable');
var index = require('./routes/index');
var http = require('http');
var path = require('path');
var morgan = require('morgan');

var app = express();

app.set('port', process.env.PORT || 9091);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(bodyParser.urlencoded({'extended':'true'}));            // parse application/x-www-form-urlencoded
app.use(bodyParser.json());                                     // parse application/json
app.use(bodyParser.json({ type: 'application/vnd.api+json' })); // parse application/vnd.api+json as json
app.use(express.static(path.join(__dirname, 'public')));
app.use('/public', express.static('public'))
app.use(morgan('tiny'));
app.use(function(req, res, next) {
	  req.headers['if-none-match'] = 'no-match-for-this';
	  next();    
});


app.all('*',index.all);
app.options('*',index.options);
app.get('/', index.health_check);
app.get('/upload', index.upload_get);
app.post('/upload', index.upload_post);
app.post('/grade_submission', index.store_grade);

http.createServer(app).listen(app.get('port'), function(){
	  console.log('Express server listening on port ' + app.get('port'));
});