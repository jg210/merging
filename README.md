[![CircleCI](https://circleci.com/gh/jg210/merging.svg?style=svg)](https://circleci.com/gh/jg210/merging)

Work in progress.

This kotlin android app will merge photos of faces. Currently, it:

* Allows photos to be taken.
* Does face detection.
* Displays the number of detected faces.
* Shows onboarding and licence information.

It's using:

* [ML Kit](https://developers.google.com/ml-kit/) from the [Firebase](https://firebase.google.com/) platform for face feature detection.
* [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/) for crash reporting.
* [Firebase Analytics](https://firebase.google.com/docs/analytics).
* [Fresco](https://developers.google.com/ml-kit/) for android Bitmap management.
* [Circle CI](https://circleci.com/gh/jg210/merging) for automated build and test.
* [Material Design](https://material.io/design/).

Release apks are built by Circle CI and published as [github releases](https://github.com/jg210/merging/releases).

There won't be any database versioning/migrations until after the first play store release, so it might be necessary to wipe the app's data when switching between versions.

## Development Environment

Can just open the project in Android Studio, or use `./gradlew`.

To use [Fastlane](https://fastlane.tools/):

* Install [rbenv](https://github.com/rbenv/rbenv#installation).
* Install [ruby-build](https://github.com/rbenv/ruby-build#installation) as an rbenv plugin.
* Run:

```
bin/setup
. ./environment
fastlane --help
```

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

* Set GITHUB_TOKEN as explained by https://github.com/tcnksm/ghr#github-api-token. 

## Branching

* Development is done on "develop" branch.
* Releases are made from "master" branch. 

## Creating release

* The version numbers are generated from the first-parent depth of the git commit graph, so all commits to the release branch should be merge commits. Otherwise, the version number will generally increment by more than one.
* Wait for develop branch to be tested, otherwise github blocks pushes to the master branch.

```
git checkout master
git pull
git merge --no-ff origin/develop      # The commit message appears as release notes.
git push
git checkout develop
```

