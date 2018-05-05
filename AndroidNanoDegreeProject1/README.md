# Marcel Braghetto - Android Nano Degree Project 1 Submission

This is my Udacity Android Developer Nano Degree Project 1 - Popular Movies Stage 1 project.

The core requirements of this project were:

- Load popular and top rated movies from the Movies DB API.
- Allow the user to select between the popular and top rated movies.
- Allow the user to select a movie and view its details.

My implementation went a bit further and the project app includes:

- Continuous fetching of movies as the user scrolls.
- Collapsing tab bar layout with quick return interaction.
- View pager for selecting between popular, top rated and user favourites.
- Swipe to refresh to force a reload of data.
- Use of OkHttp response caching to only attempt to connect to the server if the user has performed a swipe to refresh, or if the locally cached response data is stale. This is to help with battery life and network data usage for the app.
- Dynamic Recycler View columns based on portrait/landscape and tablet.
- Details activity opens in a custom way on tablets.
- Details activity includes a number of Android Design library components and techniques including a paralax collapsing header.
- A suite of JUnit unit tests run through Robolectric to verify the logical code in the solution.
- A suite of Espresso automated UI tests to exercise the UI.

**Sample of the app on a phone**

![Phone](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject1/raw/master/Support/ReadMeAssets/phone.gif)

**Sample of the app on a tablet**

![Tablet](https://github.com/MarcelBraghetto/AndroidNanoDegreeProject1/raw/master/Support/ReadMeAssets/tablet.gif)

# Software pattern

This project uses a software pattern where the business logic for a given part of the app is in a 'controller' logic class, and the UI code remains in the Activity/Fragment components.

The controllers send commands to the UI via a contract interface, but have no knowledge of how the UI executes the commands. Inversely, the UI will know how to execute commands it is given, but has no knowledge of how or why the commands were given to it.

The outcome is that all the controller code can typically be unit tested in regular JUnit tests - with the help of Robolectric as the test runner.

I also make liberal use of dependency injection using Dagger 2 to achieve loosely coupled code. Among the many benefits this provides, the main ones for this project are:

- All controller logic is injected with its dependencies, so for any given unit test, mock objects are injected instead. The mock objects are controlled by Mockito and allow for the consistent simulation of different test scenarios.
- The Espresso test suite provides a different networking implementation to the regular app, which simply returns canned responses rather than doing any actual network requests.

I also use an event bus for communicating across the app via events. This again helps to decouple components and features from each other.

# Running the project

**IMPORTANT** You must get your own Movies DB API key to run this app:

https://www.themoviedb.org/

Put your API key into the string resource file **movies_db_api.xml**.

**Other information**

The project can be opened and run from Android Studio. One quirk with Android Studio seems to be that it will delete the **.idea** directory the first time you open a new project.

There is a directory named **Support** which contains a subdirectory **runConfigurations** which should be copied into the **.idea** directory after opening the project for the first time. This will provide the following Android Studio run profiles:

- **PopularMovies** - run the application itself
- **Robolectric Unit Test Suite** - run the suite of unit tests (make sure the build variants is set to **Unit Tests** before running this profile)
- **Automated Espresso Test Suite** - run the suite of UI tests (make sure the build variants is set to **Android Instrumentation Tests** before running this profile)

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
