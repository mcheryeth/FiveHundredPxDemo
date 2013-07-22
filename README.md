# 500px demo photo app on Android


This is a demo app that uses the 500px REST API to display a series of beautiful high quality photos. Feast for the eyes.

## Design and Architecture

The app fetches and displays high res photos from 500px on a grid. You can use the side menu to browse by feature and 
the drop down to change the category. Tap a photo to view it in fullscreen.
The project is organized into the following structure:

* Models - These are your POJOs (Photo, User, etc) and data storage classes for caching
* Views - This is your UI. Activities, views, adapters, etc
* Controllers - This handles your networking. API requests to 500px, JSON parsing, callbacks to UI, etc. Currently only supports fetching photos (PhotoServiceApi)

## Limitations & improvements

* Always loads from network. Need a better persistence mechanism besides the disk cache. Use an indexed SQL database with cursors (fastest, more work)
  or cache JSON in file (slower, less work) or maybe even serialize all the data (slowest, easiest).
* Add other stuff: User photos, Photo uploads, Photo share, horizontally scrolling images (view pager), etc
* And one more thing...bugs

## Challenges

* The 500px REST API was straightforward to learn and play. Nicely documented. 
  The provided SDK, however, was somewhat limiting for me. So I ended up writing my own simple API to fetch the photos by using Google's new Volley networking framework, which is great
  because it reduces a lot of boiler plate code and handles JSON parsing and image loading/caching among other things.
* Infinite scrolling was tricky.
* Getting the user experience and design right. Making the UI easy and intuitive to use.


## Setting up your environment

1. Install developer tools
	* Download the [Android SDK] (http://developer.android.com/sdk/index.html). This project targets Android 4.2.2 (Jellybean) SDK

2. Pull source
	* Clone the following repos
	
		Main Project:
		- FiveHundredPxDemo : The 500px demo app ```$ git clone git@github.com:mcheryeth/FiveHundredPxDemo.git```

		Dependencies:
		- volley : Google's new cool android networking framework ```git clone https://android.googlesource.com/platform/frameworks/volley```
		- ActionBarSherlock : ActionBar support library ```git clone https://github.com/JakeWharton/ActionBarSherlock```
		- 500px-android-sdk : 500px android sdk ```git clone git://github.com/500px/500px-android-sdk```

		Android Studio/Intellij is the recommended IDE!
4. Open FiveHundredPxDemo project in Android Studio
	* Download Android Studio from http://developer.android.com/sdk/installing/studio.html
	* Open Android Studio and click Open Project (Quick Start -> Open Project) or (File -> Open)
		
5. If you didn't get Android Studio like you should have, do the following using Eclipse. Import projects to Eclipse
	* In Eclipse (Import -> General -> Existing Android Project into Workspace), import the following four projects:
		- FiveHundredPxDemo : The 500px demo application
		- volley : Google's Networking framework
		- ActionBarSherlock : ActionBar support library.
		- 500px-android-sdk : 500px android sdk
	* Do a Project->Clean
		
## Troubleshooting

* Check your project modules! Check your dependencies!
* Build errors in Eclipse: Check the 'Problems' window first. Make sure your build paths are correct.
* It should all work. You have the power to fix it
* Or just contact me :)

## Screenshots
|![alt tag](https://raw.github.com/mcheryeth/FiveHundredPxDemo/master/misc/500pxdemo_pic1.png)|
![alt tag](https://raw.github.com/mcheryeth/FiveHundredPxDemo/master/misc/500pxdemo_pic2.png)|
![alt tag](https://raw.github.com/mcheryeth/FiveHundredPxDemo/master/misc/500pxdemo_pic3.png)|

*Enjoy playin' and droolin'  :)*

**- Melville Cheryeth**
