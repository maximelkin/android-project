var sqlite = require('sqlite3').verbose();
var db = new sqlite.Database('users');

var crypto = require('crypto');
var config = require("./config.json");

var scheme = "($id, $pass, $rate, $username)";

db.run("CREATE TABLE if not exists users "
    + "(id INTEGER NOT NULL PRIMARY KEY, "
    + "pass TEXT NOT NULL, "
    + "rate INTEGER NOT NULL, "
    + "username TEXT NOT NULL)", function (err) {
        if (err)
            console.error(err);
    });



function hashing(password) {
    return crypto.createHash('sha256').
        update(password).digest('hex');
}

function createUser(androidId, pass, username, callback) {
    if (typeof (androidId) != 'string' || typeof (pass) != 'string')
        return callback(new Error("dangerous data"));
    db.run("INSERT INTO users (id, pass, rate, username) VALUES " + scheme, {
        $id: androidId,
        $pass: hashing(pass),
        $rate: config.defRate,
        $username: username
    }, callback);
}

function getUsername(androidId, callback) {
    db.get("SELECT username FROM users WHERE id = ?", androidId, callback);
}

function deleteUser(androidId, callback) {
    if (typeof (androidId) != 'string')
        return callback(new Error('dangerous data'));
    db.run("DELETE FROM users WHERE id = ?", androidId, callback);
}

function checkPass(androidId, pass, callback) {
    db.get("SELECT pass FROM users WHERE id = ?", androidId, function (err, row) {
        callback(err || row == null || row.pass != hashing(pass));
    });
}

function clearUsers(callback) {
    db.all("UPDATE users SET rate = ?", config.defRate, callback);
}

function updateRate(androidId, isWon, callback) {
    if (typeof (androidId) != 'string')
        return callback(new Error('dangerous data'));
    var change = -config.deltaRatingChange;
    if (isWon)
        change = config.deltaRatingChange;
    db.run("UPDATE users SET rate = (rate + ?) WHERE id = ?", change, androidId, callback);
}

module.exports.createUser = createUser;
module.exports.deleteUser = deleteUser;
module.exports.checkPass = checkPass;
module.exports.clearUsers = clearUsers;
module.exports.updateRate = updateRate;
module.exports.getUsername = getUsername;