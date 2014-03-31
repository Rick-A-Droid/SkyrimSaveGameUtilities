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

package Function_XML_Import_Export;

import Language.LanguageResources;
import Main.ReturnInfo;
import Server.In_ZIP_Message;
import Server.SPMSocketService;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import spm_library.NETWORK_MESSAGE;
import static spm_library.NETWORK_MESSAGE.REQUEST_EFFECTS_DATA;
import static spm_library.NETWORK_MESSAGE.REQUEST_INGREDIENTS_DATA;
import static spm_library.NETWORK_MESSAGE.REQUEST_WITH_PCSYNC_XML_DATA;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class XML_Handler implements SPMSocketService.XML_File_Callback {
    public static final String sXML_DIR = "XML_TRANSACTIONS";
    private static final String sXML_IN_FILE_PATH = sXML_DIR + File.separator + "XML_in";
    private static final String sXML_EFFECTS_OUT_FILE_PATH = sXML_DIR + File.separator + "XML_Out_Effects";
    private static final String sXML_INGS_OUT_FILE_PATH = sXML_DIR + File.separator + "XML_Out_Ingredients";

    /**
     * <h3>public XML_Handler() throws RuntimeException</h3>
     * <b>Constructor</b><p>
     * Creates the directories needed for importing and exporting XML files, if they don't
     * exist.
     * @throws RuntimeException
     * 
     * <h3>Possible RuntimeExceptions</h3>
     * <ul>
     * <li>InvalidPathException
     * <li>SecurityException
     * </ul>
     */
    public XML_Handler() throws RuntimeException {
	File dir;

	dir = Paths.get(sXML_IN_FILE_PATH).toFile();

	if (!dir.exists()) {
	    dir.mkdirs();
	}

	dir = Paths.get(sXML_EFFECTS_OUT_FILE_PATH).toFile();

	if (!dir.exists()) {
	    dir.mkdirs();
	}

	dir = Paths.get(sXML_INGS_OUT_FILE_PATH).toFile();

	if (!dir.exists()) {
	    dir.mkdirs();
	}
    }

    @Override
    public void setXMLExportFilePath(In_ZIP_Message inMsg) {
	inMsg.setExportedXMLFilePath(sXML_IN_FILE_PATH);
    }

    @Override
    public ReturnInfo getResponse(In_ZIP_Message inMsg) {
	Path path;
	String[] files;
	ReturnInfo result = new ReturnInfo();

	try {

	    switch (inMsg.getNetworkMessage()) {
		case REQUEST_WITH_PCSYNC_XML_DATA:
		    result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_PCSYNC_XML_RECEIVED);
		    result.setMessage(LanguageResources.get("network_info_xml_export_file_saved"));
		    break;
		case REQUEST_INGREDIENTS_DATA:
		    path = Paths.get(sXML_INGS_OUT_FILE_PATH);
		    files = path.toFile().list(new XMLFilenameFilter());

		    if (files == null || files.length == 0) {
			result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_NO_DATA);
			result.setMessage(LanguageResources.get("network_info_xml_filenotfound"));
		    } else {
			result.mOutZipMessage.setFilename(files[0]);
			result.mOutZipMessage.setXMLFile(path.toString(), files[0]);
			result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_WITH_INGREDIENTS_DATA);
			result.setMessage(LanguageResources.get("network_info_xml_ings_sent"));
		    }
		    break;
		case REQUEST_EFFECTS_DATA:
		    path = Paths.get(sXML_EFFECTS_OUT_FILE_PATH);
		    files = path.toFile().list(new XMLFilenameFilter());

		    if (files == null || files.length == 0) {
			result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_NO_DATA);
			result.setMessage(LanguageResources.get("network_info_xml_filenotfound"));
		    } else {
			result.mOutZipMessage.setFilename(files[0]);
			result.mOutZipMessage.setXMLFile(path.toString(), files[0]);
			result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_WITH_EFFECTS_DATA);
			result.setMessage(LanguageResources.get("network_info_xml_effects_sent"));
		    }
		    break;
	    }

	} catch (Exception e) {
	    result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_SERVER_ERROR);
	    result.setMessage("Other Exception: " + e.toString());
	}
	return result;
    }

    private class XMLFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
	    return (name.toLowerCase().endsWith(".xml"));
	}

    }

}
