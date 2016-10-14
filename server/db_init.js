//create 2 queues
var mongoose = require('mongoose');
var queue = require('./db/models').queue;
function callback(err){
    if (err){
        console.error(err);
    }
}
mongoose.connect('mongodb://localhost:2934/local', { config: { autoIndex: false } });

var db = mongoose.connection;
db.on('error', console.error.bind(
    console, 'connection error:'));

queue.create({_id : 1, queue: []}, callback);
queue.create({_id : 2, queue: []}, callback);
db.close();