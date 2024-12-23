


const mongoose = require('mongoose');

mongoose.connect("mongodb://localhost/app").then(_=>{
    console.log("veritabına bağlandı");
}).catch(err =>{
    console.log("bağlantı hatası"+err);
});

const Schema = mongoose.Schema;

const bookSchema = new Schema({
    id:{

        type:String, 
        required:true,
        trim:true
    },
    title:{
        type:String, 
        required:true,
        trim:true
    },
    description:{
        type: String, 
        required: true,
        trim:true
    },
    imageUrl:{
        type:String, 
        required:true,
        trim:true
    },
    date:{
        type:String, 
        required:true,
        trim:true
    },
    author:{
        type: String, 
        required: true,
        trim:true
    },
    pages:{
        type:Number, 
        required:true,
        trim:true
    },
    price:{
        type:Number, 
        required:true,
        trim:true
    },
    language:{
        type:String, 
        required:true,
        trim:true
    },
    genres:{
        type: [String], 
        required: true,
        trim:true
    }

},{collection : 'books', timestamps : true});

const book = mongoose.model("book",bookSchema);
module.exports = book;




