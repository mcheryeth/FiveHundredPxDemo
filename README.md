# 500px demo app on Android


This is a demo app that uses the 500px REST API to display a series of beautiful high quality photos categorically. Feast for the eyes.
This README is a work-in-progress, don't be surprised if some steps are missing :).

## Setting up your dev environment

1. Install developer tools
	* Download the [Android SDK] (http://developer.android.com/sdk/index.html). This project targets Android 4.1.2 (Jellybean) SDK

2. Pull source
	* Clone the following repos
		Main project:
		- FiveHundredPxDemo : The 500px demo app (git clone git@github.com:mcheryeth/FiveHundredPxDemo)  
		
		Dependencies:
		- volley : Google's new cool android networking framework (git clone https://android.googlesource.com/platform/frameworks/volley)
		- ActionBarSherlock : ActionBar support library. (git clone https://github.com/JakeWharton/ActionBarSherlock)
		- 500px-android-sdk : 500px android sdk (git clone git://github.com/500px/500px-android-sdk)

Android Studio/Intellij is the recommended IDE!
4. Open project in Intellij
	* Download Intellij Community Edition from http://www.jetbrains.com/idea/
	* Open Intellij and click Open Project (Quick Start -> Open Project) or (File -> Open).
	 
If you didn't get Intellij like you should have, do the following using Eclipse.
5. Import projects to Eclipse
	* In Eclipse (Import -> General -> Existing Android Project into Workspace), import the following three projects:
		- FiveHundredPxDemo : The Android application
		- volley : Google's Networking framework
		- ActionBarSherlock : ActionBar support library.
		- 500px-android-sdk : 500px android sdk
	* Do a Project->Clean
		
## Troubleshooting

* Build errors in Eclipse/IntelliJ : Check the 'Problems' window first. Make sure your build paths are correct. It should all work. You have the power to fix it.
Or just contact me :).

Enjoy Playin' and droolin' :)
