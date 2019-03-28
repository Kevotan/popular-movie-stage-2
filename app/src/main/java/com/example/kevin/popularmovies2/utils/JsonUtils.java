package com.example.kevin.popularmovies2.utils;

import com.example.kevin.popularmovies2.model.Movie;
import com.example.kevin.popularmovies2.model.Review;
import com.example.kevin.popularmovies2.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static ArrayList<Movie> parseMoviesJson(String json) {


        final String ID = "id";
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String VOTE = "vote_average";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";

        try {
            ArrayList<Movie> movieList = new ArrayList<>();
            JSONObject movieJson = new JSONObject(json);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String id = movie.getString(ID);
                String poster_path = movie.getString(POSTER_PATH);
                String title = movie.getString(TITLE);
                String release_date = movie.getString(RELEASE_DATE);
                String vote_average = movie.getString(VOTE);
                String overview = movie.getString(OVERVIEW);

                Movie newMovie = new Movie(id, title, release_date, vote_average, overview, poster_path);
                movieList.add(newMovie);
            }

            return movieList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Review> parseReviewsJson(String json) {

        try {

            final String REVIEW_RESULTS = "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String ID = "id";
            final String URL = "url";

            ArrayList<Review> reviewList = new ArrayList<>();
            JSONObject reviewJson = new JSONObject(json);
            JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_RESULTS);

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                String author = review.getString(AUTHOR);
                String content = review.getString(CONTENT);
                String id = review.getString(ID);
                String url = review.getString(URL);

                Review newReview = new Review(author, content, id, url);
                reviewList.add(newReview);
            }
            return reviewList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Trailer> parseTrailersJson(String json) {

        try {

            final String TRAILER_RESULTS = "results";
            final String NAME = "name";
            final String SITE = "site";
            final String KEY = "key";

            ArrayList<Trailer> trailerList = new ArrayList<>();
            JSONObject trailerJson = new JSONObject(json);
            JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_RESULTS);

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                String name = trailer.getString(NAME);
                String site = trailer.getString(SITE);
                String key = trailer.getString(KEY);

                Trailer newTrailer = new Trailer(name, site, key);
                trailerList.add(newTrailer);
            }
            return trailerList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}