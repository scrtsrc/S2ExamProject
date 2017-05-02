package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class S2ExamProject extends Application
  {

    @Override
    public void start(Stage stage) throws Exception
      {


        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GUI/View/ManagerEditView.fxml"));


        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
      }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
      {
        launch(args);
      }

  }
