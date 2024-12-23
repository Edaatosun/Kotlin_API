const express = require("express");
const user = require("./model");
const book = require("./modelBook");
const comment = require("./modelComment");
const comment2 = require("./modelBookComment");

const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const app = express();


// JSON verilerini işlemek için middleware ekleyin
app.use(bodyParser.json());  // Gelen JSON verilerini işler



// app.use(bodyParser.json()); // JSON verileri okumak için gerekli

const cors = require("cors");

app.use(express.json());

app.use(cors()); // cors middleware'ını düzgün bir şekilde çağırıyoruz

app.use(express.urlencoded({
    extended: true
}));

app.get("/", (req, res) => {
    res.send("Merhaba");
});

app.get("/users/:userId",async(req,res)=>{
    const userId = req.params.userId;
    
    await user.findOne({ id: userId }).then((foundUser) => {
        if (foundUser) {
            res.json(foundUser); 
        } else {
            res.status(404).send("Kullanıcı bulunamadı");
        }
    }).catch((err) => {
        res.status(500).send("Veritabanı hatası: " + err);
    });


});

app.get("/user/:userId/favorites",async(req,res)=>{
    const userId = req.params.userId;
    
    await user.findOne({ id: userId }).then((foundUser) => {
        if (foundUser) {
            res.json(foundUser.favoriteBooks); 
        } else {
            res.status(404).send("Kullanıcı bulunamadı");
        }
    }).catch((err) => {
        res.status(500).send("Veritabanı hatası: " + err);
    });

});


app.get("/user/:userId/toReadList",async(req,res)=>{
    const userId = req.params.userId;
    
    await user.findOne({ id: userId }).then((foundUser) => {
        if (foundUser) {
            res.json(foundUser.toReadBooks); 
        } else {
            res.status(404).send("Kullanıcı bulunamadı");
        }
    }).catch((err) => {
        res.status(500).send("Veritabanı hatası: " + err);
    });


});


app.post("/users", async(req, res) => {
    const oneUser = req.body;
    const newUser = new user({
        age:oneUser.age,
        email:oneUser.email,
        favoriteBooks: oneUser.favoriteBooks,
        id : oneUser.id,
        imageUrl:oneUser.imageUrl,
        toReadBooks:oneUser.toReadBooks,
        username : oneUser.username
    });
    await newUser.save();

    console.log("çalışıyor merak etme",newUser);
    res.json(newUser);
});



app.post("/user/:userId/favorites", async (req, res) => {
    const userId = req.params.userId;
    const { bookId } = req.body;
    console.log("Gelen veri:", req.body.bookId);

    try {
       
        if (!bookId) {
            return res.status(400).send("Favori kitap ID'si gerekli");
        }
        // Kullanıcıyı veritabanından bul
        const currentUser = await user.findOne({ id: userId });

        if (!currentUser) {
            return res.status(404).send("Kullanıcı bulunamadı");
        }

        // Favori kitap ekle
        currentUser.favoriteBooks.push(bookId);
        await currentUser.save();
        console.log("Favori kitap başarıyla eklendi", currentUser);
        res.json(currentUser);
    } catch (err) {
        console.error("Veritabanı hatası:", err);
        res.status(500).send("Veritabanı hatası: " + err.message);
    }
});

app.post("/user/:userId/toReadList", async (req, res) => {
    const userId = req.params.userId; // URL'den gelen userId
    const { bookId } = req.body; // Gönderilen JSON'dan bookId alınır

    try {
        // Kullanıcıyı veritabanında buluyoruz
        const currentUser = await user.findOne({ id: userId });

        // Kullanıcı bulunmazsa hata mesajı döndürüyoruz
        if (!currentUser) {
            return res.status(404).send("Kullanıcı bulunamadı");
        }

        // bookId yoksa hata mesajı döndürüyoruz
        if (!bookId) {
            return res.status(400).send("Kitap ID'si gerekli");
        }

        // Kullanıcıya yeni kitabı ekliyoruz
        currentUser.toReadBooks.push(bookId);

        // Kullanıcıyı güncelliyoruz ve kaydediyoruz
        await currentUser.save();

        // Başarılı işlem sonrası yanıt
        res.json({
            message: 'Kitap başarıyla eklendi.',
            updatedUser: currentUser
        });

    } catch (err) {
        console.error("Veritabanı hatası:", err);
        res.status(500).send("Veritabanı hatası: " + err.message);
    }
});



