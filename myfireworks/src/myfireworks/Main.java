package myfireworks;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private Fireworks fireworks = new Fireworks();

	@Override
	public void start(Stage primaryStage) throws Exception {
		initState(primaryStage);
		primaryStage.show();
		play();
	}
	
	public void play() {
		fireworks.start();
	}

	private void initState(Stage primaryStage) {
		Group root = new Group();
		primaryStage.setScene(new Scene(root,1024, 680));
		primaryStage.setResizable(false);
		primaryStage.setTitle("Fireworks");
		root.getChildren().add(fireworks);
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void stop() {
		fireworks.stop();
	}

}
