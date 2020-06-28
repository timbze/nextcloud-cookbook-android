# Nextcloud-Cookbook

## About

This app is a viewer for recipes in Nextcloud App.
You need the Nextcloud Android client to sync the recipes.

**First steps**

After installation you must go into the settings view and select the recipe directory with the recipes inside.
You find it on your storage under _Android/media/com.nextcloud.client/nextcloud/&lt;your account&gt;/&lt;folder&gt;_.

You also can choose the theme in the settings.

After that, the start view has a list of recipes and you select a recipe to view the details.

## Screenshots

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_start.png" width="270" height="540" alt="Screenshot start with list"/> <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_detail_info.png" width="270" height="540" alt="Screenshot detail view with info tab"/> <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/screenshot_detail_ingredients.png" width="270" height="540" alt="Screenshot detail view with ingredients tab"/>

## Compatibility

This app needs Android &gt;= 6.0 (API &gt;= 23) and uses the libraries:

* androidx dependencies
* kotlinx coroutines
* [Dexter by Karumi](https://github.com/Karumi/Dexter) (permission handling)
* [android-file-chooser by Hedzr](https://github.com/hedzr/android-file-chooser) (for choosing a directory)

## License

**Copyright 2020 by MicMun**

This program is free software: you can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see
[http://www.gnu.org/licenses/](http://www.gnu.org/licenses/).
