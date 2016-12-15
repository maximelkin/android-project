var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var UserSchema = new Schema({
    _id: {type: String, required: true, unique: true, index: true},//android id
    pass: {type: String, required: true},
    rate: {type: Number, required: true, index: true} });

module.exports.user = mongoose.model("User", UserSchema);