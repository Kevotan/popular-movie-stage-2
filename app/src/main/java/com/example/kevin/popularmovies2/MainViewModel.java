package com.example.kevin.popularmovies2;

        import android.app.Application;

        import com.example.kevin.popularmovies2.database.FavoriteMovie;
        import com.example.kevin.popularmovies2.database.MovieDatabase;

        import java.util.List;

        import androidx.lifecycle.AndroidViewModel;
        import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavoriteMovie>> movies;

    public MainViewModel(Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<FavoriteMovie>> getMovies() {
        return movies;
    }
}