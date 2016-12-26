var mongoose = require('mongoose');
var models = require('./models');
var crypto = require('crypto');
var user = models.user;
var config = require("./config.json");

mongoose.connect(config.mongooseURL, { config: { autoIndex: false } });

var db = mongoose.connection;
db.on('error', console.error.bind(
    console, 'connection error:'));


function hashing(password) {
    return crypto.createHash('sha256').
        update(password).digest('hex');
}


function createUser(androidId, pass, username, callback) {
    if (typeof (androidId) != 'string' || typeof (pass) != 'string')
        return callback(new Error("dangerous data"));
    user.create({
        _id: androidId,
        pass: hashing(pass),
        rate: config.defRate,
        username: username
    }, callback);
}

function deleteUser(androidId, callback) {
    if (typeof (androidId) != 'string')
        return callback(new Error('dangerous data'));
    user.remove({ _id: androidId }, callback);
}

function checkPass(androidId, pass, callback) {
    user.findOne({ _id: androidId }, function (err, user) {
        callback(err || user == null || user.pass != hashing(pass));
    });
}


function clearUsers(callback) {
    user.update({}, { rate: config.defRate }, callback);
}


function updateRate(androidId, isWon, callback) {
    if (typeof (androidId) != 'string')
        return callback(new Error('dangerous data'));
    var change = -config.deltaRatingChange;
    if (isWon)
        change = config.deltaRatingChange;
    user.updateOne({ _id: androidId }, { rate: { $inc: change } }, callback);
}

module.exports.createUser = createUser;
module.exports.deleteUser = deleteUser;
module.exports.checkPass = checkPass;
module.exports.clearUsers = clearUsers;
module.exports.updateRate = updateRate;