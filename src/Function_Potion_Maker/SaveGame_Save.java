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

import DirectoryWatcher.DirectoryWatcher;
import DirectoryWatcher.DirectoryWatcher.DWCallbacks;
import Main.UserData;
import SaveGame_Parser.INGR;
import SaveGame_Parser.TESVSaveFile;
import SaveGame_Parser.TESVSaveFileParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import spm_library.SPM_CONSTANTS;
import spm_library.SPM_XML_ELEMENTS;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class SaveGame_Save implements DWCallbacks {

    public static final String sXML_FILE_PATH = "spm_output";
    public static final String sXML_ZIPFILE_NAME = "spm_out.xml";
    public static final String sXML_ZIPFILE_NAME_SENT = "spm_out_sent.xml";
    
    public static final ArrayList<String> sLock_Object = new ArrayList<>();

    public SaveGame_Save() {

    }

    @Override
    public String onEventFileCreated(String filename) {
	System.out.println("RunSMP = " + UserData.get().isRunSPM() + "  fn = " + filename);
	if (UserData.get().isRunSPM() && filename.endsWith(DirectoryWatcher.sSaveExt_ess)) {
	    // Full save option
	    if (filename.toLowerCase().startsWith(DirectoryWatcher.sFullSavePrefix)) {
		if (UserData.get().isSPM_Do_Fullsaves()) {
		    readSaveGame(filename);
		}
		// Quicksave option
	    } else if (filename.equals(DirectoryWatcher.sQuickSaveNameFull)
		    || filename.startsWith(DirectoryWatcher.sSaviorSaveName)) {
		if (UserData.get().isSPM_Do_Quicksaves()) {
		    readSaveGame(filename);
		}
	    } else if (UserData.get().isSPM_Do_Othersaves()) {
		if (filename.toLowerCase().startsWith(UserData.get().getSPM_Othersave_Filename().toLowerCase())) {
		    readSaveGame(filename);
		}
	    }
	}
	return null;
    }
    
    @Override
    public void onEventFileDeleted(String filename) {
	
    }

    private boolean readSaveGame(String filename) {
	File sg = new File(UserData.get().getSaveGame_Directory(), filename);
	try {
	    TESVSaveFile s = TESVSaveFileParser.parse(sg.getAbsolutePath());
	    TreeMap<Integer, String> mods = getModsList(s);
	    //testPrint(s);
	    writeToXML(s, mods);
	    return true;
	} catch (FileNotFoundException fnf) {
	    Logger.getLogger(SaveGame_Save.class.getName()).log(Level.SEVERE, null, fnf);
	    return false;
	} catch (IOException | DataFormatException | XMLStreamException ex) {
	    Logger.getLogger(SaveGame_Save.class.getName()).log(Level.SEVERE, null, ex);
	    return false;
	}
    }

    private TreeMap<Integer, String> getModsList(TESVSaveFile sFile) {
	TreeMap<Integer, String> map = new TreeMap<>();
	for (INGR i : sFile.getIngredients()) {
	    if (!map.containsKey(i.getFormID().pos())) {
		map.put(i.getFormID().pos(), sFile.modsList.get(i.getFormID().pos()));
	    }
	}
	return map;
    }

    private static final String xmlVersion = SPM_CONSTANTS.XML_VERSION;
    private static final String encoding = SPM_CONSTANTS.XML_ENCODING;
    private static final String rootElement = SPM_XML_ELEMENTS.spm.name();
    private static final String playername = SPM_XML_ELEMENTS.playername.name();
    private static final String modsActiveElement = SPM_XML_ELEMENTS.modsactive.name();
    private static final String modElement = SPM_XML_ELEMENTS.mod.name();
    private static final String modPosElement = SPM_XML_ELEMENTS.mpos.name();
    private static final String modNameElement = SPM_XML_ELEMENTS.name.name();
    private static final String ingsElement = SPM_XML_ELEMENTS.ingredients.name();
    private static final String ingElement = SPM_XML_ELEMENTS.ingr.name();
    private static final String ingPosElement = SPM_XML_ELEMENTS.ing_mod_pos.name();
    private static final String ingIDElement = SPM_XML_ELEMENTS.ing_baseID.name();
    private static final String eff1Element = SPM_XML_ELEMENTS.ing_effect1.name();
    private static final String eff2Element = SPM_XML_ELEMENTS.ing_effect2.name();
    private static final String eff3Element = SPM_XML_ELEMENTS.ing_effect3.name();
    private static final String eff4Element = SPM_XML_ELEMENTS.ing_effect4.name();

    private final String sIn = "\n";
    //private final String sIn4 = "\n    ";
    //private final String sIn8 = "\n        ";
    //private final String sIn12 = "\n            ";

    private void writeToXML(TESVSaveFile sFile, TreeMap<Integer, String> modList) throws IOException, XMLStreamException {
	synchronized (sLock_Object) {
	    
	    File outPath = new File(sXML_FILE_PATH);
	    if (!outPath.exists()) {
		outPath.mkdir();
	    }
	    
	    outPath = new File(outPath, sXML_ZIPFILE_NAME);
	    
	    XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
	    XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new FileOutputStream(outPath), encoding);

	    try {

		writer.writeStartDocument(encoding, xmlVersion);
		writer.writeCharacters(sIn);
		writer.writeStartElement(rootElement);

		// Player name
		writer.writeStartElement(playername);
		writer.writeCharacters(sFile.playerName);
		writer.writeEndElement();

		// Active mods
		//writer.writeCharacters(sIn4);
		writer.writeStartElement(modsActiveElement);

		for (int mod : modList.keySet()) {
		    //writer.writeCharacters(sIn8);

		    writer.writeStartElement(modElement);
		    //writer.writeCharacters(sIn12);
		    writer.writeStartElement(modPosElement);
		    writer.writeCharacters(Integer.toHexString(mod));
		    writer.writeEndElement();

		    //writer.writeCharacters(sIn12);
		    writer.writeStartElement(modNameElement);
		    writer.writeCharacters(modList.get(mod));
		    writer.writeEndElement();

		    //writer.writeCharacters(sIn8);
		    writer.writeEndElement();
		}
		//writer.writeCharacters(sIn4);
		writer.writeEndElement();
		// Ingredients
		//writer.writeCharacters(sIn4);
		writer.writeStartElement(ingsElement);

		for (INGR i : sFile.getIngredients()) {
		    //writer.writeCharacters(sIn8);
		    writer.writeStartElement(ingElement);
		    //writer.writeCharacters(sIn12);

		    writer.writeStartElement(ingPosElement);
		    writer.writeCharacters(Integer.toHexString(i.getFormID().pos()));
		    writer.writeEndElement();
		    //writer.writeCharacters(sIn12);
		    writer.writeStartElement(ingIDElement);
		    writer.writeCharacters(Integer.toHexString(i.getFormID().id()));
		    writer.writeEndElement();

		    //writer.writeCharacters(sIn12);
		    writer.writeStartElement(eff1Element);
		    writer.writeCharacters(i.hasHasEffect1() ? SPM_CONSTANTS.XML_EFFECT_TRUE : SPM_CONSTANTS.XML_EFFECT_FALSE);
		    writer.writeEndElement();

		    //writer.writeCharacters(sIn12);
		    writer.writeStartElement(eff2Element);
		    writer.writeCharacters(i.hasHasEffect2() ? SPM_CONSTANTS.XML_EFFECT_TRUE : SPM_CONSTANTS.XML_EFFECT_FALSE);
		    writer.writeEndElement();

		    //writer.writeCharacters(sIn12);
		    writer.writeStartElement(eff3Element);
		    writer.writeCharacters(i.hasHasEffect3() ? SPM_CONSTANTS.XML_EFFECT_TRUE : SPM_CONSTANTS.XML_EFFECT_FALSE);
		    writer.writeEndElement();

		    //writer.writeCharacters(sIn12);
		    writer.writeStartElement(eff4Element);
		    writer.writeCharacters(i.hasHasEffect4() ? SPM_CONSTANTS.XML_EFFECT_TRUE : SPM_CONSTANTS.XML_EFFECT_FALSE);
		    writer.writeEndElement();

		    //writer.writeCharacters(sIn8);
		    writer.writeEndElement();

		}
		//writer.writeCharacters(sIn4);
		writer.writeEndElement(); // ingsElement
		//writer.writeCharacters(sIn);
		writer.writeEndElement(); // rootElement
		writer.writeEndDocument();
	    } finally {
		writer.flush();
		writer.close();
	    }
	}
    }

}
