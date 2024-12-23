
const mongoose = require('mongoose');

mongoose.connect("mongodb://localhost/app").then(_=>{
    console.log("veritabına bağlandı");
}).catch(err =>{
    console.log("bağlantı hatası"+err);
});

const Schema = mongoose.Schema;

const commentSchema = new Schema({
    commentId:{
        type:String, 
        required:true
    },
    bookId:{
        type:String, 
        required:true
    },
    userId:{
        type:String, 
        required:true
    },
    imageUrl:{
        type:String, 
        required:true
    },
    username:{
        type:String, 
        required:true
    },
    commentText:{
        type:String, 
        required:true
    },
    bookName:{
        type:String, 
        required:true
    },
    bookImageUrl:{
        type:String, 
        required:true
    },
    CommentModel:{
        type:[String],
        required:true
    }


},{collection : 'comments', timestamps : true});

const comment = mongoose.model("comment",commentSchema);
module.exports = comment;




