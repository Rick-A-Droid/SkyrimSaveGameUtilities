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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import spm_library.SOCKET_DEFAULTS;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class UserData {

    private static UserData mInstance = null;

    public static UserData get() {
	if (mInstance == null) {
	    mInstance = new UserData();
	}
	return mInstance;
    }

    // Filename for saving the user data
    private final String sUserFilename = "Resources\\SkyrimSaves_SaveData.txt";

    // Prefix strings for the user data fields
    private final String mSaveLanguageXMLKey = "LANGUAGE_XML_FILE:";
    private final String mSaveDirKey = "SAVE_DIR:";
    private final String mSaveFnKey = "SAVE_FILENAME:";
    private final String mSaveNumSavesKey = "NUM_SAVES:";
    private final String mSaveStartupKey = "STARTUP:";
    private final String mSaveSaviorKey = "RUN_SAVIOR:";
    private final String mSavePCSyncKey = "RUN_PCSYNC:";
    private final String mSaveSPMKey = "RUN_SPM:";
    private final String mSavePortKey = "PORT_NUMBER:";

    private final String mSaveQuicksavesOption = "SPM_DO_QUICKSAVES:";
    private final String mSaveFullsavesOption = "SPM_DO_FULLSAVES:";
    private final String mSaveOthersavesOption = "SPM_DO_OTHERSAVES:";
    private final String mSaveOthersavesFilename = "SPM_OTHERSAVES_FILENAME:";

    // The data for the user data fields
    private String mLanguage_XML_Filename;
    private String mSaveGame_Directory;
    private String mSaveGame_Filename;
    private int mNumberSaves;
    private boolean mStartonStartup;
    private boolean mRunSavior;
    private boolean mRunPCSync;
    private boolean mRunSPM;
    private boolean mSPM_Do_Quicksaves;
    private boolean mSPM_Do_Fullsaves;
    private boolean mSPM_Do_Othersaves;
    private String mSPM_Othersave_Filename;
    private int mPortNumber;

    private boolean mDataChanged;

    private UserData() {
	mLanguage_XML_Filename = null;
	mSaveGame_Directory = null;
	mSaveGame_Filename = null;
	mNumberSaves = 5;
	mStartonStartup = false;
	mDataChanged = false;
	mRunSavior = false;
	mRunPCSync = false;
	mRunSPM = false;
	mSPM_Do_Quicksaves = true;
	mSPM_Do_Fullsaves = true;
	mSPM_Do_Othersaves = false;
	mPortNumber = SOCKET_DEFAULTS.DEFAULT_PORT;
    }

    public String getSaveGame_Directory() {
	return mSaveGame_Directory;
    }

    public void setSaveGame_Directory(String saveGame_Directory) {
	if (mSaveGame_Directory == null || !mSaveGame_Directory.equalsIgnoreCase(saveGame_Directory)) {
	    mSaveGame_Directory = saveGame_Directory;
	    mDataChanged = true;
	}
    }

    public int getNumberSaves() {
	return mNumberSaves;
    }

    public String getNumberSavesText() {
	return Integer.toString(mNumberSaves);
    }

    public void setNumberSaves(int numberSaves) {
	if (mNumberSaves != numberSaves) {
	    mNumberSaves = numberSaves;
	    mDataChanged = true;
	}
    }

    public boolean isStartOnStartup() {
	return mStartonStartup;
    }

    public void setStartOnStartup(boolean startOnStartup) {
	if (mStartonStartup != startOnStartup) {
	    mStartonStartup = startOnStartup;
	    mDataChanged = true;
	}
    }

    public String getLanguage_XML_Filename() {
	return mLanguage_XML_Filename;
    }

    public void setLanguage_XML_Filename(String language_XML_Filename) {
	mLanguage_XML_Filename = language_XML_Filename;
	mDataChanged = true;
    }

    public String getSaveGame_Filename() {
	return mSaveGame_Filename;
    }

    public void setSaveGame_Filename(String saveGame_Filename) {
	mSaveGame_Filename = saveGame_Filename;
	mDataChanged = true;
    }

    public boolean isStartonStartup() {
	return mStartonStartup;
    }

    public void setStartonStartup(boolean startonStartup) {
	mStartonStartup = startonStartup;
	mDataChanged = true;
    }

    public boolean isRunSavior() {
	return mRunSavior;
    }

    public void setRunSavior(boolean runSavior) {
	mRunSavior = runSavior;
	mDataChanged = true;
    }

    public void toggleRunSavior() {
	mRunSavior = !mRunSavior;
	mDataChanged = true;
    }
    
    public void toggleRunPCSync() {
	mRunPCSync = !mRunPCSync;
	mDataChanged = true;
    }

    public boolean isRunSPM() {
	return mRunSPM;
    }

    public void toggleRunSPM() {
	mRunSPM = !mRunSPM;
	mDataChanged = true;
    }

    public void setRunPCSync(boolean runSPM) {
	mRunPCSync = runSPM;
	mDataChanged = true;
    }
    
    public boolean isRunPCSync() {
	return mRunPCSync;
    }
    
    public void setRunSPM(boolean runSPM) {
	mRunSPM = runSPM;
	mDataChanged = true;
    }

    public int getPortNumber() {
	return mPortNumber;
    }

    public void setPortNumber(int portNumber) {
	mPortNumber = portNumber;
	mDataChanged = true;
    }

    public void setSPM_Do_Saves(boolean do_Quicksaves, boolean do_Fullsaves, boolean do_Othersaves, 
	    String otherSaveFilename) {
	mSPM_Do_Quicksaves = do_Quicksaves;
	mSPM_Do_Fullsaves = do_Fullsaves;
	mSPM_Do_Othersaves = do_Othersaves;
	mSPM_Othersave_Filename = otherSaveFilename;
	mDataChanged = true;
    }

    public boolean isSPM_Do_Quicksaves() {
	return mSPM_Do_Quicksaves;
    }

    public boolean isSPM_Do_Fullsaves() {
	return mSPM_Do_Fullsaves;
    }

    public boolean isSPM_Do_Othersaves() {
	return mSPM_Do_Othersaves;
    }

    public String getSPM_Othersave_Filename() {
	return mSPM_Othersave_Filename;
    }

    public void getUserData() throws IOException, NumberFormatException {
	File userFile = new File(sUserFilename);
	if (userFile.exists()) {
	    BufferedReader in = null;
	    try {
		FileReader rdr = new FileReader(userFile);
		in = new BufferedReader(rdr);
		String strIn;
		do {
		    strIn = in.readLine();

		    if (strIn != null) {
			int pos = strIn.indexOf(":");
			if (pos > 0) {
			    pos++;
			    String key = strIn.substring(0, pos).trim();
			    String data = strIn.substring(pos).trim();

			    switch (key) {
				case mSaveLanguageXMLKey:
				    mLanguage_XML_Filename = data;
				    break;
				case mSaveDirKey:
				    mSaveGame_Directory = data;
				    break;
				case mSaveFnKey:
				    mSaveGame_Filename = data;
				    break;
				case mSaveNumSavesKey:
				    mNumberSaves = Integer.parseInt(data);
				    break;
				case mSaveStartupKey:
				    mStartonStartup = data.equalsIgnoreCase("true");
				    break;
				case mSaveSaviorKey:
				    mRunSavior = data.equalsIgnoreCase("true");
				    break;
				case mSavePCSyncKey:
				    mRunPCSync = data.equalsIgnoreCase("true");
				    break;
				case mSaveSPMKey:
				    mRunSPM = data.equalsIgnoreCase("true");
				    break;
				case mSavePortKey:
				    mPortNumber = Integer.parseInt(data);
				    break;
				case mSaveQuicksavesOption:
				    mSPM_Do_Quicksaves = data.equalsIgnoreCase("true");
				    break;
				case mSaveFullsavesOption:
				    mSPM_Do_Fullsaves = data.equalsIgnoreCase("true");
				    break;
				case mSaveOthersavesOption:
				    mSPM_Do_Othersaves = data.equalsIgnoreCase("true");
				    break;
				case mSaveOthersavesFilename:
				    mSPM_Othersave_Filename = data;
				    break;
			    }
			}
		    }
		} while (strIn != null);
	    } finally {
		if (in != null) {
		    in.close();
		}
	    }
	}
    }

    public void saveUserData() throws IOException {
	if (!mDataChanged) {
	    return;
	}
	mDataChanged = false;
	File userFile = new File(this.sUserFilename);
	PrintWriter pr = null;
	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter(userFile));
	    pr = new PrintWriter(out);
	    if (mLanguage_XML_Filename != null) {
		pr.println(mSaveLanguageXMLKey + mLanguage_XML_Filename);
	    }
	    if (mSaveGame_Directory != null) {
		pr.println(mSaveDirKey + mSaveGame_Directory);
	    }
	    if (mSaveGame_Filename != null) {
		pr.println(mSaveFnKey + mSaveGame_Filename);
	    }
	    pr.println(mSaveNumSavesKey + mNumberSaves);
	    pr.println(mSaveStartupKey + mStartonStartup);
	    pr.println(mSaveSaviorKey + mRunSavior);
	    pr.println(mSavePCSyncKey + mRunPCSync);
	    pr.println(mSaveSPMKey + mRunSPM);
	    pr.println(mSaveQuicksavesOption + mSPM_Do_Quicksaves);
	    pr.println(mSaveFullsavesOption + mSPM_Do_Fullsaves);
	    pr.println(mSaveOthersavesOption + mSPM_Do_Othersaves);
	    
	    if (mSPM_Othersave_Filename != null && mSPM_Othersave_Filename.length() > 0) {
		pr.println(mSaveOthersavesFilename + mSPM_Othersave_Filename);
	    }
	    pr.println(mSavePortKey + mPortNumber);
	} finally {
	    if (pr != null) {
		pr.close();
	    }
	}
    }

}
