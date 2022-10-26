# APPointment Admin
The administration app for APPointment

### Screenshots
https://imgur.com/a/wlWuUvk

## Features

### Two types of customers
The app supports both registered and unregistered customer profiles, thus you can use it also as a customer base.

### Communication
You can reach your customers using any of the communication methods they have provivided.
#### Supported methods
- Calling
- SMS message
- Email
- Notifications*

\* *Only for registered customers*

### Create customer profile
You can create your customer base by adding the individual customer information on your own. You can also edit these information later.

### Notes
Keep notes for each customer, such as the last product or service he purchased. You can also use images to accompany your note!

#### Google Drive support
The images for each note are uploaded to your personal Google Drive account so you can keep the photos individually from Firebase and also avoid the 1 Gb limit of Firebase storage.

### Merging customer profiles
In the case that you already had created a customer profile and that customer downloads the APPointment app and create a new profile on each own, you have the option to merge the two profiles without loosing anything!

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

\* *Be sure to check the official Firebase documentation*
\*\**keystore.properties file's path should also be changed inside build.gradle if the file is not located at project's root*

- a "FCM_KEY" promerty in the local.properties file:
```
FCM_KEY="<your_fcm_key>"
```
The FCM key is needed in order to send FCM notifications throu FCM Rest API (legacy). You can retrieve the key from project's settings on the Firebase console*. Check the official Firebase documentation for more info.

\* *Be sure to enable the FCM API from your Google account console*

### Firebase
#### Authentication
In order for the app to work without any modifications you must enable the following sign-in methods from Firebase console:
- Email/Password
- Google

## Companion app
The customer app is available on the APPointment repo