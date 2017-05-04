var ejs = require('ejs');
var mysql = require('mysql');

// Put your mysql configuration settings - user, password, database and port
function getConnection() {
	var connection = mysql.createConnection({
		host : '*',
		user : 'root',
		password : '*',
		database : 'tenantData',
		port : 3306
	});
	return connection;
}

var persistGrade = function (obj, callback) { 
	var connection = getConnection();
	var query1 = "Select TenantId from TenantTable where TenantName = ?";
	connection.query(query1,[obj.tenant], function(err, rows, fields) {
		if (err) {
			console.log("ERROR: " + err.message);
			console.log("\nConnection closed...");
			connection.end();
			callback(true);
		} else { 
			//after getting tenant Id store comments in TenantData Table
			var query2 = "Insert into TenantData values (?,?,?)";
			var recordId = Math.floor((Math.random() * 100000) + 1);
			var tntId = rows[0].TenantId;
			connection.query(query2,[tntId,obj.comments,recordId], function(err, rows, fields) {
				if(err) {
					console.log("ERROR: " + err.message);
					console.log("\nConnection closed...");
					connection.end();
					callback(true);
				} else {
					connection.query("Insert into TenantExtra values (?,?,?)",
								[recordId, "pointer_received", obj.pointer_received], function(err, rows, fields) {
							if(err) {
								console.log("ERROR: " + err.message);
								console.log("\nConnection closed...");
								connection.end();
								callback(true);
							}
							connection.query("Insert into TenantExtra values (?,?,?)",
									[recordId, "pointer_total", obj.pointer_total], function(err, rows, fields) {
								if(err) {
									console.log("ERROR: " + err.message);
									console.log("\nConnection closed...");
									connection.end();
									callback(true);
								}
								console.log("\nConnection closed...");
								connection.end();
								callback(false);
						});
					});
				}
			});
		}
	});
}

exports.persistGrade = persistGrade;