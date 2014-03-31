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

package Server;

import Debug.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import spm_library.NETWORK_MESSAGE;
import spm_library.ZIP_MSG_ENTRY;
import spm_zip.In_ZIP_Msg;
import spm_zip.NetworkMsgException;
import spm_zip.NetworkMsgException.NETWORK_EXCEPTION_CAUSE;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class In_ZIP_Message extends In_ZIP_Msg {

    protected File mExportedXMLFilePath = null;

    public In_ZIP_Message() {
	super();
    }

    public File getExportedXMLFilePath() {
	return mExportedXMLFilePath;
    }

    public void setExportedXMLFilePath(String path) {
	mExportedXMLFilePath = new File(path);
    }

    @Override
    protected void getXMLData(ZipInputStream zInput) throws NetworkMsgException,
	    IOException {

	BufferedReader inReader = new BufferedReader(new InputStreamReader(zInput));

	if (mExportedXMLFilePath == null) {
	    throw new NetworkMsgException(NetworkMsgException.NETWORK_EXCEPTION_CAUSE.EXPORT_PATH);
	}

	if (mFilename == null) {
	    throw new NetworkMsgException(NetworkMsgException.NETWORK_EXCEPTION_CAUSE.FILENAME);
	}

	if (!mExportedXMLFilePath.exists()) {
	    mExportedXMLFilePath.mkdir();
	}

	File file = new File(mExportedXMLFilePath, mFilename);

	Log.Msg("XML File Path: " + file.getAbsolutePath());
	Log.Msg("XML File Length to read: " + zInput.available());

	char[] buffer = new char[1024];
	int length;

	BufferedWriter bfr = new BufferedWriter(new FileWriter(file));

	try {
	    while (true) {
		length = inReader.read(buffer);
		if (length == -1) {
		    Log.Msg("XML File EOF");
		    break;
		}
		String s = new String(buffer, 0, length);
		bfr.write(s);
		Log.Msg("XML File read buffer: " + s);
	    }
	} finally {
	    bfr.flush();
	    bfr.close();
	}

    }

}
