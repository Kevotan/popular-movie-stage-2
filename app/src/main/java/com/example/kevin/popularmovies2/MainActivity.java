package com.example.kevin.popularmovies2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kevin.popularmovies2.database.FavoriteMovie;
import com.example.kevin.popularmovies2.model.Movie;
import com.example.kevin.popularmovies2.utils.JsonUtils;
import com.example.kevin.popularmovies2.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private static final String SORT_POPULAR = "popular";
    private static final String SORT_TOP_RATED = "top_rated";
    private static final String SORT_FAVORITE = "favorite";
    private static String currentSort = SORT_POPULAR;

    private ArrayList<Movie> movieList;

    private RecyclerView mMovieRecyclerView;
    private MovieAdapter mMovieAdapter;

    private List<FavoriteMovie> favoriteMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieRecyclerView = findViewById(R.id.rv_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(movieList, this, this);
        mMovieRecyclerView.setAdapter(mMovieAdapter);

        favoriteMovies = new ArrayList<>();


        setupViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_popular && !currentSort.equals(SORT_POPULAR)) {
            ClearMovieItemList();
            currentSort = SORT_POPULAR;
            makeMovieSearchQuery();
            return true;
        }
        if (id == R.id.action_sort_top_rated && !currentSort.equals(SORT_TOP_RATED)) {
            ClearMovieItemList();
            currentSort = SORT_TOP_RATED;
            makeMovieSearchQuery();
            return true;
        }
        if (id == R.id.action_sort_favorite && !currentSort.equals(SORT_FAVORITE)) {
            ClearMovieItemList();
            currentSort = SORT_FAVORITE;
            makeMovieSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ClearMovieItemList() {
        if (movieList != null) {
            movieList.clear();
        } else {
            movieList = new ArrayList<>();
        }
    }

    private void makeMovieSearchQuery() {
        if (currentSort.equals(SORT_FAVORITE)) {
            ClearMovieItemList();
            for (int i = 0; i < favoriteMovies.size(); i++) {
                Movie mov = new Movie(
                        String.valueOf(favoriteMovies.get(i).getId()),
                        favoriteMovies.get(i).getTitle(),
                        favoriteMovies.get(i).getReleaseDate(),
                        favoriteMovies.get(i).getVoteAverage(),
                        favoriteMovies.get(i).getOverview(),
                        favoriteMovies.get(i).getPosterPath()
                );
                movieList.add(mov);
            }
            mMovieAdapter.setMovieData(movieList);
        } else {
            String movieQuery = currentSort;
            URL movieSearchUrl = NetworkUtils.buildURL(movieQuery);
            new MoviesQueryTask().execute(movieSearchUrl);
        }
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                movieList = JsonUtils.parseMoviesJson(searchResults);
                mMovieAdapter.setMovieData(movieList);
            }
        }
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovie> favs) {
                if (favs.size() > 0) {
                    favoriteMovies.clear();
                    favoriteMovies = favs;
                }
                makeMovieSearchQuery();
            }
        });
    }

    @Override
    public void OnListItemClick(Movie movieItem) {
        Intent myIntent = new Intent(this, MovieDetails.class);
        myIntent.putExtra("movieItem", movieItem);
        startActivity(myIntent);
    }
}