[![CircleCI](https://circleci.com/gh/jg210/merging.svg?style=svg)](https://circleci.com/gh/jg210/merging)

Work in progress.

This kotlin android app will merge photos of faces.

It's already using:

* [ML Kit](https://developers.google.com/ml-kit/) for face feature detection.
* [Crashlytics](https://firebase.google.com/docs/crashlytics/) for crash reporting.
* [Firebase](https://firebase.google.com/) since ML Kit and Crashltics require this.
* [Fresco](https://developers.google.com/ml-kit/) for android Bitmap management.
* [Circle CI](https://circleci.com/gh/jg210/merging) for automated build and test.
* [Material Design](https://material.io/design/).

There won't be any database versioning/migrations until after the first release, so it might be necessary to wipe the
app's data when switching between versions.

## Firebase Configuration

* Create a project for the app in the [Firebase console](https://console.firebase.google.com/).
* Download a copy of google-services.json from Firebase and put it in the app/ directory.
* In CircleCI project's Environment Variables settings, add a GOOGLE_SERVICES environment variable with its value set to the output of the following command:

```
base64 --wrap=0 app/google-services.json && echo
```
