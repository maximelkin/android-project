var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var UserSchema = new Schema({
    _id: {type: String, required: true, unique: true, index: true},//android id
    pass: {type: String, required: true, index: false},
    rate: {type: Number, required: true, index: true},
    username: {type: String, required: true, index: false}
});

module.exports.user = mongoose.model("User", UserSchema);