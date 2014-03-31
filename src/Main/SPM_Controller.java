/*
 * This file is part of the Skyrim Savegame Utilities Project: https://code.google.com/p/skyrim-savegame-utilities/
 *  
 * Copyright (c) 2014 Rick Falck
 *  
 * This code is licensed under the terms of the Apache License Version 2.0.
 * You may use this code according to the license.
 *
 * The terms of the license can be found in the root directory of this project's repository as well as at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under these Licenses is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See each License for the specific language governing permissions and
 * limitations under that License.
 */

package Main;

import Function_Savegame_Savior.SaveGameSavior;
import Function_XML_Import_Export.XML_Handler;
import Language.LanguageResources;
import Main.ToggleButton.BUTTON_TYPE;
import Server.SPMSocketService;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.xml.stream.XMLStreamException;

/***
 * @author Rick Falck
 * @version 1.0
 */
public class SPM_Controller implements Initializable, ToggleButton.ICallback, SaveGameSavior.UICallback {

    @FXML
    private Button mBTN_Clear_NW_Messages;

    @FXML
    private Button mBTN_Clear_Savior_Messages;

    @FXML
    private Button mBTN_Directory;

    @FXML
    private Button mBTN_Help;

    @FXML
    private Button mBTN_OpenExplorer_Network;

    @FXML
    private Button mBTN_OpenExplorer_Savior;

    @FXML
    private Button mBTN_PCSync_OnOff;

    @FXML
    private Button mBTN_SPM_OnOff;

    @FXML
    private Button mBTN_Savior_OnOff;

    @FXML
    private Button mBTN_Start_Stop;

    @FXML
    private CheckBox mCHK_AutoStart;

    @FXML
    private CheckBox mCHK_Fullsaves;

    @FXML
    private CheckBox mCHK_OtherSaves;

    @FXML
    private CheckBox mCHK_Quicksaves;

    @FXML
    private VBox mError_Vbox;

    @FXML
    private Label mLBL_Directory;

    @FXML
    private Label mLBL_ErrorText;

    @FXML
    private Label mLBL_Networking_Title;

    @FXML
    private Label mLBL_Num_Saves;

    @FXML
    private Label mLBL_PCSyncXML;

    @FXML
    private Label mLBL_PC_IP;

    @FXML
    private Label mLBL_PotionMaker;

    @FXML
    private Label mLBL_SPM_Port;

    @FXML
    private Label mLBL_Savior_Title;

    @FXML
    private Label mLBL_Title;

    @FXML
    private Label mLBL_Your_IP;

    @FXML
    private TextField mNumberSavesText;

    @FXML
    private TextField mOtherSaveGame;

    @FXML
    private TextField mPortID_TextField;

    @FXML
    private VBox mSavior_Vbox;

    @FXML
    private ScrollPane mScrollPane;

    @FXML
    private ScrollPane mScrollPane_Savior;

    public static Stage mPrimaryStage;
    public static Application mMyApp;

    private final String mTooltipBackgroundStyle = "-fx-background-color: #ffffc0;";
    private Tooltip mBTNDirectoryTip;
    private Tooltip mCHK_AutoStartTip;

    private UserData mUserData;
    private FunctionMaster mFunctionMaster;

    private MyServerCallbacks mServerCbs = null;

    private boolean mOnCloseCreated = false;

