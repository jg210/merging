https://play.google.com/store/apps/details?id=uk.me.jeremygreen.merging

[![CircleCI](https://circleci.com/gh/jg210/merging/tree/develop.svg?style=svg)](https://circleci.com/gh/jg210/merging)

Work in progress.

This kotlin android app will merge photos of faces. Currently, it:

* Takes photos.
* Detects and displays face features.
* Shows onboarding and licence information.
* Does not do database migrations, so data may be lost when upgrading.

It's using:

* [ML Kit](https://developers.google.com/ml-kit/vision/face-detection) for face feature detection.
* [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/) for crash reporting.
* [Firebase Analytics](https://firebase.google.com/docs/analytics).
* [Fresco](https://developers.google.com/ml-kit/) for android Bitmap management.
* [Circle CI](https://circleci.com/gh/jg210/merging) for automated build, test and continuous delivery.
* [Fastlane](https://fastlane.tools/) to publish the app as a beta (early access) release on the [Google Play store](https://play.google.com/store/apps/details?id=uk.me.jeremygreen.merging) for every commit on the release branch.
* [Material Design](https://material.io/design/).

## Development Environment

Can just open the project in Android Studio, or use `./gradlew`.

To use [Fastlane](https://fastlane.tools/) (for publishing the app):

* Install [rbenv](https://github.com/rbenv/rbenv#installation).
* Install [ruby-build](https://github.com/rbenv/ruby-build#installation) as an rbenv plugin.
* Create a Google Play store API key: https://docs.fastlane.tools/actions/supply/
* Rename the Google Play store API key to google-play-service.json and put it in the top-level source-code directory.
* Run:

```
bin/setup
. ./environment
fastlane --help
```

## Firebase Configuration

* Create projects for the app in the [Firebase console](https://console.firebase.google.com/) - one for each flavour.
* Download the google-services.json files from the Firebase console and put them in the location indicated by the symlinks in the app/src/product/ and app/src/develop/ directories.

## Circle CI Configuration

* In CircleCI project's Environment Variables settings, add a GOOGLE_SERVICES environment variable with its value set to the output of:

```
base64 --wrap=0 ../merging-secrets/firebase/product/google-services.json && echo
```

* Add GOOGLE_SERVICES_DEVELOP environment variable with its value set to the output of:

```
base64 --wrap=0 ../merging-secrets/firebase/develop/google-services.json && echo
```

* For play store API access key, add GOOGLE_PLAY_SERVICE_JSON environment variable with its value set to the output of:

```
base64 --wrap=0 google-play-service.json && echo
```

* Set RELEASE_KEY_PASSWORD to android release key's password (the store and key passwords must be the same).
* Set RELEASE_KEYSTORE to output of:

```
base64 --wrap=0 release.keystore && echo
```

* Set GITHUB_TOKEN as explained by https://github.com/tcnksm/ghr#github-api-token. 

## Branching

* Development is done on "develop" branch.
* Releases are made from "release" branch (automatically, see below).

## Creating Release

Releases to the play store are done automatically from the "release" branch using a Circle CI job and fastlane.

* The version numbers are generated from the first-parent depth of the git commit graph, so all commits to the release branch should be merge commits. Otherwise, the version number will generally increment by more than one.
* Commit changelog to a new file in [fastlane/metadata/android/en-GB/changelogs](fastlane/metadata/android/en-GB/changelogs). Do this from the develop branch, prior to merging to release.
* Wait for develop branch to be tested, otherwise github blocks pushes to the release branch.

```
git checkout release
git pull
git merge --no-ff origin/develop      # The commit message appears as release notes for github release, so copy from fastlane metadata file.
git push
git checkout develop
```

