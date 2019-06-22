[![CircleCI](https://circleci.com/gh/jg210/merging.svg?style=svg)](https://circleci.com/gh/jg210/merging)

h1. Firebase Configuration

* Create project for the app on https://firebase.google.com/
* Download a copy of google-services.json into the app/ directory.
* In CircleCI project's Environment Variables settings, add a GOOGLE_SERVICES environment variable with its value set to the output of the following command:

```
base64 --wrap=0 app/google-services.json && echo
```
