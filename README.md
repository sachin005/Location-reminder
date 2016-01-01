# Location-reminder
An Android app created as a part of my Android course

The working code is here: https://github.com/sachin005/Location-reminder/tree/master/Finalapp/app/src/main/java/s/chavan/finalapp

This is a simple app that helps you pin a location on Google Map and reminds it for you to refer later.
Motivation was to help remember the location of a car parked in a huge space.

Features:
- Records location pinned on the map
- Allows setting of timer for parking duration
- Notifies 5 mins before the time is up. NotificationManager is used to show a notification and also ring, vibrate, etc.
- A note can be added to the pinned location if needed. This is stored in the SQLite database along with the location and displayed when requested by the user.
- Records location points at regular intervals as the user walks around. These points are also stored in the database.
- Using the location points recorded, user can see the path he has taken from his car's location to get to where he is. User can walk back on the same path for getting back to the car. This path is drawn as a line connecting different location points recorded. To connect these points, Polylines are used which is part of the Google Maps API.


If you wish to try running this app, you'll have to add your own api key in the manifest file line# 27.