    private ToggleButton mStartButton;
    private ToggleButton mSaviorButton;
    private ToggleButton mPCSyncXMLButton;
    private ToggleButton mPotionMakerButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	mScrollPane.setContent(mError_Vbox);
	mScrollPane_Savior.setContent(mSavior_Vbox);
	// Directory Button for location of savegame files    
	mBTN_Directory.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		onBTNSelectDirectory();
	    }

	});

	mBTN_Help.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		showHelp();
	    }

	});

	mCHK_AutoStart.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		mUserData.setStartOnStartup(mCHK_AutoStart.isSelected());
	    }

	});
	
	mBTN_Clear_Savior_Messages.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		mSavior_Vbox.getChildren().clear();
	    }

	});
	
	mBTN_Clear_NW_Messages.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		mError_Vbox.getChildren().clear();
	    }

	});
	
	mBTN_OpenExplorer_Savior.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		try {
		    Desktop.getDesktop().open(new File(mUserData.getSaveGame_Directory()));
		} catch (IOException ioe) {
		    
		}
	    }

	});
	
	mBTN_OpenExplorer_Network.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		try {
		    
		    Desktop.getDesktop().open(Paths.get(XML_Handler.sXML_DIR).toFile());
		} catch (IOException ioe) {
		    
		}
	    }

	});

	// Read the User Data file
	try {
	    mUserData = UserData.get();
	    mUserData.getUserData();
	    LanguageResources.SetLanguage(mUserData.getLanguage_XML_Filename());
	} catch (IOException | XMLStreamException | NumberFormatException e) {
	    e.printStackTrace();
	    mLBL_ErrorText.setText("No language file found!");
	}

	mStartButton = new ToggleButton(this, BUTTON_TYPE.START_STOP, mBTN_Start_Stop);
	mSaviorButton = new ToggleButton(this, BUTTON_TYPE.SAVIOR, mBTN_Savior_OnOff);
	mPCSyncXMLButton = new ToggleButton(this, BUTTON_TYPE.PC_SYNC_XML, mBTN_PCSync_OnOff);
	mPotionMakerButton = new ToggleButton(this, BUTTON_TYPE.POTION_MAKER, mBTN_SPM_OnOff);

	if (mUserData.isRunSavior()) {
	    mSaviorButton.setState(true);
	}

	if (mUserData.isRunPCSync()) {
	    mPCSyncXMLButton.setState(true);
	}

	if (mUserData.isRunSPM()) {
	    mPotionMakerButton.setState(true);
	}

	setUserStrings();

	if (mUserData.getSaveGame_Directory() != null) {
	    mLBL_Directory.setText(mUserData.getSaveGame_Directory());
	    mNumberSavesText.setText(mUserData.getNumberSavesText());
	}
	mCHK_AutoStart.setSelected(mUserData.isStartOnStartup());

	mServerCbs = new MyServerCallbacks();

	mCHK_Quicksaves.setSelected(mUserData.isSPM_Do_Quicksaves());
	mCHK_Fullsaves.setSelected(mUserData.isSPM_Do_Fullsaves());
	mCHK_OtherSaves.setSelected(mUserData.isSPM_Do_Othersaves());
	mOtherSaveGame.setText(mUserData.getSPM_Othersave_Filename());

	try {
	    mLBL_PC_IP.setText(InetAddress.getLocalHost().getHostAddress());
	} catch (UnknownHostException e) {
	    mLBL_PC_IP.setText(LanguageResources.get("error_unknown_host"));
	}

	int port = mUserData.getPortNumber();

	mPortID_TextField.setText(String.valueOf(port));

	if (mUserData.isStartOnStartup()) {
	    onBTNStartStop(true);
	}
	
	mFunctionMaster = new FunctionMaster(this, new MyServerCallbacks());
    }

    private void onBTNSelectDirectory() {
	DirectoryChooser dc = new DirectoryChooser();
	dc.setInitialDirectory(new File(System.getProperty("user.home")));
	dc.setTitle(LanguageResources.get("main_directory_chooser"));

	File bdir = dc.showDialog(mBTN_Directory.getScene().getWindow());

	if (bdir != null) {
	    mUserData.setSaveGame_Directory(bdir.getAbsolutePath());
	    mLBL_Directory.setText(bdir.getAbsolutePath());
	} else {
	    mUserData.setSaveGame_Directory(null);
	}
    }

    private void showHelp() {
	Path helpFile = FileSystems.getDefault().getPath("Resources"
		+ File.separator + "HelpFiles", "MainHelpFile.html");
	try {
	    java.awt.Desktop.getDesktop().open(helpFile.toFile());
	} catch (IllegalArgumentException | IOException ex) {
	    mLBL_ErrorText.setText(LanguageResources.get("error_help_file"));
	}
    }

    @Override
    public void onToggleButtonClick(BUTTON_TYPE type, boolean bStart) {
	mLBL_ErrorText.setText("");
	if (type == BUTTON_TYPE.START_STOP) {
	    onBTNStartStop(bStart);
	    return;
	}

	boolean error = false;
	if (bStart && !editSaveGameDirectory()) {
	    error = true;
	}
	switch (type) {
	    case SAVIOR:
		if (error) {
		    mSaviorButton.setState(false);
		} else {
		    if (bStart && !editNumSaves()) {
			mSaviorButton.setState(false);
			error = true;
		    } else {
			mUserData.setRunSavior(bStart);
		    }
		}
		break;
	    case PC_SYNC_XML:
		if (error || (bStart && !editPortNumber())) {
		    mPCSyncXMLButton.setState(false);
		    error = true;
		} else {
		    mUserData.setRunPCSync(bStart);
		}
		break;
	    case POTION_MAKER:
		if (error || ((bStart && (!editPortNumber() || !editFileOptions())))) {
		    mPotionMakerButton.setState(false);
		    error = true;
		} else {
		    mUserData.setRunSPM(bStart);
		}
		break;
	    default:
		throw new IllegalArgumentException();
	}

	if (!bStart && mStartButton.isStarted() && !mUserData.isRunPCSync()
		&& !mUserData.isRunSPM() && !mUserData.isRunSavior()) {
	    mStartButton.setState(false);
	    mFunctionMaster.stopAll();
	}

	// If a function is started or stopped when the functions are already running
	// we may need to start or stop the DirectoryWatcher or SocketService
	// The FunctionMaster only stops a service if no functions that use it are on.
	// The FunctionMaster only starts a service if one or more services are on that use it.
	if (!error && mStartButton.isStarted()) {

	    // The DirectoryWatcher is used by the Savegame Savior and Potion Maker services
	    if (type == BUTTON_TYPE.SAVIOR || type == BUTTON_TYPE.POTION_MAKER) {
		if (bStart) {
		    try {
			mFunctionMaster.startDirectoryWatcher();
		    } catch (IOException e) {
			mLBL_ErrorText.setText(LanguageResources.get("error_exception " + e.getMessage()));
		    }
		} else {
		    mFunctionMaster.stopDirectoryWatcher();
		}
	    }

	    // The Socket Service is used by PC Sync and Potion Maker
	    if (type == BUTTON_TYPE.PC_SYNC_XML || type == BUTTON_TYPE.POTION_MAKER) {
		if (bStart) {
		    mFunctionMaster.startSocketService();
		} else {
		    mFunctionMaster.stopSocketService();
		}
	    }

	}
    }

    public void onBTNStartStop(boolean bStart) {
	// Set listener for closing the application to stop the watcher if it's running
	if (!mOnCloseCreated) {
	    mOnCloseCreated = true;
	    mPrimaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent we) {
		    mFunctionMaster.stopAll();
		    try {
			mUserData.saveUserData();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}

	    });
	}

	mLBL_ErrorText.setText("");
	if (bStart) {
	    if (!mUserData.isRunPCSync() && !mUserData.isRunSPM() && !mUserData.isRunSavior()) {
		mLBL_ErrorText.setText(LanguageResources.get("error_start_no_functions_on"));
		mStartButton.setState(false);
		return;
	    }
	    
	    if (!editSaveGameDirectory()) {
		mLBL_ErrorText.setText(LanguageResources.get("error_directory_notselected"));
		mStartButton.setState(false);
		return;
	    }

	    if (mUserData.isRunSavior()) {
		if (!editNumSaves()) {
		    mStartButton.setState(false);
		    return;
		}
	    }

	    if (mUserData.isRunPCSync() || mUserData.isRunSPM()) {
		if (!editPortNumber()) {
		    mStartButton.setState(false);
		    return;
		}
	    }

	    if (mUserData.isRunSPM()) {
		if (!editFileOptions()) {
		    mStartButton.setState(false);
		    return;
		}
	    }

	    mUserData.setStartOnStartup(mCHK_AutoStart.isSelected());
	    mLBL_ErrorText.setText("");

	    try {
		mFunctionMaster.startDirectoryWatcher();
		mFunctionMaster.startSocketService();
	    } catch (IOException e) {

	    }

	    mPrimaryStage.setIconified(true);

	} else {
	    mStartButton.setState(false);
	    mFunctionMaster.stopAll();
	}
    }

    private boolean editSaveGameDirectory() {
	if (mUserData.getSaveGame_Directory() == null) {
	    mLBL_ErrorText.setText(LanguageResources.get("error_directory_notselected"));
	    return false;
	}
	return true;
    }

    private boolean editPortNumber() {
	String port = mPortID_TextField.getText().trim();
	if (port.length() != 4) {
	    mLBL_ErrorText.setText(LanguageResources.get("error_invalid_port"));
	    return false;
	}

	int portNo;
	try {
	    portNo = Integer.parseInt(port);
	    mUserData.setPortNumber(portNo);
	} catch (NumberFormatException e) {
	    mLBL_ErrorText.setText(LanguageResources.get("error_invalid_port"));
	    mStartButton.setState(false);
	    return false;
	}
	mUserData.setPortNumber(portNo);
	return true;
    }

    private boolean editNumSaves() {
	String numSaves = mNumberSavesText.getText();
	if (numSaves == null || numSaves.length() == 0) {
	    mLBL_ErrorText.setText(LanguageResources.get("error_number_saves_notset"));
	    return false;
	}

	int nSaves;
	try {
	    nSaves = Integer.parseInt(numSaves);

	    if (nSaves < 3) {
		mLBL_ErrorText.setText(LanguageResources.get("error_number_saves_ltthree"));
		return false;
	    }
	} catch (NumberFormatException e) {
	    mLBL_ErrorText.setText(LanguageResources.get("error_number_saves_notint"));
	    return false;
	}
	mUserData.setNumberSaves(nSaves);
	return true;
    }

    private boolean editFileOptions() {
	String otherFn = mOtherSaveGame.getText();
	if (mCHK_OtherSaves.isSelected()) {
	    if (otherFn == null || otherFn.length() == 0) {
		mLBL_ErrorText.setText(LanguageResources.get("error_invalid_other_filename"));
		mStartButton.setState(false);
		return false;
	    }
	}
	mUserData.setSPM_Do_Saves(mCHK_Quicksaves.isSelected(), mCHK_Fullsaves.isSelected(),
		mCHK_OtherSaves.isSelected(), otherFn);
	return true;
    }

    private void setUserStrings() {
	mLBL_Title.setText(LanguageResources.get("main_title"));
	mLBL_Savior_Title.setText(LanguageResources.get("main_savior_title"));
	mLBL_Networking_Title.setText(LanguageResources.get("main_networking_title"));
	mLBL_PCSyncXML.setText(LanguageResources.get("main_pcsync_title"));
	mLBL_PotionMaker.setText(LanguageResources.get("main_pmo_title"));
	mLBL_Num_Saves.setText(LanguageResources.get("main_num_saves"));
	
	mBTN_OpenExplorer_Savior.setText(LanguageResources.get("main_button_savior_open"));
	mBTN_OpenExplorer_Network.setText(LanguageResources.get("main_button_pcsync_open"));

	mBTNDirectoryTip = new Tooltip(LanguageResources.get("main_button_directory_tooltip"));
	mBTNDirectoryTip.setStyle(mTooltipBackgroundStyle);
	Tooltip.install(mBTN_Directory, mBTNDirectoryTip);
	mBTN_Directory.setText(LanguageResources.get("main_button_directory"));

	mCHK_AutoStartTip = new Tooltip(LanguageResources.get("main_autostart_checkbox_tooltip"));
	mCHK_AutoStartTip.setStyle(mTooltipBackgroundStyle);
	Tooltip.install(mCHK_AutoStart, mCHK_AutoStartTip);
	mCHK_AutoStart.setText(LanguageResources.get("main_autostart_checkbox"));

	mNumberSavesText.setPromptText(LanguageResources.get("main_num_saves_hint"));
	mLBL_Your_IP.setText(LanguageResources.get("main_your_ip_label"));
	mLBL_SPM_Port.setText(LanguageResources.get("main_port_label"));

	mCHK_Quicksaves.setText(LanguageResources.get("main_checkbox_quicksaves"));
	mCHK_Fullsaves.setText(LanguageResources.get("main_checkbox_fullsaves"));

	mOtherSaveGame.setPromptText(LanguageResources.get("main_checkbox_othersaves_hint"));
    }

    @Override
    public void onSaviorEvent(String eventText) {
	mSavior_Vbox.getChildren().add(new Label(eventText));
    }

    private class MyServerCallbacks implements SPMSocketService.UICallbacks {

	public MyServerCallbacks() {
	}

	@Override
	public void onServerReceiveRequest(String infoText) {
	    mError_Vbox.getChildren().add(new Label(infoText));
	}

	@Override
	public void onServerSentDataToAndroid(String infoText) {
	    mError_Vbox.getChildren().add(new Label(infoText));
	}

	@Override
	public void onError(String errorText) {
	    mError_Vbox.getChildren().add(new Label(errorText));
	}

	@Override
	public void onServerException(String errorText) {
	    mError_Vbox.getChildren().add(new Label(errorText));
	}

	@Override
	public void onServerUpdate(String msg) {
	    mError_Vbox.getChildren().add(new Label(msg));
	}

    }

}
