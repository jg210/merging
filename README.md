[![CircleCI](https://circleci.com/gh/jg210/merging.svg?style=svg)](https://circleci.com/gh/jg210/merging)

Work in progress.

This kotlin android app will merge photos of faces. Currently, it just does face detection and sends the result to logcat.

It's using:

* [ML Kit](https://developers.google.com/ml-kit/) from the [Firebase](https://firebase.google.com/) platform for face feature detection.
* [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/) for crash reporting.
* [Firebase Analytics](https://firebase.google.com/docs/analytics).
* [Fresco](https://developers.google.com/ml-kit/) for android Bitmap management.
* [Circle CI](https://circleci.com/gh/jg210/merging) for automated build and test.
* [Material Design](https://material.io/design/).

There won't be any database versioning/migrations until after the first play store release, so it might be necessary to wipe the app's data when switching between versions.

Release apks are available as artifacts here:

https://circleci.com/gh/jg210/merging/tree/master

...although the "artifacts" tab isn't always visible in the Circle CI UI:

https://discuss.circleci.com/t/stored-artifacts-not-showing-up-on-a-build/20761/3

## Firebase Configuration

* Create a project for the app in the [Firebase console](https://console.firebase.google.com/).
* Download a copy of google-services.json from the Firebase console and put it in the app/ directory.

## Circle CI Configuration

* In CircleCI project's Environment Variables settings, add a GOOGLE_SERVICES environment variable with its value set to the output of the following command:

```
base64 --wrap=0 app/google-services.json && echo
```


* Set RELEASE_KEY_PASSWORD to android release key's password (the store and key passwords must be the same).
* Set RELEASE_KEYSTORE to output of:

```
base64 --wrap=0 release.keystore && echo
```

## Branching

* Development is done on "develop" branch.
* Releases are made from "master" branch.

