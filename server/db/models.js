var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var UserSchema = new Schema({
    _id: {type: Number, required: true, unique: true, index: true},//android id
    pass: {type: String, required: true},
    rate: {type: Number, required: true, index: true} });

var QueueSchema = new Schema({
    _id: {type: Number, required: true, unique: true, index: true},//1=better or 2
    queue: [String]
});

module.exports.user = mongoose.model("User", UserSchema);
module.exports.queue = mongoose.model("Queue", QueueSchema);