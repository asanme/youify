# Youify
---
# Information
Youify is an Android app for converting your YouTube playlists to an Spotify playlist.

**The app is currently only available for Android.**

## Features work in progress
- [x] Log in using Google.
- [x] Fetch videos from the provided YouTube playlist.
- [ ] Ability to remove videos before converting.
- [ ] Connect to Spotify account.
- [ ] Convert YouTube playlist to Spotify.
- [ ] Add converted playlist to new Spotify folder / playlist.


## Future Updates
* Port to Kotlin Multiplatform.
* Add two way support for playlist conversion.

## Regarding safety
In the current version, the tokens are stored in EncryptedSharedPreferences so that they can't be read from outside the app. The access token is automatically refreshed upon receiving an HTTP 401 (UNAUTHORIZED) error.

I'm still new to Android, so any suggestions regarding safety for logins, tokens, and other matters will be greatly appreciated.

## NOTE:
The current version of the app has not been thoroughly tested and is still work in progress, so please expect some bugs.