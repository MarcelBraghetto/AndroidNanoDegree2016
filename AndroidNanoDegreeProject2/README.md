# Marcel Braghetto - Android Nano Degree Project 2 Submission

This is my Udacity Android Developer Nano Degree Project 2 - Popular Movies Stage 2 project.

The core requirements of this project were everything from Project 1, plus some additional requirements:

- For a selected movie, load and display the list of video trailers (if any) for that movie.
- Allow the user to play a video trailer via an intent to the video url.
- Allow the user to share a video trailer via an intent with the video url and a message.
- For a selected movie, load and display the list of user reviews (if any) for that movie.
- Implementation of a **master / detail** view when the app is viewed on tablet devices, where the left hand (master) shows the collection of movies and the right hand (detail) shows the currently selected movie details.
- Allow the user to **favourite** a movie which is then persisted into the user's 
**favourites** collection.

My implementation added some additional features:

- When viewing the app on any sized screen, the recycler view collection will configure itself with an appropriate number of columns for the screen size and current device orientation.
- The toolbar and navigation tabs operate differently depending on whether the app is viewed on a large device or in landscape.
- The list of video trailers is rendered with thumbnail images sourced dynamically from YouTube.
- The first **trailer** found in the videos can be played using the play button in the movie details image header.
- Migration of networking implementation to Retrofit (with OkHttp as its client).
- Dynamic Recycler View columns based on portrait/landscape and tablet.
- Details activity opens in a custom way on tablets.
- Details activity includes a number of Android Design library components and techniques including a paralax collapsing header.
- A suite of JUnit unit tests run through Robolectric to verify the logical code in the solution.
- A suite of Espresso automated UI tests to exercise the UI.

**Sample of the app on a phone**

![Phone 1](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/phone01.jpg)

![Phone 2](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/phone02.jpg)

![Phone 3](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/phone03.jpg)

![Phone 4](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/phone04.jpg)

**Sample of the app on a tablet**

![Tablet 1](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/tablet01.jpg)

![Tablet 2](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/tablet02.jpg)

![Tablet 3](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/tablet03.jpg)

# Software pattern

This project uses a software pattern where the business logic for a given part of the app is in a 'controller' logic class, and the UI code remains in the Activity/Fragment components.

The controllers send commands to the UI via a contract interface, but have no knowledge of how the UI executes the commands. Inversely, the UI will know how to execute commands it is given, but has no knowledge of how or why the commands were given to it.

The outcome is that all the controller code can typically be unit tested in regular JUnit tests - with the help of Robolectric as the test runner.

I also make liberal use of dependency injection using Dagger 2 to achieve loosely coupled code. Among the many benefits this provides, the main ones for this project are:

- All controller logic is injected with its dependencies, so for any given unit test, mock objects are injected instead. The mock objects are controlled by Mockito and allow for the consistent simulation of different test scenarios.
- The Espresso test suite provides a different networking implementation to the regular app, which simply returns canned responses rather than doing any actual network requests.

I also use an event bus for communicating across the app via events. This again helps to decouple components and features from each other.

# Running the project

The project can be opened and run from Android Studio. One quirk with Android Studio seems to be that it will delete the **.idea** directory the first time you open a new project.

# Unit Test Setup

**Important:** There is a directory named **Support** which contains a subdirectory **runConfigurations** which should be copied into the **.idea** directory after opening the project for the first time.

![Copy runConfigurations folder](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/copy_run_configs.png)

This will provide the following Android Studio run profiles in the **run** dropdown menu:

- **PopularMovies** - run the application itself
- **Robolectric Unit Test Suite** - run the suite of unit tests (make sure the build variants is set to **Unit Tests** before running this profile)
- **Automated Espresso Test Suite** - run the suite of UI tests (make sure the build variants is set to **Android Instrumentation Tests** before running this profile)

# Unit Test Profile Configuration Setup

You also need to add one more thing to the **Robolectric Unit Test Suite** run profile in order for it to find the text resources inside the unit test class path:

- Edit the run configuration
- Set the *Use class path of module* to **PopularMovies**
- **Important step:** Add the *copyUnitTestResources* Gradle task to the **Before launch: ...** section.

Like this:

![Setup Unit Test profile](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject2/raw/master/Support/ReadMeAssets/unit_test_profile.png)

You should now be able to run the **Robolectric Unit Test Suite** run profile from Android Studio to execute all the JUnit tests.

You can also run them from the command line with:

**./gradlew clean testDebug**

The JUnit test reports will be found here:

**/PopularMovies/build/reports/tests/debug/index.html**

###Licence

Copyright 2016 Marcel Braghetto

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
