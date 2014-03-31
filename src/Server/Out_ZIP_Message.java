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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import spm_library.NETWORK_MESSAGE;
import spm_zip.Out_ZIP_Msg;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class Out_ZIP_Message extends Out_ZIP_Msg {

    protected File mXMLFile = null;

    public Out_ZIP_Message() {
	super();
    }

    public Out_ZIP_Message(NETWORK_MESSAGE msg) {
	super(msg);
    }    
    public void setXMLFile(String path, String filename) {
	mXMLFile = new File(path, filename);
    }
    
    @Override
    protected BufferedReader getInReader() throws IOException {
	return new BufferedReader(new FileReader(mXMLFile));
    }

}
