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

package Language;

import Main.FilenameUtils;
import Main.UserData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.TreeMap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Rick Falck
 * @version 1.0
 */
public final class LanguageResources {

    private static final TreeMap<String, String> mStringMap = new TreeMap<>();
    private static final String sLanguageFilenamePrefix = "Skyrim_Language";

    private static final String[] mMonths = new String[12];
    private static final String[] mDays = new String[7];
    
    private LanguageResources() { }

    public static void SetLanguage(String userFile) throws IOException, FileNotFoundException, XMLStreamException {
	File resDir = new File("Resources");

	if (resDir.exists()) {
	    if (userFile == null) {
		String[] stringFiles = resDir.list(new XMLFilter());
		if (stringFiles.length > 0) {
		    readFile(new File(resDir, stringFiles[0]));
		    UserData.get().setLanguage_XML_Filename(stringFiles[0]);
		} else {
		    throw new IOException("No language file found");
		}
	    } else {
		readFile(new File(resDir, userFile));
	    }
	} else {
	    System.out.println(resDir.getAbsoluteFile());
	    throw new IOException("Language file not found");
	}
    }

    public static void ReSetLanguage(Stage parentStage) throws IOException, FileNotFoundException, XMLStreamException {
	File resDir = new File("Resources");

	if (resDir.exists()) {
	    String[] stringFiles = resDir.list(new XMLFilter());
	    if (stringFiles.length > 1) {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.setTitle("Select Language File");
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter(
			"XML files (*.xml)", "*.xml");
		fc.getExtensionFilters().addAll(extentionFilter);
		File lfile = fc.showOpenDialog(parentStage.getScene().getWindow());

		if (lfile != null) {
		    readFile(lfile);
		    UserData.get().setLanguage_XML_Filename(FilenameUtils.getName(lfile.getName()));

		} else {
		    throw new IOException("No language file selected");
		}
	    } else {
		readFile(new File(resDir, stringFiles[0]));
	    }

	} else {
	    System.out.println(resDir.getAbsoluteFile());
	    throw new IOException("Language file not found");
	}
    }

    public static String get(String key) {
	String v = mStringMap.get(key);
	if (v == null) {
	    v = "ERROR";
	}
	return v;
    }

    private static void readFile(File xmlFile) throws FileNotFoundException, XMLStreamException {
	final String root = "strings";
	final String string = "string";
	final String key = "key";
	final String value = "value";
	final String string_array = "string-array";
	final String string_item = "item";
	// create the XMLInputFactory object
	XMLInputFactory inputFactory = XMLInputFactory.newFactory();

	// create a XMLStreamReader object
	FileInputStream fis = new FileInputStream(xmlFile);
	XMLStreamReader reader = inputFactory.createXMLStreamReader(fis);

	int stringArrayIndex = -1;

	String[] array = null;

	// read the Ingredients from the file
	while (reader.hasNext()) {
	    int eventType = reader.getEventType();
	    switch (eventType) {
		case XMLStreamConstants.START_ELEMENT:
		    String elementName = reader.getLocalName();
		    switch (elementName) {
			case root:
			    break;
			case string:
			    String akey = null;
			    String avalue = null;
			    if (reader.getAttributeLocalName(0).equals(key)) {
				akey = reader.getAttributeValue(0);
				if (reader.getAttributeLocalName(1).equals(value)) {
				    avalue = reader.getAttributeValue(1);
				} else {
				    throw new XMLStreamException("Invalid attribute Found");
				}
			    } else if (reader.getAttributeLocalName(1).equals(key)) {
				akey = reader.getAttributeValue(1);
				if (reader.getAttributeLocalName(0).equals(value)) {
				    avalue = reader.getAttributeValue(0);
				} else {
				    throw new XMLStreamException("Invalid attribute Found");
				}
			    }

			    if (akey == null || avalue == null) {
				throw new XMLStreamException("Invalid attribute Found");
			    }

			    mStringMap.put(akey, avalue);
			    break;
			case string_array:
			    stringArrayIndex = 0;
			    akey = reader.getAttributeValue(0);
			    if (akey.equals("months_of_year")) {
				array = mMonths;
			    } else if (akey.equals("days_of_week")) {
				array = mDays;
			    } else {
				throw new XMLStreamException("Invalid attribute Found");
			    }
			    break;
			case string_item:
			    if (array == null || stringArrayIndex == -1) {
				throw new XMLStreamException("no array for item");
			    }
			    akey = reader.getAttributeValue(0);
			    array[stringArrayIndex++] = akey;
			    break;
			default:
			    throw new XMLStreamException("Invalid Element Found");
		    }
		    break;
		case XMLStreamConstants.COMMENT:
		    break;
		case XMLStreamConstants.END_ELEMENT:
		    String endElementName = reader.getLocalName();
		    switch (endElementName) {
			case string_array:
			    stringArrayIndex = -1;
			    array = null;
			    break;
		    }
		default:
		    break;
	    }
	    reader.next();

	}
    }
    
    public static String getMonth(int month) {
	return mMonths[month];
    }
    
    public static int getMonthIndex(String month) {
	for (int index = 0; index < mMonths.length; index++) {
	    if (mMonths[index].equals(month)) {
		return index;
	    }
	}
	return -1;
    }
    
    public static int getDayIndex(String day) {
	for (int index = 0; index < mDays.length; index++) {
	    if (mDays[index].equals(day)) {
		return index;
	    }
	}
	return -1;
    }
    
    public static String getDay(int day) {
	return mDays[day];
    }

    private static class XMLFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
	    return (name.startsWith(sLanguageFilenamePrefix) && name.endsWith(".xml"));
	}

    }

}
