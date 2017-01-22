from keras.models import Sequential, Model
from keras.layers.core import Flatten, Dense, Dropout
from keras.layers.convolutional import Convolution2D, MaxPooling2D, ZeroPadding2D
import cv2, numpy as np
from PIL import Image
import numpy as np
import os
import cPickle
import pyrebase
from sklearn.metrics.pairwise import cosine_similarity

config = {
    "apiKey": "",
    "authDomain": "",
    "databaseURL": "",
    "storageBucket": ""
}
email = raw_input("Enter Firebase email:\n")
password = raw_input("Enter Firebase password:\n")



def VGG_19(weights_path=None):
    model = Sequential()
    model.add(ZeroPadding2D((1, 1), input_shape=(3, 224, 224)))
    model.add(Convolution2D(64, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(64, 3, 3, activation='relu'))
    model.add(MaxPooling2D((2, 2), strides=(2, 2)))

    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(128, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(128, 3, 3, activation='relu'))
    model.add(MaxPooling2D((2, 2), strides=(2, 2)))

    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(256, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(256, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(256, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(256, 3, 3, activation='relu'))
    model.add(MaxPooling2D((2, 2), strides=(2, 2)))

    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(MaxPooling2D((2, 2), strides=(2, 2)))

    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(ZeroPadding2D((1, 1)))
    model.add(Convolution2D(512, 3, 3, activation='relu'))
    model.add(MaxPooling2D((2, 2), strides=(2, 2)))

    model.add(Flatten())
    model.add(Dense(4096, activation='relu', name='output_layer'))
    model.add(Dropout(0.5))
    model.add(Dense(4096, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(1000, activation='softmax'))

    model2 = Model(input=model.input,
                   output=model.get_layer("output_layer").output)
    if weights_path:
        model.load_weights(weights_path)

    return model, model2


firebase = pyrebase.initialize_app(config)
auth = firebase.auth()
user = auth.sign_in_with_email_and_password(email, password)
database = firebase.database()
storage = firebase.storage()


def get_matched_image(hash_map, filename, nb_matches=6):
    img = Image.open(filename).convert("RGB")
    img = np.array(img)
    img = cv2.resize(img, (224, 224)).astype(np.float32)
    img[:, :, 0] -= 103.939
    img[:, :, 1] -= 116.779
    img[:, :, 2] -= 123.68
    img = img.transpose((2, 0, 1))
    img = np.expand_dims(img, axis=0)

    output = model2.predict(img)
    cosine_similarities = {file_name: cosine_similarity(output, vectors) for file_name, vectors in hash_map.iteritems()}
    matched_file_name = sorted(cosine_similarities, key=cosine_similarities.get)[:nb_matches]
    return matched_file_name


def stream_handler(message):
    print(message["event"])  # put
    print(message["path"])  # /-K7yGTTEp7O549EzTYtI
    print(message["data"])  # {'title': 'Pyrebase', "body": "etc..."}
    print(message["data"])
    if message["data"] is not None:
        filename = message["data"]["filename"]
        storage.child("images/" + filename).download(filename)
        print("Image saved")
        matched_file_name = get_matched_image(hash_map, filename)
        for index, filename in enumerate(matched_file_name):
            print(filename + str(index) + ".jpg")
            storage.child("similar_images/" + filename + str(index) + ".jpg").put("wikiart/train/" + filename)

        database.child("similar_images/").push({"file": filename})


database.child("messages").stream(stream_handler)

if __name__ == "__main__":
    hash_map = {}
    _, model2 = VGG_19('vgg19_weights.h5')
    Image.MAX_IMAGE_PIXELS = None
    # sgd = SGD(lr=0.1, decay=1e-6, momentum=0.9, nesterov=True)
    # model.compile(optimizer=sgd, loss='categorical_crossentropy')
    for iteration, filename in enumerate(os.listdir("wikiart/train")):
        img = Image.open("wikiart/train/" + filename).convert("RGB")
        img = np.array(img)
        img = cv2.resize(img, (224, 224)).astype(np.float32)
        img[:, :, 0] -= 103.939
        img[:, :, 1] -= 116.779
        img[:, :, 2] -= 123.68
        img = img.transpose((2, 0, 1))
        img = np.expand_dims(img, axis=0)
        if (iteration % 50 == 0):
            print("Iteration %d" % iteration)
            cPickle.dump(hash_map, open("vectors.p", "w"))
        # Test pretrained model

        out = model2.predict(img)
        hash_map[filename] = out
