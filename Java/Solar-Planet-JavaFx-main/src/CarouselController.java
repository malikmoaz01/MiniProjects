import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;

public class CarouselController {

    private CarouselView view;
    private Planet[] planets;
    private int currentIndex;

    public CarouselController(CarouselView view) {
        this.view = view;
        planets = new Planet[]{
            new Planet("Mercury", "mercury.png", "The smallest planet in the Solar System and the closest to the Sun."),
            new Planet("Earth", "earth.png", "The third planet from the Sun."),
            new Planet("Mars", "mars.png", "The fourth planet from the Sun and the second-smallest planet in the Solar System."),
            new Planet("Jupiter", "jupiter.png", "The largest planet in the Solar System. It is a gas giant."),
            new Planet("Saturn", "saturn.png", "The sixth planet from the Sun and the second-largest in the Solar System, after Jupiter."),
            new Planet("Uranus", "uranus.png", "The seventh planet from the Sun. It has the third-largest planetary radius and fourth-largest planetary mass in the Solar System."),
            new Planet("Neptune", "neptune.png", "The eighth and farthest-known Solar planet from the Sun. It is a gas giant."),
            new Planet("Pluto", "Pluto.png", "The Last Planet . Now it doesnt exist."),
        };

        currentIndex = 0;
        showPlanet(currentIndex);

        view.getPrevButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showPreviousPlanet();
            }
        });

        view.getNextButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showNextPlanet();
            }
        });
    }

    private void showPlanet(int index) {
        Planet planet = planets[index];
        // Build the absolute file path
        String imagePath = "file:///" + System.getProperty("user.home").replace("\\", "/") + "/OneDrive/Desktop/Project/JavaFx/resources/" + planet.getImagePath();
        try {
            view.getImageView().setImage(new Image(imagePath));
            view.getDescriptionText().setText(planet.getDescription());
        } catch (Exception e) {
            System.out.println("Failed to load image from file: " + imagePath);
            e.printStackTrace();
        }
    }

    private void showPreviousPlanet() {
        currentIndex = (currentIndex - 1 + planets.length) % planets.length;
        showPlanet(currentIndex);
    }

    private void showNextPlanet() {
        currentIndex = (currentIndex + 1) % planets.length;
        showPlanet(currentIndex);
    }
}
