/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyect2;

/**
 *
 * @author sergi
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MovieAPIFrame extends JFrame {

    private JPanel contentPane;
    private JTextField searchField;
    private JComboBox<String> searchCriteriaComboBox;
    private JButton searchButton;
    private JPanel resultsPanel;

    private JComboBox<String> genreComboBox;
    private JLabel titleLabel;

    
    /*
    
    */
    public MovieAPIFrame() {
        setTitle("Movies from TMDb");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(contentPane);

        // Crear y añadir componentes de búsqueda
        JPanel searchPanel = createSearchPanel();
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);

        // Añadir una franja indicando "Más Populares" arriba de las películas
        titleLabel = new JLabel("Más Populares");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(createGenreComboBox(), BorderLayout.SOUTH);

        contentPane.add(topPanel, BorderLayout.NORTH);

        // Añadir un panel para mostrar los resultados de la búsqueda
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // Diseño horizontal
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Márgenes
        contentPane.add(resultsPanel, BorderLayout.CENTER);

        // Mostrar las películas más populares al inicio
        displayPopularMovies();

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

   
    
    private JPanel createGenreComboBox() {
        JPanel genrePanel = new JPanel();
        genrePanel.setLayout(new FlowLayout());

        genreComboBox = new JComboBox<>(getMovieGenres());
        genreComboBox.setSelectedIndex(0);  // Establecer la selección predeterminada

        // Añadir ActionListener al desplegable de géneros
        genreComboBox.addActionListener(e -> performGenreSearch());

        // Añadir el desplegable de géneros al panel
        genrePanel.add(new JLabel("Genre: "));
        genrePanel.add(genreComboBox);

        return genrePanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());

        JPanel searchInputPanel = new JPanel();
        searchInputPanel.setLayout(new FlowLayout());

        searchField = new JTextField(20);
        searchCriteriaComboBox = new JComboBox<>(new String[]{"Title", "Release Year"});
        searchButton = new JButton("Search");
        genreComboBox = new JComboBox<>(getMovieGenres());  // Añade el género al JComboBox

        // Agregar ActionListener al botón de búsqueda
        searchButton.addActionListener(e -> performSearch());
        searchCriteriaComboBox.addActionListener(e -> {
            String selectedCriteria = searchCriteriaComboBox.getSelectedItem().toString().toLowerCase();
            if ("genre".equals(selectedCriteria)) {
                performGenreSearch();
            } else {
                performSearch();
            }
        });

        // Añadir componentes al panel de búsqueda
        searchInputPanel.add(new JLabel("Search: "));
        searchInputPanel.add(searchField);
        searchInputPanel.add(new JLabel("Criteria: "));
        searchInputPanel.add(searchCriteriaComboBox);
        searchInputPanel.add(searchButton);

        JPanel searchResultsPanel = new JPanel();
        searchResultsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // Diseño similar al de las filas de resultados
        searchResultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Márgenes

        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        searchPanel.add(searchResultsPanel, BorderLayout.CENTER);

        return searchPanel;
    }

    private void displayPopularMovies() {
        List<JsonObject> popularMovies = getPopularMovies();
        resultsPanel.setLayout(new GridLayout(0, 4));  // Configura un diseño de cuadrícula con 4 columnas

        for (JsonObject movie : popularMovies) {
            String title = getStringOrNull(movie, "title");
            String posterPath = getStringOrNull(movie, "poster_path");

            // Crear el panel de película con el póster y el título
            resultsPanel.add(createMoviePosterPanel(title, posterPath, movie));
            titleLabel.setText("Más Populares");
        }

        // Revalidar y repintar el panel de resultados para reflejar los cambios en la interfaz gráfica
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private List<JsonObject> getPopularMovies() {
        List<JsonObject> popularMovies = new ArrayList<>();

        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";
            String language = "es-ES";

            String popularMoviesURL = "https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey
                    + "&language=" + language + "&page=1";

            HttpURLConnection popularMoviesConnection = (HttpURLConnection) new URL(popularMoviesURL).openConnection();
            popularMoviesConnection.setRequestMethod("GET");

            StringBuilder popularMoviesResponse;
            try ( BufferedReader popularMoviesReader = new BufferedReader(new InputStreamReader(popularMoviesConnection.getInputStream()))) {
                popularMoviesResponse = new StringBuilder();
                String line;
                while ((line = popularMoviesReader.readLine()) != null) {
                    popularMoviesResponse.append(line);
                }
            }

            JsonObject popularMoviesJsonObject = JsonParser.parseString(popularMoviesResponse.toString()).getAsJsonObject();
            JsonArray popularMoviesArray = popularMoviesJsonObject.getAsJsonArray("results");

            for (JsonElement element : popularMoviesArray) {
                popularMovies.add(element.getAsJsonObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return popularMovies;
    }

    private JPanel createBackButtonPanel() {
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Cambiado a FlowLayout vertical

        JButton backButton = new JButton("Back");

        // Agregar ActionListener al botón "Back"
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Limpiar el contenido del panel de resultados
                resultsPanel.removeAll();

                // Mostrar las películas más populares al volver a la página principal
                displayPopularMovies();

                // Revalidar y repintar el panel de resultados para reflejar los cambios en la interfaz gráfica
                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        });

        backButtonPanel.add(backButton);
        return backButtonPanel;
    }

    private void performSearch() {
        String query = searchField.getText();
        String criteria = searchCriteriaComboBox.getSelectedItem().toString().toLowerCase();

        List<JsonObject> searchResults = searchMovies(query, criteria);

        // Limpiar el contenido anterior en el panel de resultados antes de mostrar nuevos resultados
        JPanel searchResultsPanel = (JPanel) ((BorderLayout) contentPane.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        searchResultsPanel.removeAll();

        // Actualizar el nuevo panel de resultados de búsqueda con los pósters y títulos
        updateUIWithSearchResults(searchResults, searchResultsPanel);

        titleLabel.setText("Resultados");

        // Agregar el panel de botón "Back" al final de los resultados
        searchResultsPanel.add(createBackButtonPanel());

        // Revalidar y repintar el panel de resultados para reflejar los cambios en la interfaz gráfica
        searchResultsPanel.revalidate();
        searchResultsPanel.repaint();
    }

    private List<JsonObject> searchMovies(String query, String criteria) {
        List<JsonObject> searchResults = new ArrayList<>();

        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";
            String language = "es-ES";

            String searchURL = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey
                    + "&query=" + query + "&language=" + language + "&page=1";

            HttpURLConnection searchConnection = (HttpURLConnection) new URL(searchURL).openConnection();
            searchConnection.setRequestMethod("GET");

            StringBuilder searchResponse;
            try ( BufferedReader searchReader = new BufferedReader(new InputStreamReader(searchConnection.getInputStream()))) {
                searchResponse = new StringBuilder();
                String line;
                while ((line = searchReader.readLine()) != null) {
                    searchResponse.append(line);
                }
            }

            JsonObject searchJsonObject = JsonParser.parseString(searchResponse.toString()).getAsJsonObject();
            JsonArray searchResultsArray = searchJsonObject.getAsJsonArray("results");

            for (JsonElement element : searchResultsArray) {
                searchResults.add(element.getAsJsonObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResults;
    }

    private String getDirector(int movieId) {
        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";

            String creditsURL = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;

            HttpURLConnection creditsConnection = (HttpURLConnection) new URL(creditsURL).openConnection();
            creditsConnection.setRequestMethod("GET");

            StringBuilder creditsResponse;
            try ( BufferedReader creditsReader = new BufferedReader(new InputStreamReader(creditsConnection.getInputStream()))) {
                creditsResponse = new StringBuilder();
                String line;
                while ((line = creditsReader.readLine()) != null) {
                    creditsResponse.append(line);
                }
            }

            JsonObject creditsJsonObject = JsonParser.parseString(creditsResponse.toString()).getAsJsonObject();
            JsonArray crewArray = creditsJsonObject.getAsJsonArray("crew");

            for (JsonElement element : crewArray) {
                JsonObject crewMember = element.getAsJsonObject();
                if ("Director".equals(crewMember.get("job").getAsString())) {
                    return crewMember.get("name").getAsString();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    private String getTrailerLink(int movieId) {
        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";

            String videosURL = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + apiKey;

            HttpURLConnection videosConnection = (HttpURLConnection) new URL(videosURL).openConnection();
            videosConnection.setRequestMethod("GET");

            StringBuilder videosResponse;
            try ( BufferedReader videosReader = new BufferedReader(new InputStreamReader(videosConnection.getInputStream()))) {
                videosResponse = new StringBuilder();
                String line;
                while ((line = videosReader.readLine()) != null) {
                    videosResponse.append(line);
                }
            }

            JsonObject videosJsonObject = JsonParser.parseString(videosResponse.toString()).getAsJsonObject();
            JsonArray resultsArray = videosJsonObject.getAsJsonArray("results");

            for (JsonElement element : resultsArray) {
                JsonObject video = element.getAsJsonObject();
                if ("Trailer".equals(video.get("type").getAsString())) {
                    return "https://www.youtube.com/watch?v=" + video.get("key").getAsString();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    private String getMainActor(int movieId) {
        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";

            String creditsURL = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;

            HttpURLConnection creditsConnection = (HttpURLConnection) new URL(creditsURL).openConnection();
            creditsConnection.setRequestMethod("GET");

            StringBuilder creditsResponse;
            try ( BufferedReader creditsReader = new BufferedReader(new InputStreamReader(creditsConnection.getInputStream()))) {
                creditsResponse = new StringBuilder();
                String line;
                while ((line = creditsReader.readLine()) != null) {
                    creditsResponse.append(line);
                }
            }

            JsonObject creditsJsonObject = JsonParser.parseString(creditsResponse.toString()).getAsJsonObject();
            JsonArray castArray = creditsJsonObject.getAsJsonArray("cast");

            // Tomamos al primer actor como el actor principal
            if (castArray.size() > 0) {
                JsonObject mainActor = castArray.get(0).getAsJsonObject();
                return mainActor.get("name").getAsString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    private void updateUIWithSearchResults(List<JsonObject> searchResults, JPanel targetPanel) {
        int columnsPerRow = 4;

        // Utilizar un GridLayout para organizar los posters en filas
        targetPanel.setLayout(new GridLayout(0, columnsPerRow, 20, 20));

        // Limpiar el contenido anterior en el panel de resultados antes de mostrar nuevos resultados
        targetPanel.removeAll();

        // Agregar los posters y títulos al panel de resultados
        for (JsonObject movie : searchResults) {
            String title = getStringOrNull(movie, "title");
            String posterPath = getStringOrNull(movie, "poster_path");

            // Crear el panel de película con el póster y el título
            targetPanel.add(createMoviePosterPanel(title, posterPath, movie));
        }

        // Revalidar y repintar el panel de resultados para reflejar los cambios en la interfaz gráfica
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    private String getStringOrNull(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return element != null && !element.isJsonNull() ? element.getAsString() : "N/A";
    }

    private JPanel createMoviePanel(String title, String overview, String posterPath, String voteAverage, String director, String trailerLink, String mainActor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        try {
            if (posterPath != null && !posterPath.equals("N/A")) {
                BufferedImage img = loadImage(posterPath);

                // Escalar la imagen sin cortarla ni hacer zoom
                ImageIcon scaledIcon = new ImageIcon(img.getScaledInstance(150, 200, Image.SCALE_SMOOTH));

                JLabel picLabel = new JLabel(scaledIcon);
                panel.add(picLabel);
            } else {
                // Si la URL de la imagen es nula o "N/A", puedes agregar un JLabel con un texto alternativo
                JLabel noImageLabel = new JLabel("No Image Available");
                panel.add(noImageLabel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel);

        panel.setPreferredSize(new Dimension(200, 250)); // Ajusta según tus preferencias

        return panel;
    }

    private BufferedImage loadImage(String imageUrl) throws IOException {
        if (imageUrl.equals("N/A")) {
            // Si la URL de la imagen es "N/A", regresa null
            return null;
        }
        URL url = new URL("https://image.tmdb.org/t/p/w500" + imageUrl);
        return ImageIO.read(url);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MovieAPIFrame();
        });
    }

    private JPanel createMovieRowPanel(List<JsonObject> movies) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new GridLayout(1, 4, 20, 20)); // Utiliza GridLayout para organizar los posters en una fila
        rowPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Agrega márgenes alrededor del panel

        for (JsonObject movie : movies) {
            String title = getStringOrNull(movie, "title");
            String posterPath = getStringOrNull(movie, "poster_path");

            // Crear el panel de película con el póster y el título
            rowPanel.add(createMoviePosterPanel(title, posterPath, movie));
        }

        return rowPanel;
    }

    private JPanel createMoviePosterPanel(String title, String posterPath, JsonObject movie) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Ajusta los márgenes horizontalmente

        try {
            if (posterPath != null && !posterPath.equals("N/A")) {
                BufferedImage img = loadImage(posterPath);

                // Escalar la imagen sin cortarla ni hacer zoom
                ImageIcon scaledIcon = new ImageIcon(img.getScaledInstance(150, 200, Image.SCALE_SMOOTH));

                JLabel picLabel = new JLabel(scaledIcon);
                picLabel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        showMovieDetailsDialog(movie);
                    }
                });
                panel.add(picLabel);
            } else {
                // Si la URL de la imagen es nula o "N/A", puedes agregar un JLabel con un texto alternativo
                JLabel noImageLabel = new JLabel("No Image Available");
                panel.add(noImageLabel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel);

        panel.setPreferredSize(new Dimension(200, 250)); // Ajusta según tus preferencias

        return panel;
    }

    private void showMovieDetailsDialog(JsonObject movie) {
        String title = getStringOrNull(movie, "title");
        String overview = getStringOrNull(movie, "overview");
        String releaseDate = getStringOrNull(movie, "release_date");
        String mainActor = getMainActor(movie.get("id").getAsInt());
        String trailerLink = getTrailerLink(movie.get("id").getAsInt());
        String rating = getStringOrNull(movie, "vote_average");

        StringBuilder details = new StringBuilder();
        details.append("Title: ").append(title).append("\n\n");
        details.append("Overview: ").append(formatOverview(overview)).append("\n\n");
        details.append("Release Date: ").append(releaseDate).append("\n\n");
        details.append("Main Actor: ").append(mainActor).append("\n\n");
        details.append("Trailer Link: ").append(trailerLink).append("\n\n");
        details.append("Rating: ").append(rating);

        // Ajusta las dimensiones para hacer el cuadro de diálogo cuadrado
        int dialogWidth = 400; // Puedes ajustar el tamaño según tus preferencias
        int dialogHeight = 400;

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(dialogWidth, dialogHeight));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Movie Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String formatOverview(String overview) {
        StringBuilder formattedOverview = new StringBuilder();
        int charCount = 0;

        for (char c : overview.toCharArray()) {
            formattedOverview.append(c);
            charCount++;

            // Agrega un salto de línea después de cada 60 caracteres
            if (charCount % 60 == 0) {
                formattedOverview.append("\n");
            }
        }

        return formattedOverview.toString();
    }

    private String[] getMovieGenres() {
        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";
            String language = "es-ES";

            String genresURL = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=" + language;

            HttpURLConnection genresConnection = (HttpURLConnection) new URL(genresURL).openConnection();
            genresConnection.setRequestMethod("GET");

            StringBuilder genresResponse;
            try ( BufferedReader genresReader = new BufferedReader(new InputStreamReader(genresConnection.getInputStream()))) {
                genresResponse = new StringBuilder();
                String line;
                while ((line = genresReader.readLine()) != null) {
                    genresResponse.append(line);
                }
            }

            JsonObject genresJsonObject = JsonParser.parseString(genresResponse.toString()).getAsJsonObject();
            JsonArray genresArray = genresJsonObject.getAsJsonArray("genres");

            List<String> genreNames = new ArrayList<>();

            for (JsonElement element : genresArray) {
                JsonObject genre = element.getAsJsonObject();
                String name = genre.get("name").getAsString();
                genreNames.add(name);
            }

            return genreNames.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    private void performGenreSearch() {
        String selectedGenre = genreComboBox.getSelectedItem().toString();
        // Realiza la búsqueda por género utilizando la API de TMDb y muestra los resultados.
        // Implementa esta lógica según la estructura de la respuesta de la API.
        List<JsonObject> genreResults = searchMoviesByGenre(selectedGenre);
        updateUIWithSearchResults(genreResults, resultsPanel);
        // También puedes realizar otras acciones necesarias al realizar la búsqueda por género.
    }

    private List<JsonObject> searchMoviesByGenre(String selectedGenre) {
        List<JsonObject> genreResults = new ArrayList<>();

        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";
            String language = "es-ES";

            String genreId = getGenreIdByName(selectedGenre);

            if (!genreId.isEmpty()) {
                // Construir la URL para buscar películas por género
                String genreMoviesURL = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey
                        + "&language=" + language + "&page=1&with_genres=" + genreId;

                HttpURLConnection genreMoviesConnection = (HttpURLConnection) new URL(genreMoviesURL).openConnection();
                genreMoviesConnection.setRequestMethod("GET");

                StringBuilder genreMoviesResponse;
                try ( BufferedReader genreMoviesReader = new BufferedReader(new InputStreamReader(genreMoviesConnection.getInputStream()))) {
                    genreMoviesResponse = new StringBuilder();
                    String line;
                    while ((line = genreMoviesReader.readLine()) != null) {
                        genreMoviesResponse.append(line);
                    }
                }

                JsonObject genreMoviesJsonObject = JsonParser.parseString(genreMoviesResponse.toString()).getAsJsonObject();
                JsonArray genreMoviesArray = genreMoviesJsonObject.getAsJsonArray("results");

                for (JsonElement element : genreMoviesArray) {
                    genreResults.add(element.getAsJsonObject());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return genreResults;
    }

    private String getGenreIdByName(String genreName) {
        try {
            String apiKey = "09ffc714f9e2fe428ca1c61fc94c9411";
            String language = "es-ES";

            // Construye la URL para obtener la lista de géneros
            String genresURL = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=" + language;

            HttpURLConnection genresConnection = (HttpURLConnection) new URL(genresURL).openConnection();
            genresConnection.setRequestMethod("GET");

            StringBuilder genresResponse;
            try ( BufferedReader genresReader = new BufferedReader(new InputStreamReader(genresConnection.getInputStream()))) {
                genresResponse = new StringBuilder();
                String line;
                while ((line = genresReader.readLine()) != null) {
                    genresResponse.append(line);
                }
            }

            JsonObject genresJsonObject = JsonParser.parseString(genresResponse.toString()).getAsJsonObject();
            JsonArray genresArray = genresJsonObject.getAsJsonArray("genres");

            for (JsonElement element : genresArray) {
                JsonObject genre = element.getAsJsonObject();
                String name = genre.get("name").getAsString();
                if (name.equalsIgnoreCase(genreName)) {
                    return genre.get("id").getAsString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}

