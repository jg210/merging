[![CircleCI](https://circleci.com/gh/jg210/merging.svg?style=svg)](https://circleci.com/gh/jg210/merging)

Work in progress.

This kotlin android app will merge photos of faces.

It's already using:

* [ML Kit](https://developers.google.com/ml-kit/) for face feature detection.
* [Firebase](https://firebase.google.com/) since ML Kit requires this, and since [Crashlytics]()https://firebase.google.com/docs/crashlytics) will be useful later on.
* [Fresco](https://developers.google.com/ml-kit/) for android Bitmap management.
* [Circle CI](https://circleci.com/gh/jg210/merging) for automated testing.

## Firebase Configuration

* Create project for the app on https://firebase.google.com/
* Download a copy of google-services.json into the app/ directory.
* In CircleCI project's Environment Variables settings, add a GOOGLE_SERVICES environment variable with its value set to the output of the following command:

```
base64 --wrap=0 app/google-services.json && echo
```
