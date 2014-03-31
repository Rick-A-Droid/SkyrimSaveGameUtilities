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

import Language.LanguageResources;
import spm_zip.NetworkMsgException;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class NW_EXC {
    
    public static String getErrorText(NetworkMsgException exc) {
	StringBuilder sb = new StringBuilder();
	if (exc.isIOException()) {
	    sb.append(LanguageResources.get("network_error_ioexception"));
	} else {
	    sb.append(LanguageResources.get("network_error_processing"));
	}
	
	switch (exc.getError()) {
	    case NETWORK_MESSAGE:
		sb.append(LanguageResources.get("network_error_nwmessage"));
		break;
	    case FILENAME:
		sb.append(LanguageResources.get("network_error_filename"));
		break;
	    case EXPORT_PATH:
		sb.append(LanguageResources.get("network_error_export_path"));
		break;
	    case XML_STREAM:
		sb.append(LanguageResources.get("network_error_zip_stream"));
		break;
	    case ZIP_WRITE:
		sb.append(LanguageResources.get("network_error_zip_write"));
		break;
	    case ZIP_READ:
		sb.append(LanguageResources.get("network_error_zip_read"));
		break;
	    case ZIP_ENTRY:
		sb.append(LanguageResources.get("network_error_zip_entry"));
		break;
	    case XML_FILE:
		sb.append(LanguageResources.get("network_error_import_xmlfile"));
		break;
	    case ZIP_STREAM:
		sb.append(LanguageResources.get("network_error_zip_stream"));
		break;
	}
	
	return sb.toString();
    }
}
