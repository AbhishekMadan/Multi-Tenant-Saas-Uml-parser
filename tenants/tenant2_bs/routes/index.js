var formidable = require('formidable');
var fs = require('fs');
var unzip = require('unzip');
var exec = require('child_process').exec;
var errors = require('errors');
var dbconn = require('./dbconn');


var all = function(req, res, next) {
    // add details of what is allowed in HTTP request headers to the response headers
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'POST, GET, PUT, DELETE, OPTIONS');
    res.header('Access-Control-Allow-Credentials', false);
    res.header('Access-Control-Max-Age', '86400');
    res.header('Access-Control-Allow-Headers', 'X-Requested-With, X-HTTP-Method-Override, Content-Type, Accept');
    // the next() function continues execution and will move onto the requested URL/URI
    next();
};

var options = function(req, res) {
    res.sendStatus(200);
};

var health_check = function(req, res) {
	res.send("Health check successful!");
};

var upload_get = function (req, res) {
	res.render('fileupload');
};

var upload_post = function (req, res) {
	 var form = new formidable.IncomingForm();

	    form.parse(req);
	    form.on('fileBegin', function (name, file){
	    	var dir = './public/uploads/';
	        if (!fs.existsSync(dir)){
	        	fs.mkdirSync(dir);
	        }
	        file.path = dir + file.name;
	    });

	    form.on('file', function (name, file){
	    	// Unzip the file
	    	fs.createReadStream(file.path)
	    		.pipe(unzip.Extract({ path: './public/uploads/' }))
	    		.on('close',function(err) {
	    			
	    			if (err){
	    				res.send(new errors.errors.Http500Error());
	    			} else {
	    				// run the jar
		    			var extractedFilePath = file.path.substring(1,file.path.lastIndexOf('.'));
		    			var outputFile = '.'+extractedFilePath+'/output.png';
		    			
		    			exec('java -jar ./public/uml_parser/UmlParser.jar '+'.'+extractedFilePath+' '+outputFile,
		    					function (error, stdout, stderr){
		    				    if(error !== null){
		    				    	console.log("Error -> "+error);
		    				    	res.send({'path':'./public/images/server_error.png'});
		    				    } else {
		    				    	console.log("Uml Diagram Generate");
		    				    	console.log(outputFile);
		    				    	res.send({'path':outputFile});
		    				    }
		    			});
	    			}
	    		});
	    });
};

var store_grade = function(req, res) {
	//console.log(req.body);
	dbconn.persistGrade(req.body, function(err) {
		if (err) {
			res.send(new errors.Http500Error());
		}
		res.send({status:"success"});
	});
};

module.exports = {
		options,
		health_check,
		all,
		upload_get,
		upload_post,
		store_grade
}