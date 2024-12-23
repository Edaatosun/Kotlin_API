


const mongoose = require('mongoose');

mongoose.connect("mongodb://localhost/app").then(_=>{
    console.log("veritabına bağlandı");
}).catch(err =>{
    console.log("bağlantı hatası"+err);
});

const Schema = mongoose.Schema;

const userSchema = new Schema({
    age:{
        type:String, 
        required:true
    },
    email:{
        type:String, 
        required:true
    },
    favoriteBooks:{
        type: [String], 
        required: true
    },
    id:{
        type:String, 
        required:true
    },
    imageUrl:{
        type:String, 
        required:true
    },
    toReadBooks:{
        type: [String], 
        required: true
    },
    username:{
        type:String, 
        required:true
    }

},{collection : 'users', timestamps : true});

const user = mongoose.model("user",userSchema);
module.exports = user;




