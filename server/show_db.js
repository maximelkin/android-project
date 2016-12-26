var mongoose = require('mongoose');
var models = require('./db/models');
var user = models.user;
var config = require("./db/config.json");

mongoose.connect(config.mongooseURL, { config: { autoIndex: false } });

var db = mongoose.connection;
db.on('error', console.error.bind(
    console, 'connection error:'));

user.find({}).lean().exec(function(err, users){
    console.log(err);
    console.log(user);
});