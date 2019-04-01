# popular-movie-stage-2

<p float="left">
  <img src="https://github.com/Kevotan/popular-movie-stage-2/blob/master/screenshot-main.jpg" width="300">  
  <img src="https://github.com/Kevotan/popular-movie-stage-2/blob/master/screenshot-details.jpg" width="300">
</p>

## Instructions:

Please insert an API key for the Movie Database API in [`app/src/main/java/utils/NetworkUtils`](https://github.com/Kevotan/popular-movie-stage-2/blob/master/app/src/main/java/com/example/kevin/popularmovies2/utils/NetworkUtils.java)
in the following line of code: ```String API_KEY = "Insert your API Key here";```

## Overview:

- Main Screen:
  - Show movies in a List and order them by popularity or top rating
  - Filter movies by popularity, top rating or favorites
  - Users are able to launch a detail screen for each movie
  
- Detail Screen:
  - Allow users to read a description, reviews and see the rating of a selected movie
  - Allow users to watch trailers by launching a Youtube link
  - Allow users to mark and unmark movies as favorites to add or delete them from the local favorite movie collection
