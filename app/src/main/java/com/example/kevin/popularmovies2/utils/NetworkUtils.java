package com.example.kevin.popularmovies2.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String MOVIE_API_KEY_QUERY_PARAM = "api_key";
    private static final String MOVIE_LANGUAGE_QUERY_PARAM = "language";
    private static final String MOVIE_API_URL = "https://api.themoviedb.org/3/movie";
    private final static String API_KEY = "Insert your API Key here";
    private final static String LANGUAGE = "en-US";

    private final static String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String WIDTH = "w185";


    public static URL buildURL(String movieQuery) {
        Uri builtUri = Uri.parse(MOVIE_API_URL).buildUpon()
                .appendEncodedPath(movieQuery)
                .appendQueryParameter(MOVIE_API_KEY_QUERY_PARAM, API_KEY)
                .appendQueryParameter(MOVIE_LANGUAGE_QUERY_PARAM, LANGUAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String buildPosterUrl(String poster) {

        String finalPath = POSTER_BASE_URL + WIDTH + "/" + poster;
        return finalPath;

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}