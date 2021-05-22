# Nextcloud Cookbook for Android

## This is a fork

Forked from [here](https://codeberg.org/MicMun/nextcloud-cookbook)

## About

This app is a viewer for recipes in Nextcloud App.
You need the Nextcloud Android client to sync the recipes.

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Get it on Google Play"
      height="60">](https://play.google.com/store/apps/details?id=de.micmun.android.nextcloudcookbook)
[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="60">](https://f-droid.org/en/packages/de.micmun.android.nextcloudcookbook/)

**First steps**

After installation you must go into the settings view and select the recipe directory with the recipes inside.
You find it on your storage under _Android/media/com.nextcloud.client/nextcloud/&lt;your account&gt;/&lt;folder&gt;_.

You also can choose the theme in the settings.

After that, the start view has a list of recipes and you select a recipe to view the details.

## Screenshots

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_start.png" width="270" height="540" alt="Screenshot start with list"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_detail_info.png" width="270" height="540" alt="Screenshot detail view with info tab"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_detail_ingredients.png" width="270" height="540" alt="Screenshot detail view with ingredients tab"/>

## Roadmap

* Cooking timer
* Create new recipes
* Edit recipes
* Direct server synchronisation
* ...

## Translations

The project can be translated [here](https://weblate.bubu1.eu/projects/nextcloud-cookbook-android-app/).

Thanks to translators:

* [@mondstern](https://mastodon.technology/@mondstern)

## Contributors

Here I want to thank for contributions to the app.  
Thanks to

* [mrremo](https://codeberg.org/mrremo)
* [leafar](https://codeberg.org/leafar)

## Dependencies

This app needs Android &gt;= 6.0 (API &gt;= 23) and uses the libraries (see also app/build.gradle):

* androidx dependencies
* kotlinx coroutines
* [klaxon by cbeust](https://github.com/cbeust/klaxon) (json parser)
* [Dexter by Karumi](https://github.com/Karumi/Dexter) (permission handling)
* [SimpleStorage by anggrayudi](https://github.com/anggrayudi/SimpleStorage) (storage handling and choosing a directory)

## License

**Copyright 2020-2021 by MicMun**

This program is free software: you can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see
[http://www.gnu.org/licenses/](http://www.gnu.org/licenses/).