app.patch("/users/:userId", async (req, res) => {
    console.log(req.body);  // Gelen veriyi konsola yazdırarak kontrol edin
    const userId = req.params.userId;
    const { imageUrl } = req.body;

    if (!imageUrl) {
        return res.status(400).send("ImageUrl alanı gereklidir");
    }

    try {
        const updatedUser = await user.findOneAndUpdate(
            { id: userId },
            { imageUrl: imageUrl },
            { new: true }
        );

        if (!updatedUser) {
            return res.status(404).send("Kullanıcı bulunamadı");
        }

        res.json(updatedUser);
    } catch (err) {
        res.status(500).send("Veritabanı hatası: " + err);
    }
});

// PATCH endpoint
app.patch('/user/:userId', async (req, res) => {
    console.log(req.body);  // Gelen veriyi konsola yazdırarak kontrol edin
    const userId = req.params.userId;
    const { field, value } = req.query; // field ve value query parametrelerinden alınır


    try {
        const updatedUser = await user.findOneAndUpdate(
            { id: userId },
            { [field]: value },
            { new: true }
        );

        if (!updatedUser) {
            return res.status(404).send("Kullanıcı bulunamadı");
        }

        res.json(updatedUser);
    } catch (err) {
        res.status(500).send("Veritabanı hatası: " + err);
    }
});



/////////////////////////////////////////////////////////////////////

app.get("/books", async (req, res) => {
    try {
        const books = await book.find();  // Modeli doğru şekilde kullanalım
        res.json(books);  // Kitapları döndürüyoruz
    } catch (err) {
        console.error("Veritabanı hatası:", err);
        res.status(500).send("Veritabanı hatası: " + err.message);
    }
});

app.get("/books/:bookId",async(req,res)=>{
    const bookId = req.params.bookId;
    
    await book.findOne({ id: bookId }).then((foundBook) => {
        if (foundBook) {
            res.json(foundBook); 
        } else {
            res.status(404).send("Kitap hbulunamadı");
        }
    }).catch((err) => {
        res.status(500).send("Veritabanı hatası: " + err);
    });


});

app.post("/books", async(req, res) => {
    const oneBook = req.body;
    const newBook = new book({
        id:oneBook.id,
        title:oneBook.title,
        description:oneBook.description,
        imageUrl:oneBook.imageUrl,
        date : oneBook.date,
        author:oneBook.author,
        pages:oneBook.pages,
        price : oneBook.price,
        language : oneBook.language,
        genres: oneBook.genres,
    });
    await newBook.save();

    console.log("çalışıyor merak etme",newBook);
    res.json(newBook);
});

//////////////////////////////////////////////////////////////////

app.get("/comments", async (req, res) => {
    try {
        const Comments = await comment.find();  // Modeli doğru şekilde kullanalım
        res.json(Comments);  // Kitapları döndürüyoruz
    } catch (err) {
        console.error("Veritabanı hatası:", err);
        res.status(500).send("Veritabanı hatası: " + err.message);
    }
});


app.post("/comments", async(req, res) => {
    const oneComment = req.body;
    const newComment = new comment({
        commentId:oneComment.commentId,
        bookId:oneComment.bookId,
        userId:oneComment.userId,
        imageUrl:oneComment.imageUrl,
        username:oneComment.username,
        commentText:oneComment.commentText,
        bookName:oneComment.bookName,
        bookImageUrl:oneComment.bookImageUrl,
        CommentModel:oneComment.CommentModel
    });
    await newComment.save();

    console.log("çalışıyor merak etme",newComment);
    res.json(newComment);
});
//////////////////////////////////////////////////////////////////////////

// POST route to add a comment

app.post("/user/:userId/toReadList", async (req, res) => {
    const userId = req.params.userId; // URL'den gelen userId
    const { bookId } = req.body; // Gönderilen JSON'dan bookId alınır

    try {
        // Kullanıcıyı veritabanında buluyoruz
        const currentUser = await user.findOne({ id: userId });

        // Kullanıcı bulunmazsa hata mesajı döndürüyoruz
        if (!currentUser) {
            return res.status(404).send("Kullanıcı bulunamadı");
        }

        // bookId yoksa hata mesajı döndürüyoruz
        if (!bookId) {
            return res.status(400).send("Kitap ID'si gerekli");
        }

        // Kullanıcıya yeni kitabı ekliyoruz
        currentUser.toReadBooks.push(bookId);

        // Kullanıcıyı güncelliyoruz ve kaydediyoruz
        await currentUser.save();

        // Başarılı işlem sonrası yanıt
        res.json({
            message: 'Kitap başarıyla eklendi.',
            updatedUser: currentUser
        });

    } catch (err) {
        console.error("Veritabanı hatası:", err);
        res.status(500).send("Veritabanı hatası: " + err.message);
    }
});

