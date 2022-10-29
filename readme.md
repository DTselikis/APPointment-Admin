# <div align="center">APPointment Admin</div>
<div align="center"><img src="https://i.imgur.com/9CmS8k2.png" height="480"></div>
<div align="center"><p>The administration app for APPointment</p></div>

<a name="readme-top"></a>
## Table of contents
- [Features](#features)
    - [Contact](#contact)
    - [Notifications](Notifications)
    - [Notes](#notes)
        - [Google Drive support](#google-drive-support)
    - [Merging](#merging)
- [Requirements](#requirements)
    - [Project](#project)
    - [Firebase](#firebase)
- [Built with](#built-with)
- [Credits](#credits)
- [Companion app](#companion-app)

## Features

### Customer catalog
With the power of Firebase you can have your customer catalog wherever you go. You can choose between registered and unregistered customers (or both) and even **create** the profile of a customer who doesn’t have the app.

### Contact
You can contact your customer choosing one of the available methods:
-	Calling
-	Sms
-	Email
-	Push notification

<sup>Calling and Sms will be handled by external apps through Intents</sup>

##### Notifications
You can choose from a precomposed notifications or make your own and sent it to your customer via FCM.

### Notes
Keep important information about each of your customers with notes. Each note support also images so you can keep track of your customer progress!

#### Google Drive support
All notes images stored at your **Google Drive** to avoid the 1 GB limitation of Firebase storage and also keep the images even if you uninstall the app.

### Merging
In the case that you have created an unregistered account for a customer and this customer eventually created an account on their own, you can merge the two accounts by preserving the registered own and choose what information to keep in case of conflicts.
<br>
<div align="center"><img src="https://i.imgur.com/QnnpLwX.png" height="580"/></div>
<sup><p align="right">(<a href="#readme-top">back to top</a>)</p></sup>

# Requirements
### Project
To build the project you must provide:
- a google-services.json file in the root project* folder. You can retrieve it from "Project Settings" at Firbase console
- a .jks file in order to sign the release build
- a keystore.properties file with the following structure:
```
storePassword = <Keystore password>  
keyPassword = <Signing key password>  
keyAlias = <Key alias>  
storeFile = <Path to the .jks file>
```
Those fields corresponds to properties at module level **build.gradle**\*\* file.
You need both .jks and keystore.properties files in order to sign the release build.

<sup>\* *Be sure to check the official Firebase documentation*</sup>
<sup>\** *keystore.properties file's path should also be changed inside build.gradle if the file is not located at project's root*</sup>

- a "FCM_KEY" promerty in the local.properties file:
```
FCM_KEY="<your_fcm_key>"
```
The FCM key is needed in order to send FCM notifications throu FCM Rest API (legacy). You can retrieve the key from project's settings on the Firebase console***. Check the official Firebase documentation for more info.

<sup>\*** *Be sure to enable the FCM API from your Google account console*</sup>

### Firebase
#### Authentication
In order for the app to work without any modifications you must enable the following sign-in methods from Firebase console:
- Email/Password
- Google

<sup><p align="right">(<a href="#readme-top">back to top</a>)</p></sup>

## Built with
Here is a brief summary of what technologies have been used for the app:
-	[Firebase](https://github.com/firebase/FirebaseUI-Android) for authentication and storage
-	[Coroutines](https://developer.android.com/kotlin/coroutines)
-	[Drive API](https://developers.google.com/drive/api) for storing the images from notes
-	[FCM](https://firebase.google.com/docs/cloud-messaging) for push notifications
-	[Retrofit](https://github.com/square/retrofit) and [Moshi](https://github.com/square/moshi) to communicate wirh the FCM API
-	[Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started)
-	[Coil](https://github.com/coil-kt/coil) to load images
-	[Google Sign-In for Android](https://developers.google.com/identity/sign-in/android) to authenticate for Drive API

## Credits
This software uses the following open source packages:
- [Retrofit](https://github.com/square/retrofit)
- [Moshi](https://github.com/square/moshi)
- [Coil](https://github.com/coil-kt/coil)
- [FirebaseUI for Android](https://github.com/firebase/FirebaseUI-Android)

## Companion app
The customer app is available on the APPointment repo.

<sup><p align="right">(<a href="#readme-top">back to top</a>)</p></sup>