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

import DirectoryWatcher.DirectoryWatcher;
import Function_Potion_Maker.SaveGame_Save;
import Function_Potion_Maker.SaveGame_Sync;
import Function_Savegame_Savior.SaveGameSavior;
import Function_XML_Import_Export.XML_Handler;
import Server.SPMSocketService;
import java.io.IOException;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class FunctionMaster {
    private final DirectoryWatcher mDirWatcher;
    private final SPMSocketService mSocketService;

    private final XML_Handler mXMLHandler;
    private final SaveGame_Save mPlayerDirectoryWatcher;
    private final SaveGame_Sync mPlayerSync;
    private final SaveGameSavior mSavior;

    public FunctionMaster(SaveGameSavior.UICallback uicb, SPMSocketService.UICallbacks uiCbs) {

	mXMLHandler = new XML_Handler();
	mPlayerDirectoryWatcher = new SaveGame_Save();
	mPlayerSync = new SaveGame_Sync();
	mSavior = new SaveGameSavior(uicb);

	mDirWatcher = new DirectoryWatcher(mSavior, mPlayerDirectoryWatcher);

	mSocketService = new SPMSocketService(uiCbs, mPlayerSync, mXMLHandler);

    }

    public void startDirectoryWatcher() throws IOException {
	if (UserData.get().getSaveGame_Directory() != null) {
	    if (!mDirWatcher.isRunning()) {
		if (UserData.get().isRunSavior()) {
		    mSavior.start();
		}

		if (UserData.get().isRunSPM() || UserData.get().isRunSavior()) {
		    mDirWatcher.start();
		}
	    }
	}
    }

    public void stopDirectoryWatcher() {
	if (mDirWatcher.isRunning()) {
	    if (!UserData.get().isRunSavior()) {
		mSavior.stop();
	    }
	    
	    if (!UserData.get().isRunSPM() && !UserData.get().isRunSavior()) {
		mDirWatcher.stop();
	    }
	}
    }

    public void startSocketService() {
	if (!mSocketService.isRunning()) {
	    if (UserData.get().isRunSPM() || UserData.get().isRunPCSync()) {
		mSocketService.start();
	    }
	}
    }

    public void stopSocketService() {
	if (mSocketService.isRunning()) {
	    if (!UserData.get().isRunSPM() && !UserData.get().isRunPCSync()) {
		mSocketService.stop();
	    }
	}
    }

    public void stopAll() {
	mDirWatcher.stop();
	mSocketService.stop();
    }

}