app.post("/comments/:commentId", async(req, res) => {
    const commentId = req.params.commentId; // Extract commentId from URL
    const commentModel = req.body; // Extract commentModel from request body

   try{
     // Kullanıcıyı veritabanında buluyoruz
     const currentComment = await comment.findOne({ commentId: commentId });

     // Kullanıcı bulunmazsa hata mesajı döndürüyoruz
     if (!currentComment) {
         return res.status(404).send("yorumu bulamadık");
     }

     // bookId yoksa hata mesajı döndürüyoruz
     if (!commentId) {
         return res.status(400).send("Kitap ID'si gerekli");
     }

     // Kullanıcıya yeni kitabı ekliyoruz
     currentComment.CommentModel.push(commentModel);

     // Kullanıcıyı güncelliyoruz ve kaydediyoruz
     await currentComment.save();

     // Başarılı işlem sonrası yanıt
     res.json({
         message: 'Kitap başarıyla eklendi.',
         updatedComment: currentComment
     });

   }catch (err) {
    console.error("Veritabanı hatası:", err);
    res.status(500).send("Veritabanı hatası: " + err.message);
}
});

app.get("/comments/:commentId", async(req, res) => {
    const commentId = req.params.commentId;
    
    await comment.findOne({ commentId: commentId }).then((foundComemnt) => {
        if (foundComemnt) {
            res.json(foundComemnt); 
        } else {
            res.status(404).send("Kitap hbulunamadı");
        }
    }).catch((err) => {
        res.status(500).send("Veritabanı hatası: " + err);
    });

});

app.post("/commentsModel", async(req, res) => {
    const oneCommentModel = req.body;
    const newCommentModel = new comment2({
        commentModelId:oneCommentModel.commentId,
        username:oneCommentModel.bookId,
        commentText:oneCommentModel.userId,
        
    });
    await newCommentModel.save();

    console.log("çalışıyor merak etme",newCommentModel);
    res.json(newCommentModel);
});
//////////////////////////////////////////////////////////////

app.delete("/user/:userId/favorite/:bookId", async (req, res) => {
    const { userId, bookId } = req.params;
 
    try {
        // Kullanıcıyı bul ve favori kitaplarından kitabı kaldır
        const updatedUser = await user.findOneAndUpdate(
            { id:userId }, // Kullanıcıyı eşleştirmek için filtre
            { $pull: { favoriteBooks: bookId } }, // Favori kitaplardan `bookId` silinir
            { new: true } // Güncellenmiş kullanıcıyı geri döndür
        );
 
        if (!updatedUser) {
            return res.status(404).json({ error: "User not found" });
        }
 
        res.status(200).json({
            message: "Book removed from favorites successfully",
            user: updatedUser,
        });
    } catch (error) {
        console.error("Error removing book from favorites:", error);
        res.status(500).json({ error: "Internal server error" });
    }
});

app.delete("/user/:userId/toReadList/:bookId", async (req, res) => {
    const { userId, bookId } = req.params;
 
    try {
        // Kullanıcıyı bul ve favori kitaplarından kitabı kaldır
        const updatedUser = await user.findOneAndUpdate(
            { id:userId }, // Kullanıcıyı eşleştirmek için filtre
            { $pull: { toReadBooks: bookId } }, // Favori kitaplardan `bookId` silinir
            { new: true } // Güncellenmiş kullanıcıyı geri döndür
        );
 
        if (!updatedUser) {
            return res.status(404).json({ error: "User not found" });
        }
 
        res.status(200).json({
            message: "Book removed from favorites successfully",
            user: updatedUser,
        });
    } catch (error) {
        console.error("Error removing book from favorites:", error);
        res.status(500).json({ error: "Internal server error" });
    }
});

app.post('/user/:userId/favorites', async (req, res) => {
    const userId = req.params.userId; // URL'den gelen userId
    const { bookId } = req.body; // Gönderilen JSON'dan bookId alınır

    try {
        // Kullanıcıyı veritabanında buluyoruz
        const currentUser = await user.findOne({ id: userId });

        // Kullanıcı bulunmazsa hata mesajı döndürüyoruz
        if (!currentUser) {
            return res.status(404).json(false); // Kullanıcı bulunamadığında false döndür
        }

        // bookId yoksa hata mesajı döndürüyoruz
        if (!bookId) {
            return res.status(400).json(false); // bookId eksikse false döndür
        }

        // Eğer kitap zaten favorilerde varsa, false döndürüyoruz
        if (currentUser.toReadBooks.includes(bookId)) {
            return res.json(true); // Kitap zaten favorilerde
        }
        else{
            res.json(false);

        }


    } catch (err) {
        console.error("Veritabanı hatası:", err);
        res.status(500).json(false); // Hata durumunda false döndürüyoruz
    }
});


// Sunucuyu başlat

app.listen(3000, "192.168.188.187",() => {
    console.log('Çalışıyor........... http://192.168.188.187:3000');
});
