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

package Function_Potion_Maker;

import Debug.Log;
import Language.LanguageResources;
import Main.ReturnInfo;
import Server.SPMSocketService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import spm_library.NETWORK_MESSAGE;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class SaveGame_Sync implements SPMSocketService.PotionMaker_Callback {

    public SaveGame_Sync() {
    }

    @Override
    public ReturnInfo getNextFile() {

	ReturnInfo result = new ReturnInfo();

	try {

	    // The zip file to read
	    File fIn = new File(SaveGame_Save.sXML_FILE_PATH, SaveGame_Save.sXML_ZIPFILE_NAME);

	    Log.Msg("SaveGame_Sync.getNextFile() " + fIn.getAbsolutePath());

	    if (!fIn.exists()) {
		Log.Msg("SaveGame_Sync.getNextFile() - File Not Found");
		result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_NO_DATA);
		result.setMessage(LanguageResources.get("network_info_playerdata_nodata_sent"));
	    } else {
		Log.Msg("SaveGame_Sync.getNextFile() - File Found, putting data in result.mOutZipMessage");
		result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_WITH_PLAYER_DATA);
		result.mOutZipMessage.setFilename(SaveGame_Save.sXML_ZIPFILE_NAME);
		result.mOutZipMessage.setXMLFile(SaveGame_Save.sXML_FILE_PATH, SaveGame_Save.sXML_ZIPFILE_NAME);
		result.setMessage(LanguageResources.get("network_info_playerdata_sent"));
	    }

	    File fSent = new File(SaveGame_Save.sXML_FILE_PATH, SaveGame_Save.sXML_ZIPFILE_NAME_SENT);
	    if (fSent.exists()) {
		fSent.delete();
	    }
	} catch (Exception e) {
	    result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_SERVER_ERROR);
	    result.setMessage("Other Exception: " + e.toString());
	}
	return result;
    }

    @Override
    public void onClientResponse(NETWORK_MESSAGE msg) {
	if (msg == NETWORK_MESSAGE.RESPONSE_PLAYER_DATA_RECEIVED_OK) {
	    File fSent = new File(SaveGame_Save.sXML_FILE_PATH, SaveGame_Save.sXML_ZIPFILE_NAME_SENT);
	    if (fSent.exists()) {
		fSent.delete();
	    }
	}
    }

}
