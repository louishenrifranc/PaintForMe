# Painter
* Take a picture with your phone, or search on on your local drive. 
* Send the picture to the server, and wait for paintings which are similar in the style of the initial sended picture.

# Play with the app
* Get a Firebase Account
* Place your 'google-services.json' in the app folder
* Launch the app

# Play with the server
To reproduce the results:
1. Download the dataset at ![kaggle.com](https://www.kaggle.com/c/painter-by-numbers/data)
2. Download the neural networks weights at ![google drive](https://drive.google.com/file/d/0Bz7KyqmuGsilZ2RVeVhKY0FyRmc/view)
3. Compute the vector representation for painting using ```script_python/model.py```
4. Set Firebase config credentials in the ```backend.py```. For more information about the api, I used ![pyrebase](https://github.com/thisbejim/Pyrebase#database)
5. Launch the server with ```python backend.py```

# Examples
_(still training the accuracy of the backend)_
