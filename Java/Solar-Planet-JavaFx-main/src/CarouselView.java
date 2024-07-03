import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CarouselView extends BorderPane {

    private ImageView imageView;
    private Text descriptionText;
    private Button prevButton;
    private Button nextButton;

    public CarouselView() {
        imageView = new ImageView();
        imageView.setFitHeight(400);
        imageView.setFitWidth(600);

        descriptionText = new Text();
        descriptionText.setWrappingWidth(600);

        prevButton = new Button("Previous");
        nextButton = new Button("Next");

        VBox buttonBox = new VBox(10, prevButton, nextButton);
        buttonBox.setAlignment(Pos.CENTER);

        setCenter(imageView);
        setBottom(descriptionText);
        setRight(buttonBox);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Text getDescriptionText() {
        return descriptionText;
    }

    public Button getPrevButton() {
        return prevButton;
    }

    public Button getNextButton() {
        return nextButton;
    }
}
