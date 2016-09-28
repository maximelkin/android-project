var mongoose = require('mongoose');
var models = require('./models');
var crypto = require('crypto');
var user = models.user,
    queue = models.queue;

var def_rate = 101;//throw out this file
//two divisions
var border = 100

mongoose.connect('mongodb://localhost/local', { config: { autoIndex: false } });
var db = mongoose.connection;
db.on('error', console.error.bind(
    console, 'connection error:'));


function hashing(password) {
    return crypto.createHash('sha256').
        update(password).digest('hex');
}


function createUser(androidId, pass, callback){
    if (typeof(androidId) != 'number' || pass != 'string')
        return callback(new Error("dangerous data"));
    user.create({
        _id: androidId,
        pass: hashing(pass),
        rate: def_rate
    }, callback);
}

function checkPass(androidId, pass, callback){
    user.findOne({_id: androidId}, function(err, user){
        callback(err || user.pass != pass);
    });
}


function clearUsers(callback){
    user.update({}, {rate: def_rate}, callback);
}


function updateRate(androidId, ratingChange, callback){
    var change = ratingChange * 100 - 50
    if (typeof(androidId) != 'number' || typeof(score) != 'number')
        return callback(new Error('dangerous data'));
    user.updateOne({_id: androidId}, {rate: {$inc: change}}, callback);
}

//------
//queues
function addToQueue(socket, callback){
    if (typeof(androidId) != 'number')
        return callback(new Error('dangerous data'));

    queue.update({_id: (user.rate >= 100)}, {queue: {$addToSet: socket.id}}, callback);
}

function pullQueue(id, callback){
    findOneAndUpdate({_id: id}, {queue: []}, {new: false}, callback);
}

db.exports.createUser = createUser;
db.exports.checkPass = checkPass;
db.exports.clearUsers = clearUsers;
db.exports.updateRate = updateRate;
db.exports.addToQueue = addToQueue;
db.exports.pullQueue = pullQueue;