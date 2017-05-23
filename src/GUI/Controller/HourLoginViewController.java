package GUI.Controller;

import BE.EnumCache.*;
import BE.Guild;
import GUI.Model.ModelFacade;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class HourLoginViewController implements Initializable
{

    @FXML
    private JFXButton btnLogHours;
    @FXML
    private Label lblUsernameTag;
    @FXML
    private JFXTextField txtUser;
    @FXML
    private Label lblHourTag;
    @FXML
    private JFXTextField txtHours;
    @FXML
    private Label lblHourTagTwo;
    @FXML
    private Label lblGuildTag;
    @FXML
    private JFXComboBox<Guild> cmbGuildChooser;
    @FXML
    private JFXButton btnLanguage;
    @FXML
    private JFXButton btnSeeInfo;
    @FXML
    private StackPane root;
    @FXML
    private VBox loginWindow;
    @FXML
    private Label lblUsername;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private Label lblWrongPass;
    @FXML
    private JFXButton btnLogin;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private Label lblPassword;
    @FXML
    private JFXButton btnIntDown;
    @FXML
    private JFXButton btnIntUp;
    @FXML
    private AnchorPane ancDarken;
    private String strLogThanks = "Thanks!";
    private String strContribution = "Your hours have been logged. Thank you!";
    private String strLogin = "Log In";
    private String strCancel = "Cancel";
    private Image iconDK, iconENG;
    private final ImageView imgViewLngBut = new ImageView();
    //Models used by this Controller
    private final static ModelFacade MOD_FACADE = new ModelFacade();

    private String username;
    private int guildID;
    private int hours;
    JFXButton btnDanish = new JFXButton();
    JFXButton btnEnglish = new JFXButton();
    boolean loggingIn = false;
    private final Service serviceLog = new Service()
    {
        @Override
        protected Task createTask()
        {
            return new Task()
            {
                @Override
                protected Object call() throws Exception
                {
                    loginEvent();

                    return null;
                }
            };
        }
    };

    private final Service serviceHours = new Service()
    {
        @Override
        protected Task createTask()
        {
            return new Task()
            {
                @Override
                protected Object call() throws Exception
                {
                    logHours();
                    return null;
                }
            };
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        preloadImages();
        imgViewLngBut.setImage(iconENG);
        btnLanguage.setGraphic(imgViewLngBut);
        btnLanguage.setText(MOD_FACADE.getLang("BTN_LANGUAGE"));
        cmbGuildChooser.setItems(FXCollections.observableArrayList(MOD_FACADE.getAllGuilds()));
        ModelFacade.setModelFacade(MOD_FACADE);
        addListener();
        setTextAll();
        rememberThisSession();
    }

    public void buttonPressed(KeyEvent ke)
    {
        if (ke.getCode() == KeyCode.ENTER)
        {
            loginEvent();
        }
    }

    @FXML
    private void LogHoursAction(ActionEvent event)
    {
        buttonsLocking(true);
        if (!txtUser.getText().isEmpty() && !txtHours.getText().isEmpty() && !cmbGuildChooser.getSelectionModel().isEmpty())
        {
            username = txtUser.getText();
            hours = Integer.parseInt(txtHours.getText());
            guildID = cmbGuildChooser.getSelectionModel().getSelectedItem().getId();
            loadingScreen(true);
            serviceHours.restart();
        }

        else
        {
            snackBarPopup("Please input information in all fields");
        }
    }

    @FXML
    private void ChangeLanguageAction(ActionEvent event)
    {
        buttonsLocking(true);
        languagePopup();

    }

    @FXML
    private void LogInAction(ActionEvent event)
    {
        buttonsLocking(true);
        loginPopup();
    }

    public void addListener()
    {
        txtHours.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                try
                {
                    if (newValue.matches("\\d*") && newValue.length() < 3)
                    {
                        if (Integer.parseInt(newValue) >= 25)
                        {
                            snackBarPopup("You Cannot Exceed 24 hours");
                            txtHours.setText(oldValue);
                        }
                        else if (Integer.parseInt(newValue) <= 0)
                        {
                            snackBarPopup("You Cannot log 0 hours");
                            txtHours.setText(oldValue);
                        }
                        else
                        {
                            int value = Integer.parseInt(newValue);
                        }
                    }
                    else
                    {
                        txtHours.setText(oldValue);
                    }
                }
                catch (NumberFormatException ex)
                {
                    //do nothing
                }
            }

        });
        new GUI.Model.AutoCompleteComboBoxListener<>(cmbGuildChooser);

        cmbGuildChooser.setConverter(new StringConverter<Guild>()
        {

            @Override
            public String toString(Guild object)
            {
                if (object == null)
                {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Guild fromString(String string)
            {
                Guild findGuild = null;
                for (Guild guild : cmbGuildChooser.getItems())
                {
                    if (guild.getName().equals(string))
                    {
                        return guild;
                    }

                }
                return findGuild;
            }
        });
    }

    /**
     * pops up a bordered VBox that disappear after a short moment.
     */
    public void snackBarPopup(String str)
    {
        int time = 3000;
        JFXSnackbar snackbar = new JFXSnackbar(root);
        snackbar.show(str, time);
        PauseTransition pause = new PauseTransition(Duration.millis(time - 2000));
        pause.setOnFinished(
                e -> buttonsLocking(false)
        );
        pause.play();

    }

    /**
     * Pops up a login view that plays a short fade in transition. Contains
     * event for the buttons that are within.
     */
    public void loginPopup()
    {
        //popup for the login
        loginWindow.visibleProperty().set(true);
        ancDarken.visibleProperty().set(true);
        MOD_FACADE.fadeInTransition(Duration.millis(500), ancDarken);
        MOD_FACADE.fadeInTransition(Duration.millis(500), loginWindow);

        if (!txtUser.getText().isEmpty() && txtUser.getText() != null)
        {
            txtUsername.setText(txtUser.getText());
        }

        txtPassword.setOnKeyPressed((event)
                ->
        {
            if (event.getCode() == KeyCode.ENTER)
            {
                if (!txtUsername.getText().isEmpty())
                {
                    serviceLog.restart();
                    loadingScreen(true);
                }
            }
        });
        btnLogin.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                if (!txtUsername.getText().isEmpty())
                {

                    serviceLog.restart();
                    loadingScreen(true);
                }
            }
        }
        );

        btnCancel.setOnAction(
                new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e
            )
            {
                MOD_FACADE.fadeOutTransition(Duration.millis(500), loginWindow)
                        .setOnFinished(
                                ev -> hideLoginWind()
                        );
                MOD_FACADE.fadeOutTransition(Duration.millis(500), ancDarken);

            }
        }
        );

    }

    /**
     * Pops up with a language selector
     */
    public void languagePopup()
    {
        //contains buttons = languages
        AnchorPane anch = new AnchorPane();
        int size = 75;
        HBox popup = new HBox();
        popup.getStyleClass().add("popup");
        popup.setStyle("-fx-background-color: #00c4ad;");
        popup.setPadding(new Insets(20));
        popup.setSpacing(20);
        popup.setPrefSize(size, size);
        popup.setMaxSize(size * 3, size * 3);
        anch.getChildren().add(popup);
        EventHandler changeLanguageHandler = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (event.getSource().equals(btnDanish))
                {
                    MOD_FACADE.setLang(Lang.DAN);
                    imgViewLngBut.setImage(iconDK);
                }
                else if (event.getSource().equals(btnEnglish))
                {
                    MOD_FACADE.setLang(Lang.ENG);
                    imgViewLngBut.setImage(iconENG);
                }
                setTextAll();
                MOD_FACADE.fadeOutTransition(Duration.millis(500), popup).setOnFinished(
                        e -> root.getChildren().remove(anch));

                buttonsLocking(false);
            }
        };

        btnDanish.getStyleClass().add("JFXRoundedButton");
        btnDanish.setStyle("-fx-background-color:#FFFFFF");
        btnDanish.setMinSize(size, size);
        btnDanish.setPrefSize(size, size);
        btnDanish.setMaxSize(size, size);
        btnDanish.setGraphic(new ImageView(iconDK));

        btnEnglish.getStyleClass().add("JFXRoundedButton");
        btnEnglish.setStyle(btnDanish.getStyle());
        btnEnglish.setMinSize(size, size);
        btnEnglish.setPrefSize(size, size);
        btnEnglish.setMaxSize(size, size);
        btnEnglish.setGraphic(new ImageView(iconENG));

        root.getChildren().add(anch);
        popup.getChildren().addAll(btnDanish, btnEnglish);
        anch.setBottomAnchor(popup, 10.0);
        anch.setLeftAnchor(popup, 10.0);
        MOD_FACADE.fadeInTransition(Duration.millis(500), popup);

        btnDanish.setOnAction(changeLanguageHandler);
        btnEnglish.setOnAction(changeLanguageHandler);

    }

    /**
     * Locks or unlocks buttons based on a boolean Locks if true and unlocks if
     * false
     *
     * @param dis
     */
    public void buttonsLocking(Boolean dis)
    {

        btnLanguage.setDisable(dis);
        btnSeeInfo.setDisable(dis);
        btnLogHours.setDisable(dis);
        txtUser.setDisable(dis);
        txtHours.setDisable(dis);
        cmbGuildChooser.setDisable(dis);
        btnIntDown.setDisable(dis);
        btnIntUp.setDisable(dis);
    }

    public void hideLoginWind()
    {
        ancDarken.visibleProperty().set(false);
        loginWindow.visibleProperty().set(false);
        buttonsLocking(false);

    }

    public void preloadImages()
    {
        iconDK = new Image(getClass().getResourceAsStream("/GUI/Images/danish.png"));
        iconENG = new Image(getClass().getResourceAsStream("/GUI/Images/english.png"));
    }

    private void setTextAll()
    {
        lblUsernameTag.setText(MOD_FACADE.getLang("USERNAME_TAG"));
        txtUser.setPromptText(MOD_FACADE.getLang("TXT_USERNAME_PROMPT"));
        lblHourTag.setText(MOD_FACADE.getLang("HOUR_TAG"));
        txtHours.setPromptText(MOD_FACADE.getLang("TXT_HOURS_PROMPT"));
        lblHourTagTwo.setText(MOD_FACADE.getLang("HOUR_TAG_TWO"));
        lblGuildTag.setText(MOD_FACADE.getLang("GUILD_TAG"));
        cmbGuildChooser.setPromptText(MOD_FACADE.getLang("CMB_GUILDCHOOSER_PROMPT"));
        btnLogHours.setText(MOD_FACADE.getLang("BTN_LOG_HOURS"));
        btnSeeInfo.setText(MOD_FACADE.getLang("BTN_SEE_INFO"));
        btnLanguage.setText(MOD_FACADE.getLang("BTN_LANGUAGE"));

        if (MOD_FACADE.getLangProperty().equals(Lang.ENG))
        {
            imgViewLngBut.setImage(iconENG);
        }
        else if (MOD_FACADE.getLangProperty().equals(Lang.DAN))
        {
            imgViewLngBut.setImage(iconDK);
        }

    }

    private void loginEvent()
    {

        MOD_FACADE.getUserFromLogin(txtUsername.getText(), txtPassword.getText());
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                if (MOD_FACADE.getCurrentUser() != null)
                {

                    MOD_FACADE.changeView(0);
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.close();
                }
                else
                {
                    lblWrongPass.visibleProperty().set(true);
                }
                loadingScreen(false);
            }
        });
    }

    private void logHours()
    {
        
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

        String dateString = sdf.format(date);

        MOD_FACADE.logHours(username, dateString,hours, guildID);
        
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                snackBarPopup(strContribution);
                System.out.println(cmbGuildChooser.getSelectionModel().getSelectedItem().getId() + "");
                loadingScreen(false);
            }
        });
    }

    private void loadingScreen(Boolean StartLoading)
    {
        if (StartLoading == true)
        {
            root.getChildren().add(MOD_FACADE.getLoadingScreen());
        }
        else
        {
            root.getChildren().remove(MOD_FACADE.getLoadingScreen());
        }
    }

    @FXML
    private void setNumberOfHoursEvent(ActionEvent event)
    {

        if ((event.getSource().equals(btnIntUp)))
        {
            if (txtHours.getText().isEmpty())
            {
                txtHours.setText("1");
            }
            else
            {
                int hours = Integer.parseInt(txtHours.getText());

                int currentHours = Integer.parseInt(txtHours.getText());
                currentHours++;
                txtHours.setText(currentHours + "");
            }
        }
        if ((event.getSource().equals(btnIntDown)))
        {

            if (txtHours.getText().isEmpty())
            {
                snackBarPopup("Invalid Action");
            }
            else
            {
                int hours = Integer.parseInt(txtHours.getText());
                hours--;
                txtHours.setText(hours + "");

            }
        }
    }

    public void rememberThisSession()
    {
        if (MOD_FACADE.loadSession() != null)
        {
            txtUser.setText(MOD_FACADE.loadSession().get("lastuser"));
            txtHours.setText(MOD_FACADE.loadSession().get("lasthours"));
            for (Guild guild : cmbGuildChooser.getItems()) {
                if(guild.getId() == Integer.parseInt(MOD_FACADE.loadSession().get("lastguild"))) {
                    cmbGuildChooser.getSelectionModel().select(guild);
                    return;
                }
            }
        }
    }
}
