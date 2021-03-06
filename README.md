> Do you feel a lack of creativity while drawing sometimes? 
> Stuck at a museum in front of La Joconde, ever wanted to find closely looking alike painting?  
> Just bored about life, make your routine panorama looking alike a painting?  

__Download the PaintForMe app__

# What for
* Take a picture with your phone, or search on on your local drive. 
* Send the picture to the server, and wait for paintings which are similar in the style of the initial sended picture.
http://www.inference.vc/content/images/2016/01/9k-.jpg

# Examples
* Example with a picture of building:  
![](https://github.com/ConUHacks/image_to_painting/blob/master/demos/demo1.gif)
* Example with a picture of Venise:  
![](https://github.com/ConUHacks/image_to_painting/blob/master/demos/demo2.gif)
* Example with a garden picture:  
![](https://github.com/ConUHacks/image_to_painting/blob/master/demos/demo3.gif)
* Example with La Joconde:  
![](https://github.com/ConUHacks/image_to_painting/blob/master/demos/demo4.gif)  
_(still training the accuracy)_

# Usage
## Play with the app
* Get a Firebase Account, setup your 'google-services.json' and build a new project
* or, use the apk provided

## Play with the server
To reproduce the results:  
1. Download the dataset at ![kaggle.com](https://www.kaggle.com/c/painter-by-numbers/data)  
2. Download the neural networks weights at ![google drive](https://drive.google.com/file/d/0Bz7KyqmuGsilZ2RVeVhKY0FyRmc/view). I used this deep neural network architecture [vgg-19](https://arxiv.org/abs/1409.1556)
3. Compute the vector representation for painting using ```script_python/model.py```  
4. Set Firebase config credentials in the ```backend.py```. For more information about the api, I used ![pyrebase](https://github.com/thisbejim/Pyrebase#database)  
5. Launch the server with ```python backend.py```  

# Visualisation of the latent space
Visualisation of a set of pictures using PCA algorithm and tensorboard Embedding
![](https://github.com/ConUHacks/image_to_painting/blob/master/demos/visualisation.gif)


