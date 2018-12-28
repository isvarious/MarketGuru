package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application 
{
	@Override
	public void start(Stage primaryStage) throws Exception 
	{    			
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Main.fxml"));   
		
	    Parent root = (Parent) loader.load();
	    Scene scene = new Scene(root);
	    primaryStage.setTitle("Market Guru");
	    primaryStage.setScene(scene);
	    primaryStage.setResizable(false);
	    primaryStage.show();
	    primaryStage.getIcons().add(new Image("application/arm.png"));
	  
	    //centers on screen
	    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
	}

	public static void main(String[] args) 
	{   
		Application.launch(args);
	}
}
