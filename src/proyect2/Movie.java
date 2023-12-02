/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.List;

/**
 *
 * @author Mi equipo
 */
public class Movie {
    
    private String titulo;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String director;
    private String rating;
    private String trailerLink;

    public Movie(String titulo, String originalTitle, String overview, String posterPath, String director, String rating, String trailerLink) {
        this.titulo = titulo;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.director = director;
        this.rating = rating;
        this.trailerLink = trailerLink;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }

    @Override
    public String toString() {
        return "Movie{" + "titulo=" + titulo + ", originalTitle=" + originalTitle + ", overview=" + overview + ", posterPath=" + posterPath + ", director=" + director + ", rating=" + rating + ", trailerLink=" + trailerLink + '}';
    }

    String getTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}
