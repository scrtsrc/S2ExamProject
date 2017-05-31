package GUI.Controller;

import BE.Day;
import BE.Guild;
import BE.User;
import GUI.Model.ModelFacade;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXPopup;

import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class UserInfoViewController implements Initializable

{

    @FXML
    private ImageView imgVwProfilePic;
    @FXML
    private JFXButton btnUpdatePhoto;
    @FXML
    private GridPane gridEdit;
    @FXML
    private JFXButton btnEditSave;
    @FXML
    private AnchorPane root;
    @FXML
    private HBox hBoxInvisBtn;
    @FXML
    private JFXTextField txtFSearchDate;
    @FXML
    private JFXButton btnLogout;
    @FXML
    private JFXButton btnAddHours;
    @FXML
    private JFXButton btnChangePassword;
    @FXML
    private JFXButton btnChangePWConfirm;
    @FXML
    private JFXButton btnCancelPW;
    @FXML
    private JFXButton btnAddHoursPOP;
    @FXML
    private JFXButton btnCancelPOP;
    @FXML
    private StackPane stckPanePasswordChanger;
    @FXML
    private JFXPasswordField txtOPassword;
    @FXML
    private JFXPasswordField txtNPassword;
    @FXML
    private JFXPasswordField txtNPasswordTwo;
    @FXML
    private Label lblGuilds;
    @FXML
    private TableView<Day> tableViewMain;
    @FXML
    private TableColumn<Day, String> colDate;
    @FXML
    private TableColumn<Day, String> colGuild;
    @FXML
    private TableColumn<Day, Integer> colHours;
    @FXML
    private JFXListView<Guild> listVwGuilds;
    @FXML
    private JFXDatePicker datePickerInPop;
    @FXML
    private JFXButton btnIntDown;
    @FXML
    private JFXTextField txtfldHoursInPop;
    @FXML
    private JFXButton btnIntUp;
    @FXML
    private JFXComboBox<Guild> comboboxGuildInPop;
    @FXML
    private Label lblDateInPop;
    @FXML
    private Label lblHoursInPop;
    @FXML
    private Label lblGuildInPop;
    @FXML
    private StackPane stckPaneAddHours;
    @FXML
    private HBox hBoxBtnsInPOP;
    @FXML
    private StackPane stackPdeleteHours;
    @FXML
    private ImageView imgVwDel;
    @FXML
    private ImageView imgVwEdit;

    //FXML Textfields
    @FXML
    TextField txtName;
    @FXML
    TextField txtPhone;
    @FXML
    TextField txtEmail;
    @FXML
    TextField txtAddress;
    @FXML
    TextField txtAddress2;

    //FXML Labels
    @FXML
    private Label lblOldPassword;
    @FXML
    private Label lblNewPassword;
    @FXML
    private Label lblNewPassword2;

    //Objects Used
    User currentUser;
    JFXPopup popup;
    JFXButton btnHighClearance = new JFXButton();
    JFXButton btnCancelEditInfo = new JFXButton();
    File newImg;
    FilteredList<Day> filteredData = new FilteredList(FXCollections.observableArrayList());
    //Variables Used
    boolean editing = false;
    boolean isIncorrect = false;
    boolean finishedService = true;
    Day dayToEdit = null;
    private Image profileImage;

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private final static ModelFacade MOD_FACADE = ModelFacade.getModelFacade();

    /*
    * Creates a service that runs in the background which contacts the database 
    * to update the user image to one that the user has chosen. The service also
    * records the updating of the image into the event logs stored on the 
    * database.
     */
    private final Service serviceSavePicture = new Service()
    {
        @Override
        protected Task createTask()
        {
            return new Task()
            {
                @Override
                protected Object call() throws Exception
                {
                    if (newImg != null)
                    {
                        try
                        {
                            MOD_FACADE.updateUserImage(currentUser, newImg);
                            MOD_FACADE.logEvent(new BE.Event(new Timestamp(new Date().getTime()), MOD_FACADE.getCurrentUser().getName() + " changed his/her image."));
                        }
                        catch (FileNotFoundException e)
                        {
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setHeaderText("Selected image is not found");
                            a.setContentText("File not found!");
                        }
                    }
                    return null;

                }
            };
        }
    };

    private final Service serviceInitializer = new Service()
    {
        @Override
        protected Task createTask()
        {
            return new Task()
            {
                @Override
                protected Object call() throws Exception
                {
                    if (profileImage == null)
                    {
                        setUserImage();
                    }
                    filteredData = new FilteredList<>(MOD_FACADE.getWorkedDays(currentUser), p -> true);
                    return null;

                }
            };
        }
    };

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        setCurrentUser(MOD_FACADE.getCurrentUser());
        setUserInfo();
        checkTypeOfUser();
        createEditFields();
        setTextAll();
        setupGuildList();
        setupTableView("Looking For Data");
        searchListener();
        setupDragDrop();
        serviceInitializer.start();
        serviceInitializer.setOnFailed(e
                -> System.out.println("Error"));

        serviceInitializer.setOnSucceeded(e
                ->
        {
            setupTableView(MOD_FACADE.getLang("STR_SEARCH_EMPTY"));
            if (profileImage != null)
            {
                imgVwProfilePic.setImage(profileImage);
            }
        });

    }

    /**
     * Sets up the drag and drop functionality of editing or deleting a certain
     * day worked. If the day dropped on the delete image, then it will delete
     * that day. If the day is dropped on the edit image then the edit day
     * worked pop up will appear.
     */
    private void setupDragDrop()
    {
        imgVwDel.setOnDragOver(event
                ->
        {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE))
            {

                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            }
            event.consume();
        });

        imgVwDel.setOnDragDropped(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent event)
            {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE))
                {
                    Day dayToDelete = tableViewMain.getSelectionModel().getSelectedItem();
                    MOD_FACADE.deleteWorkedDay(currentUser, dayToDelete);
                    event.setDropCompleted(true);
                    MOD_FACADE.fadeOutTransition(Duration.millis(250), stackPdeleteHours).setOnFinished(ez -> stackPdeleteHours.setVisible(false));

                    event.consume();
                }
            }
        });

        imgVwEdit.setOnDragOver(event
                ->
        {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE))
            {

                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            }
            event.consume();
        });

        imgVwEdit.setOnDragDropped(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent event)
            {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE))
                {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    dayToEdit = tableViewMain.getItems().get(draggedIndex);

                    event.setDropCompleted(true);
                    MOD_FACADE.fadeOutTransition(Duration.millis(250), stackPdeleteHours).setOnFinished(ez -> stackPdeleteHours.setVisible(false));
                    handleOpenAddHoursPopup();
                    event.consume();
                }
            }
        });

    }

    /**
     * Handles the popup that appears for both editing and adding hours worked.
     */
    private void handleOpenAddHoursPopup()
    {
        //Clears everything from popup
        datePickerInPop.setValue(null);
        txtfldHoursInPop.clear();
        txtfldHoursInPop.clear();

        //Unlocks all the buttons
        buttonsLocking(false);

        //Sets up the popup depending if it is editing or adding hours worked
        setupAddHoursPopup();

        stckPaneAddHours.setVisible(true);
        MOD_FACADE.fadeInTransition(Duration.millis(750), stckPaneAddHours);
    }

    /**
     * Locks buttons in the popup if set to true.
     *
     * @param dis = boolean
     */
    public void buttonsLocking(Boolean dis)
    {
        datePickerInPop.setDisable(dis);
        btnIntUp.setDisable(dis);
        btnIntDown.setDisable(dis);
        comboboxGuildInPop.setDisable(dis);

    }

    /**
     * Sets up the calendar (so user cannot choose beyond today's date); 
     * the combobox (guilds are loaded into there); adds a listener to the hour
     * butons; adds search functionality to combobox.
     */
    
    private void setupAddHoursPopup()
    {
        //If editing rather than adding new day worked
        if (dayToEdit!=null){
            //Sets calendar to date of editing day's
            LocalDate date = LocalDate.parse(dayToEdit.getDate());
            datePickerInPop.setValue(date);
            //Sets hours worked to editing day's
            txtfldHoursInPop.setText(String.valueOf(dayToEdit.getHour()));
            //Sets guild worked to editing day's
            comboboxGuildInPop.getEditor().textProperty().set(dayToEdit.getGuild());
            
        }
        //Cannot pick dates beyond today's date
        MOD_FACADE.formatCalendar(datePickerInPop);

        //Sets all the guilds in the combobox
        comboboxGuildInPop.setItems(MOD_FACADE.getAllSavedGuilds());

        //Adds a listener to the hour buttons
        txtfldHoursInPop.textProperty().addListener(new ChangeListener<String>()
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
                            MOD_FACADE.snackbarPopup(MOD_FACADE.getLang("STR_MAX_HOUR"), root);

                            txtfldHoursInPop.setText(oldValue);
                        }
                        else if (Integer.parseInt(newValue) <= 0)
                        {
                            MOD_FACADE.snackbarPopup(MOD_FACADE.getLang("STR_MIN_HOUR"), root);
                            txtfldHoursInPop.setText(oldValue);
                        }
                    }
                    else
                    {
                        txtfldHoursInPop.setText(oldValue);
                    }
                }
                catch (NumberFormatException ex)
                {
                    System.err.println("NumberFormatException");
                }
            }

        });

        //Search function of the combobox
        new GUI.Model.AutoCompleteComboBoxListener<>(comboboxGuildInPop);

        comboboxGuildInPop.setConverter(
                new StringConverter<Guild>()
        {

            @Override
            public String toString(Guild object
            )
            {
                if (object == null)
                {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Guild fromString(String string
            )
            {
                Guild findGuild = null;
                for (Guild guild : comboboxGuildInPop.getItems())
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
     * Changes the text field when the up or down button has been pressed.
     * 
     * @param event = when up or down button has been pressed.
     */
    @FXML
    private void setNumberOfHoursEvent(ActionEvent event)
    {

        if ((event.getSource().equals(btnIntUp)))
        {
            if (txtfldHoursInPop.getText().isEmpty())
            {
                txtfldHoursInPop.setText("1");
            }
            else
            {

                int currentHours = Integer.parseInt(txtfldHoursInPop.getText());
                currentHours++;
                txtfldHoursInPop.setText(currentHours + "");
            }
        }
        if ((event.getSource().equals(btnIntDown)))
        {

            if (txtfldHoursInPop.getText().isEmpty())
            {

                MOD_FACADE.snackbarPopup(MOD_FACADE.getLang("STR_INVALID_ACTION"), root);
            }
            else
            {
                int hours = Integer.parseInt(txtfldHoursInPop.getText());
                hours--;
                txtfldHoursInPop.setText(hours + "");

            }
        }
    }


    public void setCurrentUser(User currentUser)
    {
        this.currentUser = currentUser;
    }

    private void setupTableView(String str)
    {

        tableViewMain.setPlaceholder(new Label(str));
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colHours.setCellValueFactory(val -> val.getValue().hourProperty().asObject());
        colGuild.setCellValueFactory(cellData -> cellData.getValue().guildProperty());

        SortedList<Day> sortedData = new SortedList<>(filteredData);

        searchListener();

        sortedData.comparatorProperty().bind(tableViewMain.comparatorProperty());
        tableViewMain.setItems(sortedData);

        tableViewMain.setRowFactory(tv
                ->
        {
            TableRow<Day> row = new TableRow<>();

            row.setOnDragDetected(event
                    ->
            {
                if (!row.isEmpty())
                {
                    stackPdeleteHours.setVisible(true);
                    MOD_FACADE.fadeInTransition(Duration.millis(250), stackPdeleteHours);

                    int selectedDayIndex = tableViewMain.getSelectionModel().getSelectedIndex();

                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();

                    // Store row ID in order to know what is dragged.
                    cc.put(SERIALIZED_MIME_TYPE, selectedDayIndex);
                    db.setContent(cc);

                    event.consume();
                }
            });
            row.setOnDragDone(new EventHandler<DragEvent>()
            {
                @Override
                public void handle(DragEvent e)
                {
                    System.out.println("removes stackpane");
                    MOD_FACADE.fadeOutTransition(Duration.millis(250), stackPdeleteHours).setOnFinished(ez -> stackPdeleteHours.setVisible(false));

                    e.consume();
                }
            });

            return row;
        });

    }

    private void searchListener()
    {
        txtFSearchDate.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)
                ->
        {
            filteredData.setPredicate(day
                    ->
            {
                String regex = "[^a-zA-Z0-9\\s]";
                Boolean search
                        = day.dateProperty().getValue().replaceAll(regex, "")
                                .contains(newValue.replaceAll(regex, ""))
                        || day.guildProperty().getValue().toLowerCase().replaceAll(regex, "").
                                contains(newValue.toLowerCase().replaceAll(regex, ""));

                return search;

            });
        });
    }

    private void setupGuildList()
    {
        listVwGuilds.setItems(FXCollections.observableArrayList(currentUser.getGuildList()));
    }

    /**
     * Check what type of User this is, if it's a Manager or Administrator, a
     * button will be created.
     *
     * @param 1 = Manager, 2 = Admin
     */
    private void checkTypeOfUser()

    {
        switch (currentUser.getType())
        {
            case 0:
                break;
            case 1:
                createHighClearanceButton(1);
                break;

            case 2:
                createHighClearanceButton(2);
                listVwGuilds.setVisible(false);
                lblGuilds.setVisible(false);
                break;
        }
    }

    /**
     * Displays additional button for Manager and Administrators.
     *
     * @param type 1 = Manager, 2 = Admin
     */
    private void createHighClearanceButton(int type)

    {

        btnHighClearance.setPrefWidth(160);
        btnHighClearance.setId("btnConfirmTeal");
        btnHighClearance.toFront();
        btnHighClearance.setVisible(true);
        btnCancelEditInfo.setPrefHeight(25);

        hBoxInvisBtn.setAlignment(Pos.CENTER);
        hBoxInvisBtn.getChildren().add(btnHighClearance);

        btnHighClearance.getStylesheets().add("GUI/View/MainLayout.css");
        btnHighClearance.prefHeightProperty().set(btnChangePassword.getPrefHeight());

        btnHighClearance.prefWidthProperty().set(btnChangePassword.getPrefWidth());

        if (type == 1)
        {
            btnHighClearance.setText(MOD_FACADE.getLang("BTN_HIGHER_CLEARANCE_1"));

        }
        else
        {
            btnHighClearance.setText(MOD_FACADE.getLang("BTN_HIGHER_CLEARANCE_2"));

        }

        btnHighClearance.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {

                MOD_FACADE.changeView(1);

            }
        });

    }

    private void setUserInfo()
    {
        txtName.setText(currentUser.getName());
        txtPhone.setText(String.valueOf(currentUser.getPhone()));
        txtEmail.setText(currentUser.getEmail());
        txtAddress.setText(currentUser.getResidence());
        txtAddress2.setText(currentUser.getResidence2());
    }

    @FXML
    private void pressedEditSaveButton(ActionEvent event)

    {
        if (!editing)
        {

            editInfo();
            editing = true;
            btnEditSave.setText(MOD_FACADE.getLang("BTN_SAVE"));
            checkTextFields();
            addCancelButton();

        }
        else
        {
            if (isIncorrect && btnEditSave.isDisabled())
            {
                MOD_FACADE.timedSnackbarPopup(MOD_FACADE.getLang("STR_INVALID_INPUT"), root, 5000);
            }
            saveInfo(currentUser);
            editing = false;
            btnEditSave.setText(MOD_FACADE.getLang("BTN_EDIT"));
            checkTextFields();
            removeCancelButton();
            btnEditSave.setStyle("-fx-background-color:#00c4ad;");

        }
    }

    private void createEditFields()

    {

        txtPhone.setOnKeyReleased(new EventHandler<KeyEvent>()

        {
            @Override
            public void handle(KeyEvent event)
            {
                checkTextFields();

            }

        });
    }

    private void editInfo()
    {
        txtName.setEditable(true);
        txtEmail.setEditable(true);
        txtPhone.setEditable(true);
        txtAddress.setEditable(true);
        txtAddress2.setEditable(true);
    }

    private void saveInfo(User user)
    {
        MOD_FACADE.updateUserInfo(user.getId(), txtName.getText(), txtEmail.getText(), user.getType(), Integer.parseInt(txtPhone.getText()), user.getNote(), txtAddress.getText(), txtAddress2.getText());
        MOD_FACADE.logEvent(new BE.Event(new Timestamp(new Date().getTime()), MOD_FACADE.getCurrentUser().getName() + " edited personal information."));
        currentUser = MOD_FACADE.getUserInfo(user.getId());

        txtName.setEditable(false);
        txtEmail.setEditable(false);
        txtPhone.setEditable(false);
        txtAddress.setEditable(false);
        txtAddress2.setEditable(false);
        setUserInfo(); //update labels

    }

    private void checkTextFields()
    {
        boolean success = false;
        try

        {
            Integer.parseInt(txtPhone.getText());
            success = true;
        }
        catch (NumberFormatException e)
        {
            success = false;
            txtPhone.setStyle("-fx-background-color:red;");
            btnEditSave.setDisable(true);
        }
        if (success)
        {
            btnEditSave.setDisable(false);
            txtPhone.setStyle("");
            isIncorrect = false;
        }
        else
        {
            txtPhone.setStyle("-fx-background-color:red;");
            btnEditSave.setDisable(true);
            isIncorrect = true;

        }
    }

    @FXML
    private void pressedChangeImage(ActionEvent event)

    {
        FileChooser c = new FileChooser();
        c.setTitle("Select a new image");
        String[] extensions
                =

                {
                    "jpg", "jpeg", "png", "gif"
                };
        c.setSelectedExtensionFilter(new ExtensionFilter("Image files only", extensions));
        newImg = c.showOpenDialog(btnUpdatePhoto.getScene().getWindow());
        serviceSavePicture.restart();
        serviceSavePicture.setOnSucceeded(e
                ->
        {
            profileImage = null;
            serviceInitializer.restart();
        });

    }

    public void setUserImage()
    {

        profileImage = new Image(MOD_FACADE.getUserImage(currentUser));

    }

    private void addCancelButton()
    {

        int btnSavePosCol = GridPane.getColumnIndex(btnEditSave); //saving position
        int btnSavePosRow = GridPane.getRowIndex(btnEditSave);
        GridPane.setRowIndex(btnEditSave, GridPane.getRowIndex(btnEditSave) - 1); //moving save button one up

        btnCancelEditInfo.setText(MOD_FACADE.getLang("BTN_CANCEL")); //preparing cancel button
        btnCancelEditInfo.setButtonType(JFXButton.ButtonType.RAISED);
        btnCancelEditInfo.setStyle("-fx-background-color: #B0B0B0; -fx-font: 15px System;");
        btnCancelEditInfo.setPrefHeight(25);
        btnCancelEditInfo.setPrefWidth(77);
        btnCancelEditInfo.setTextFill(Color.WHITE);
        btnCancelEditInfo.setPadding(btnEditSave.getPadding());

        gridEdit.add(btnCancelEditInfo, btnSavePosCol, btnSavePosRow); //adding to the old position of save btn
        GridPane.setValignment(btnEditSave, VPos.CENTER);
        GridPane.setValignment(btnCancelEditInfo, VPos.CENTER);
        btnCancelEditInfo.setOnAction(new EventHandler<ActionEvent>()
        { //setting onAction, nothing changed, just show old labels again
            @Override
            public void handle(ActionEvent event)
            {
                txtName.setEditable(false);
                txtEmail.setEditable(false);
                txtPhone.setEditable(false);
                txtAddress.setEditable(false);
                txtAddress2.setEditable(false);

                removeCancelButton(); //if cancel button clicked, it will disappear
                editing = false;
                btnEditSave.setText(MOD_FACADE.getLang("BTN_EDIT"));

            }
        });
    }

    private void removeCancelButton()
    {
        GridPane.setRowIndex(btnEditSave, GridPane.getRowIndex(btnEditSave) + 1); //moving save button one down
        gridEdit.getChildren().remove(btnCancelEditInfo); //deleting cancel button from gridpane
        if (btnEditSave.isDisabled())
        {
            btnEditSave.setDisable(false);
        }
    }

    private void setTextAll()
    {
        btnUpdatePhoto.setText(MOD_FACADE.getLang("BTN_UPDATEPHOTO"));
        btnChangePassword.setText(MOD_FACADE.getLang("BTN_CHANGEPASS"));
        btnEditSave.setText(MOD_FACADE.getLang("BTN_EDIT"));
        btnLogout.setText(MOD_FACADE.getLang("BTN_LOGOUT"));

        colDate.setText(MOD_FACADE.getLang("COL_DATE"));
        colHours.setText(MOD_FACADE.getLang("COL_HOURS"));
        colGuild.setText(MOD_FACADE.getLang("COL_GUILD"));
        txtFSearchDate.setPromptText(MOD_FACADE.getLang("PROMPT_SEARCH"));
        lblGuilds.setText(MOD_FACADE.getLang("LBL_GUILDS"));

    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException
    {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("GUI/View/HourLoginView.fxml"));
        StackPane page = (StackPane) loader.load();

        MOD_FACADE.changeView(3);
        Stage stage = (Stage) btnEditSave.getScene().getWindow();
        stage.close();

    }

    @FXML

    private void changePasswordEvent(ActionEvent event)
    {
        int count;
        if (txtOPassword.getText().equals(txtNPassword.getText()))
        {
            count = -1;
        }
        else if (txtNPassword.getText().equals(txtNPasswordTwo.getText()))
        {
            count = MOD_FACADE.changePassword(currentUser, txtOPassword.getText(), txtNPassword.getText());
        }
        else
        {
            count = -2;
        }
        if (count > 0)
        {
            MOD_FACADE.timedSnackbarPopup(MOD_FACADE.getLang("STR_PASSWORD_CHANGE"), root, 5000);
            MOD_FACADE.logEvent(new BE.Event(new Timestamp(new Date().getTime()), currentUser.getName() + " changed his/her password."));
            hidePasswordChangerEvent();
        }

        else if (count == -1)
        {
            MOD_FACADE.timedSnackbarPopup(MOD_FACADE.getLang("STR_OLD_PW_MATCH_NEW"), root, 5000);
        }
        else if (count == -2)
        {
            MOD_FACADE.timedSnackbarPopup(MOD_FACADE.getLang("STR_PW_DOESNT_MATCH"), root, 5000);
        }

        else
        {
            MOD_FACADE.timedSnackbarPopup(MOD_FACADE.getLang("STR_OLD_PW_WRONG"), root, 5000);
        }

    }

    @FXML
    private void openAddHoursPopup(ActionEvent event)
    {
        handleOpenAddHoursPopup();
    }

    
    @FXML
    private void handleAddEditHours(ActionEvent event)
    {

        StackPane stckLoadScreen = MOD_FACADE.getLoadingScreen();
        root.getChildren().add(stckLoadScreen);

        buttonsLocking(true);

        if (datePickerInPop.getValue() != null && !txtfldHoursInPop.getText().isEmpty() && !comboboxGuildInPop.getSelectionModel().isEmpty())
        {

            String date = datePickerInPop.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int hours = Integer.parseInt(txtfldHoursInPop.getText());
            int guildID = comboboxGuildInPop.getSelectionModel().getSelectedItem().getId();
            int errorCode = 1;

            if (currentUser.getEmail() != null)
            {
                if (dayToEdit != null)
                {
                    MOD_FACADE.logWorkDay(currentUser.getEmail(), date, hours, guildID);
                    MOD_FACADE.logEvent(new BE.Event(new Timestamp(new Date().getTime()), MOD_FACADE.getCurrentUser().getName() + " added " + hours + " working hours to guild " + MOD_FACADE.getGuild(guildID).getName() + " on " + date + "."));
                }
                else
                {

                    //   MOD_FACADE.editHours(currentUser.getEmail(), date, hours, guildID);
                    MOD_FACADE.logEvent(new BE.Event(new Timestamp(new Date().getTime()), MOD_FACADE.getCurrentUser().getName() + " edited his/her working hours to " + hours + " in guild " + MOD_FACADE.getGuild(guildID).getName() + " on " + date + "."));
                }
                stckLoadScreen.setVisible(false);

                MOD_FACADE.snackbarPopup(MOD_FACADE.getLang("STR_NO_ERROR_CONTRIBUTION"), root);

            }
            else if (currentUser.getPhone() != 0)

            {
                if (dayToEdit != null)
                {
                    MOD_FACADE.logWorkDay(currentUser.getPhone() + "", date, hours, guildID);
                    MOD_FACADE.logEvent(new BE.Event(new Timestamp(new Date().getTime()), MOD_FACADE.getCurrentUser().getName() + " added " + hours + " working hours to guild " + MOD_FACADE.getGuild(guildID).getName() + " on " + date + "."));
                }
                else
                {

                    //    MOD_FACADE.editHours(currentUser.getPhone() + "", date, hours, guildID);
                    MOD_FACADE.logEvent(new BE.Event(new Timestamp(new Date().getTime()), MOD_FACADE.getCurrentUser().getName() + " edited his/her working hours to " + hours + " in guild " + MOD_FACADE.getGuild(guildID).getName() + " on " + date + "."));
                }
                stckLoadScreen.setVisible(false);
                MOD_FACADE.snackbarPopup(MOD_FACADE.getLang("STR_NO_ERROR_CONTRIBUTION"), root);

            }
        }

        else
        {
            MOD_FACADE.timedSnackbarPopup(MOD_FACADE.getLang("STR_INVALID_INPUT"), root, 5000);
        }

    }

    @FXML
    private void closeAddHoursPopup()
    {
        MOD_FACADE.fadeOutTransition(Duration.millis(750), stckPaneAddHours).setOnFinished(e -> stckPaneAddHours.setVisible(false));
        
    }

    @FXML
    private void openPasswordChangerEvent(ActionEvent event
    )
    {
        stckPanePasswordChanger.setVisible(true);
        MOD_FACADE.fadeInTransition(Duration.millis(750), stckPanePasswordChanger);

    }

    @FXML
    private void hidePasswordChangerEvent()

    {
        MOD_FACADE.fadeOutTransition(Duration.millis(750), stckPanePasswordChanger)
                .setOnFinished(e -> stckPanePasswordChanger.setVisible(false));

    }
}
