# Where to Next?

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
A travel planning / language learning app. Based on where you want to go, the app will provide you with phrases that you should know so you can be ready for your trip!

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Travel, Education
- **Mobile:** This app is specialized for mobile because it will use location, maps, and audio.
- **Story:** As the world becomes more globalized, more and more people will be out and about, talking with other people and travelling the world. This app helps to facilitate that connection. It creates a one-stop-shop for your communication needs while travelling. I think my friends would respond very well to this app idea.
- **Market:** The size and scale of the app could be quite large, as many people enjoy travelling and language learning. I believe the app provides a lot of value to the targeted audience.
- **Habit:** The user would open the app as frequently as they make travel plans or want to explore different languages and places. The user only consumes and does not create (at the moment).
- **Scope:** This seems pretty challenging to complete by the end of the program but it different travel and translating API's may expedite the process. A stripped-down version of this app would still be interesting to build. I think the project I want to build is quite defined.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create a new account
* User can log in
* User can log out
* Profile page
* Users can click on / search different countries 
* A page for a country to learn the essential phrases for travel
* A map of the world where a user can click on different countries to see different phrases in their main language
    * Integration with Google SDK/API
* A map tab so people can see where to ask for directions
* Favorite phrases screen
* Slide elements (phrases) to favorite them (using a gesture is a required story)
* An animation / external library for visual polish

**Optional Nice-to-have Stories**

* On the phrases page, you can listen to the phrases when you click on an audio button; pronunciation
* A translation activity (like google translate)
* Photos of different countries
* It could be made a social app where people say what places are on their bucket list, where they've been, what phrases worked well for them or recommendations of where to go (I think it's maybe out of the scope of this project for now, though.) -> Post comments and check out other people's profiles
* A way to save the countries on their profile bucket list, where they've been, or favorite phrases
* User ability to add photos from their travels
* User ability to add written memories / journal

### 2. Screen Archetypes

* Log In Screen
   * User can log in
* Create account screen
   * User can create a new account
* Search / Map of all the countries
   * Users can click on / search different countries
* Phrases page
   * A page for a country to learn the essential phrases for travel
* Log Out screen
   * User can log out


### 3. Navigation

**Tab Navigation** (Tab to Screen)

I don't plan on having tab navigation.

**Flow Navigation** (Screen to Screen)

* User -> Country -> Language
* Log In Screen
   * If they do not have an account: Log In > Create Account
   * Else: Log In > Search / Map of all the countries
* Create account screen
   * Create account > Log In
* Search / Map of all the countries
    * Search a country / click on a country > Phrases Page
* Phrases page
    * Click on button > Log Out
    * (Optional) Click on button > Translation Page
    * (Optional) Click on button > Map Page

**Technical Complexity**

* Log In Screen
    * Use Parse database to check whether a user exists and if the correct username and password are used
    * Buttons and Intents can be used to navigate to either "create account" screen or to the main activity
* Create account screen
    * Interact with Parse database to create an account
* Search / Map of all the countries
    * Search feature
    * Connect to Google SDK/API
    * https://developers.google.com/maps/documentation/android-sdk/start
    * Button and Intent can be used to navigate to the Phrases Page
* Phrases Page
    * Connect to translation API to translate different phrases to the proper language of that country
    * https://medium.com/swlh/free-use-google-translate-api-in-android-with-no-limit-70977726d7cf
    * It seems you have to pay but: https://cloud.google.com/translate
    * There is also an ML kit for translation: https://developers.google.com/ml-kit/language/translation/android#java
    * Internationalization feature would also be useful because then user would not be dependent on network connection
    * Back button should bring you back to the Search/Map page
    * Implementing Pronunciation (stretch): https://cloud.google.com/text-to-speech/

* In between screens or on screens, use an animation / use external library. Some ideas: 
    * https://www.freecodecamp.org/news/25-new-android-libraries-which-you-definitely-want-to-try-at-the-beginning-of-2017-45878d5408c0/
        * https://github.com/airbnb/lottie-android

    * https://github.com/wasabeef/awesome-android-ui
        * https://github.com/roynx98/transition-button-android
        * https://github.com/Yalantis/Side-Menu.Android
        * https://github.com/mdgspace/RotatingText
        * https://github.com/KeepSafe/TapTargetView

    * LottieFiles

## Wireframes
![](https://i.imgur.com/aH37qRh.jpg)


### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
* User
* Country


### Models
* User

| Property      | Type | Description     |
| :---        |    :---   |          :--- |
| firstName      | String       | first name of the user   |
| lastName   | String        | last name of the user      |
| username   | String        | username of user for logging in      |
| password   | String        | password of user for logging in      |
| email      | String       | email of the user   |
| favoriteCountries      | Array of Pointers to a Country       | all the countries in which the user has favorited a phrase   |



* Country

| Property      | Type | Description     |
| :---        |    :---   |          :--- |
| language      | String       | main language of that country   |
| phrases   | Array of Strings        | helpful phrases in the country's main language      |
| favoritePhrases   | Array of Strings        | the user's favorited phrases      |


### Networking
List of network requests by screen:
* Log In Screen
    * (Parse: Read/GET) Query username to check if login matches
    * (Parse: Read/GET) Query password to check if login matches
* Create account screen
    * (Parse: Update/PUT) Create a new user on the Parse database
* Search / Map of all the countries
    * (Read/GET) Map from Google SDK
    * (Read/GET) Clickable functionality
* Phrases page
    * (Read/GET) Translations from Google API
    * (Parse: Update/PUT) Favorite phrases
    * (Parse: Update/PUT) Favorite countries


Example Parse network request code:
```
private void queryPosts() {
        // specify what type of data we want to query - Country.class
        ParseQuery<Post> query = ParseQuery.getQuery(Country.class);
        // include data referred by user key
        query.include(Country.KEY_USER);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting countries.", e);
                    return;
                }

                // save received favorite countries to list and notify adapter of new data
                allCountries.addAll(favoriteCountries);
                adapter.notifyDataSetChanged();
            }
        });
    }
```

OPTIONAL: List endpoints if using existing API:
