import pyrebase

config = {
    "apiKey": "AIzaSyCPnBdXhBl-h37lAfjngMpm28s0dLQ1FJs",
    "authDomain": "firstproject-b3334.firebaseapp.com",
    "databaseURL": "https://firstproject-b3334.firebaseio.com/",
    "storageBucket": "firstproject-b3334.appspot.com"
}
email = "louishenrifranc@gmail.com"
password = "groscaca"

firebase = pyrebase.initialize_app(config)

auth = firebase.auth()
user = auth.sign_in_with_email_and_password(email, password)

database = firebase.database()
storage = firebase.storage()


def stream_handler(message):
    print(message["event"])  # put
    print(message["path"])  # /-K7yGTTEp7O549EzTYtI
    print(message["data"])  # {'title': 'Pyrebase', "body": "etc..."}
    print(message["data"])
    if message["data"] is not None:
        filename = message["data"]["filename"]
        storage.child("images/" + filename).download(filename)
        print("Image saved")


database.child("messages").stream(stream_handler)
