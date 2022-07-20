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
* Favorite phrases screen
* A gesture such as a slide up panel to go the the Favorite Phrases
* An animation / external library for visual polish

**Optional Nice-to-have Stories**

* On the phrases page, you can listen to the phrases when you click on an audio button; pronunciation
* A map tab so people can see where to ask for directions
* A translation activity (like google translate)
* Photos of different countries
* It could be made a social app where people say what places are on their bucket list, where they've been, what phrases worked well for them or recommendations of where to go -> Post comments and check out other people's profiles
* A way to save the countries on their profile bucket list or save where they've been
* User ability to add photos from their travels
* User ability to add written memories / journal
* Settings screen
* Side navigation menu

### 2. Screen Archetypes

* Log In Screen
  * User can log in
* Create account screen
  * User can create a new account
* Search / Map of all the countries
  * Users can click on / search different countries
* Phrases screen
  * A page for a country to learn the essential phrases for travel
* Favorite Phrases screen
  * A page for the User to view their favorite phrases
* Log Out screen
  * User can log out


### 3. Navigation

**Tab Navigation** (Tab to Screen)

Side menu navigation
* Can navigate between the Maps fragment and Settings fragment
  * Can logout from the side menu
  * Must navigate to the Phrases activity from within the Maps fragment

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
  * Click back > Map fragment
  * Slide up > Favorite Phrases panel

**Technical Complexity**

* Log In Screen
  * Use Parse database to check whether a user exists and if the correct username and password are used
  * Buttons and Intents can be used to navigate to either "create account" screen or to the main activity
* Sign Up Screen
  * Interact with Parse database to create an account and allow users to sign up
* Map / Search Screen
  * LottieDialog box with an animation pops up when the application is first opened
  * Autocomplete search feature
    * When a country is searched, it is highlighted using a GeoJsonLayer and imported country border coordinates from OpenStreetMap
    * Map automatically moves to the country searched in order for it to be easily clicked upon
  * Connect to Google Maps SDK
  * Click on a country to navigate to the Phrases activity
* Phrases Screen
  * The useful phrases of the main language that a country speaks are shown here
  * Google's Cloud Translation API allows for translations to the proper language based on the country clicked
  * Pronunciation functionality implemented using Android's built-in Text-To-Speech (TTS) API
    * If the language data is not saved onto the phone, a dialog pops where a user can opt to download the language data from the Google Play Store
    * User can use a button to listen to the phrase being pronounced
  * Users can favorite their favorite phrases using a toggle button
    * Favorite phrase toggle button status is saved using Android's SharedPreferences
    * Favorite phrases are saved onto the Parse database
  * A circular reveal/disappear animation was implemented to/from this screen
* Favorite Phrases panel
  * A user's favorite phrases are shown here
  * Communication with the Parse database must be done to grab all the Favorite Phrases
* Side Navigation Menu
  * Multiple tabs and icons are illustrated and users can click on them to navigate around the application

## Wireframes
![](https://i.imgur.com/aH37qRh.jpg)


### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema
* User
* ParsePhrase
* ParseFavoritePhrase
* FavoritePhrase
* Translation


### Models
* User

| Property      | Type | Description     |
| :---        |    :---   |          :--- |
| username   | String        | username of user for logging in      |
| password   | String        | password of user for logging in      |
| email      | String       | email of the user   |
| profileImage      | File       | user uploaded profile image   |

* ParsePhrase

| Property      | Type | Description     |
| :---        |    :---   |          :--- |
| phrase   | String        | a helpful phrase in the country's main language      |

* ParseFavoritePhrase

| Property      | Type | Description     |
| :---        |    :---   |          :--- |
| user   | Pointer (to a User)        | a pointer to the user that favorited the phrase   |
| countryName   | String        | the name of the country that the phrase belongs to      |
| languageCode   | String        | the iso639_1 two character code of the target translated language      |
| favoritePhrase   | Pointer (to a Phrase)        | a pointer to the Phrase object that the User favorited      |

* FavoritePhrase
  * Same model as the ParseFavoritePhrase, just another class to abstract the Parse logic away from the activity

* Translation

| Property      | Type | Description     |
| :---        |    :---   |          :--- |
| textToTranslate   | String        | the phrase to translate      |
| languageOfTranslation   | String        | language to translate to      |
| translation   | String        | translated phrase     |


### Networking
List of network requests by screen:
* Log In Screen
  * (Parse: Read/GET) Query username to check if login matches
  * (Parse: Read/GET) Query password to check if login matches
* Create account screen
  * (Parse: Update/PUT) Create a new user on the Parse database
* Search / Map of all the countries
  * (Read/GET) Map from Google SDK
* Phrases page
  * (Read/GET) Translations from Google Cloud Translate API
  * (Parse: GET) Phrases
  * (Parse: Update/PUT) Favorite phrases
  * (Parse: Update/PUT) Favorite countries


Example Parse network request code:
```
public FavoritePhrase getFavoritePhrase(String countryName, String phrase) {

        try {
            ParsePhrase parsePhrase = getParsePhrase(phrase);

            ParseQuery<ParseFavoritePhrase> query = ParseQuery.getQuery("FavoritePhrase");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("countryName", countryName);
            query.whereEqualTo("favoritePhrase", parsePhrase);

            List<ParseFavoritePhrase> favePhraseList = query.find();

            if (favePhraseList.size() > 0) {
                ParseFavoritePhrase firstFavePhrase = favePhraseList.get(0);
                FavoritePhrase favoritePhrase = new FavoritePhrase(firstFavePhrase.getCountryName(),
                        firstFavePhrase.getLanguageCode(),
                        firstFavePhrase.getFavoritePhrase().getPhrase());

                return favoritePhrase;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
```

Endpoints for existing API:
* To grab country names: https://restcountries.com/v2/all?fields=name,languages
* To grab translations: https://translation.googleapis.com/language/translate/v2?key={GOOGLE_API_KEY}=

How to run tests:
Input the following line into the command line: ./gradlew test 

Attributions:
Used two types of Star Icons for the Favorites functionality:
[Star icons created by Pixel perfect - Flaticon](https://www.flaticon.com/free-icons/star "star icons")

Used icons for navigation side menu:
[User icons created by Freepik](https://www.flaticon.com/free-icons/user)

Settings icon:
[Settings icons created by Ilham Fitrotul Hayat - Flaticon](https://www.flaticon.com/free-icons/settings)
