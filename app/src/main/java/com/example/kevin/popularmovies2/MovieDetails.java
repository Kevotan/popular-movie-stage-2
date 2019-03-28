package com.example.kevin.popularmovies2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.popularmovies2.database.FavoriteMovie;
import com.example.kevin.popularmovies2.database.MovieDatabase;
import com.example.kevin.popularmovies2.model.Movie;
import com.example.kevin.popularmovies2.model.Review;
import com.example.kevin.popularmovies2.model.Trailer;
import com.example.kevin.popularmovies2.utils.JsonUtils;
import com.example.kevin.popularmovies2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetails extends AppCompatActivity implements TrailerAdapter.ListItemClickListener {

    private Movie movieItem;
    private ArrayList<Review> reviewList;
    private ArrayList<Trailer> trailerList;

    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MovieDatabase mDb;
    private ImageView mFavButton;
    private Boolean isFav = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError("Intent is null");
        }

        movieItem = (Movie) intent.getSerializableExtra("movieItem");
        if (movieItem == null) {
            closeOnError(getString(R.string.Error_NoMovieData));
            return;
        }

        mTrailerRecyclerView = findViewById(R.id.rv_trailers);
        mTrailerAdapter = new TrailerAdapter(this, trailerList, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mTrailerRecyclerView.setLayoutManager(mLayoutManager);

        mFavButton = findViewById(R.id.iv_favButton);
        mDb = MovieDatabase.getInstance(getApplicationContext());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final FavoriteMovie favoriteMovie = mDb.movieDao().loadMovieById(Integer.parseInt(movieItem.getId()));
                setFavorite(favoriteMovie != null);
            }
        });

        getMoreDetails(movieItem.getId());

    }

    private void setFavorite(Boolean fav) {
        if (fav) {
            isFav = true;
            mFavButton.setImageResource(R.drawable.ic_favorite_24dp);
        } else {
            isFav = false;
            mFavButton.setImageResource(R.drawable.ic_favorite_border_24dp);
        }
    }

    private static class SearchURLs {
        URL reviewSearchUrl;
        URL trailerSearchUrl;

        SearchURLs(URL reviewSearchUrl, URL trailerSearchUrl) {
            this.reviewSearchUrl = reviewSearchUrl;
            this.trailerSearchUrl = trailerSearchUrl;
        }
    }

    private static class ResultsStrings {
        String reviewString;
        String trailerString;

        ResultsStrings(String reviewString, String trailerString) {
            this.reviewString = reviewString;
            this.trailerString = trailerString;
        }
    }

    private void getMoreDetails(String id) {
        String reviewQuery = id + File.separator + "reviews";
        String trailerQuery = id + File.separator + "videos";
        SearchURLs searchURLs = new SearchURLs(
                NetworkUtils.buildURL(reviewQuery),
                NetworkUtils.buildURL(trailerQuery)
        );
        new ReviewsQueryTask().execute(searchURLs);
    }


    public class ReviewsQueryTask extends AsyncTask<SearchURLs, Void, ResultsStrings> {
        @Override
        protected ResultsStrings doInBackground(SearchURLs... params) {
            URL reviewSearchUrl = params[0].reviewSearchUrl;
            URL trailerSearchUrl = params[0].trailerSearchUrl;

            String reviewResults = null;
            try {
                reviewResults = NetworkUtils.getResponseFromHttpUrl(reviewSearchUrl);
                reviewList = JsonUtils.parseReviewsJson(reviewResults);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String trailerResults = null;
            try {
                trailerResults = NetworkUtils.getResponseFromHttpUrl(trailerSearchUrl);
                trailerList = JsonUtils.parseTrailersJson(trailerResults);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ResultsStrings results = new ResultsStrings(reviewResults, trailerResults);

            return results;
        }

        @Override
        protected void onPostExecute(ResultsStrings results) {
            String searchResults = results.reviewString;
            if (searchResults != null && !searchResults.equals("")) {
                reviewList = JsonUtils.parseReviewsJson(searchResults);
                populateDetails();
            }
        }
    }

    private void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        webIntent.putExtra("finish_on_ended", true);
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    public void OnListItemClick(Trailer trailerItem) {
        watchYoutubeVideo(trailerItem.getKey());
    }

    private void populateDetails() {

        ((TextView) findViewById(R.id.tv_title)).setText(movieItem.getTitle());
        ((TextView) findViewById(R.id.tv_header_rating)).append(" (" + movieItem.getVoteAverage() + "/10)");
        ((RatingBar) findViewById(R.id.rbv_user_rating)).setRating(Float.parseFloat(movieItem.getVoteAverage()));
        ((TextView) findViewById(R.id.tv_release_date)).setText(movieItem.getReleaseDate());
        ((TextView) findViewById(R.id.tv_overview)).setText(movieItem.getOverview());


        // Favorite
        mFavButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                final FavoriteMovie mov = new FavoriteMovie(
                        Integer.parseInt(movieItem.getId()),
                        movieItem.getTitle(),
                        movieItem.getReleaseDate(),
                        movieItem.getVoteAverage(),
                        movieItem.getOverview(),
                        movieItem.getPosterPath()
                );


                AppExecutors.getInstance().diskIO().execute(new Runnable() {

                    @Override
                    public void run() {
                        if (isFav) {
                            // delete item
                            mDb.movieDao().deleteMovie(mov);
                        } else {
                            // insert item
                            mDb.movieDao().insertMovie(mov);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setFavorite(!isFav);
                            }
                        });
                    }

                });
            }
        });


        mTrailerAdapter.setTrailerData(trailerList);

        ((TextView) findViewById(R.id.tv_reviews)).setText("");
        for (int i = 0; i < reviewList.size(); i++) {
            ((TextView) findViewById(R.id.tv_reviews)).append("\n");
            ((TextView) findViewById(R.id.tv_reviews)).append(reviewList.get(i).getContent());
            ((TextView) findViewById(R.id.tv_reviews)).append("\n\n");
            ((TextView) findViewById(R.id.tv_reviews)).append(" - Reviewed by ");
            ((TextView) findViewById(R.id.tv_reviews)).append(reviewList.get(i).getAuthor());
            ((TextView) findViewById(R.id.tv_reviews)).append("\n\n--------------\n");
        }

        String imagePathURL = NetworkUtils.buildPosterUrl(movieItem.getPosterPath());

        try {
            Picasso.get()
                    .load(imagePathURL)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into((ImageView) this.findViewById(R.id.iv_image));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void closeOnError(String msg) {
        finish();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}