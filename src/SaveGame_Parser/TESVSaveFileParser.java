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
package SaveGame_Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * @author trira
 * @version 1.0
 */
public class TESVSaveFileParser {
    private static final int INGR_TYPE = 16;
    private static final int CHANGE_INGREDIENT_USE = 1 << 31;

    public static TESVSaveFile parse(String path) throws IOException, DataFormatException {
	TESVSaveFile s = new TESVSaveFile(path);
	BinaryInputStream in = null;
	in.skip(13); // Skip magic number

	try {
	    in = new BinaryInputStream(path);
	    in.skip(13); // Skip magic number
	    int lngth = setPlayerName(s, in);  // set Player name
	    skipHeader(lngth, in);
	    in.skip(5); // Skip form version and plugin list size
	    parsePluginList(s, in);
	    parseFileLocationTable(s, in);
	    in.skip(s.getCfTableOffset() - in.pos()); // Skip to start of ChangeForms table
	    Map<RefID, INGR> ingrs = parseChangeForms(s, in);
	    in.skip(s.getFidTableOffset() - in.pos()); // Skip to start of FormID table
	    parseFormIDTable(s, in);
	    // Add ingredients to save file (needs to be done here since FormID table is read after ChangeForm table.
	    for (RefID r : ingrs.keySet()) {
		INGR i = ingrs.get(r);
		FormID f = s.getFormID(r);
		i.setFormID(f);
		s.addIngredient(i);
	    }
	} finally {
	    in.close();
	}
	return s;
    }

    private static int setPlayerName(TESVSaveFile s, BinaryInputStream in) throws IOException {
	int headerSize = in.nextInt();

	int nextInt = in.nextInt();
	int nextInt2 = in.nextInt();

	s.playerName = in.nextString();

	return headerSize - (10 + s.playerName.length() + 8);
    }

    private static void skipHeader(int lngth, BinaryInputStream in) throws IOException {
	// Skip everything up until screenshot width	
	in.skip(lngth);
	// Get width and height
	int w = in.nextInt();
	int h = in.nextInt();
	// Skip screenshot
	in.skip(3 * w * h);
    }

    private static void parsePluginList(TESVSaveFile s, BinaryInputStream in) throws IOException {
	int count = in.nextByte();
	for (int i = 0; i < count; ++i) {
	    // Get next plugin and check if it's a DLC plugin
	    String tmp = in.nextString().toLowerCase();
	    switch (tmp) {
		case "dawnguard.esm":
		    s.setHasDawnguardDLC();
		    break;
		case "dragonborn.esm":
		    s.setHasDragonbornDLC();
		    break;
		case "hearthfires.esm":
		    s.setHasHearthfireDLC();
		    break;
	    }

	    s.modsList.add(tmp);
	}
    }

    private static void parseFileLocationTable(TESVSaveFile s, BinaryInputStream in)
	    throws IOException {
	s.setFidTableOffset(in.nextInt());
	in.skip(12);
	s.setCfTableOffset(in.nextInt());
	in.skip(16);
	s.setCfTableCount(in.nextInt());
	in.skip(15);
    }

    private static Map<RefID, INGR> parseChangeForms(TESVSaveFile s, BinaryInputStream in)
	    throws IOException, DataFormatException {
	Map<RefID, INGR> ingrs = new HashMap<>();
	for (int i = 0; i < s.getCfTableCount(); ++i) {
	    RefID ref = in.nextRefID();
	    int flags = in.nextInt();
	    // Get type and width of length vars
	    int type = in.nextByte();
	    int lenWidth = type >>> 6;
	    type = type & 0b00111111;
	    // Skip form version
	    in.skip(1);
	    // Get length vars
	    int len1 = 0;
	    int len2 = 0;
	    switch (lenWidth) {
		case 0:
		    len1 = in.nextByte();
		    len2 = in.nextByte();
		    break;
		case 1:
		    len1 = in.nextShort();
		    len2 = in.nextShort();
		    break;
		case 2:
		    len1 = in.nextInt();
		    len2 = in.nextInt();
		    break;
	    }
			// If ChangeForm is an ingredient and only the CHANGE_INGREDIENT_USE flag is set (otherwise data format is
	    // unknown)
	    if (type == INGR_TYPE && (flags & CHANGE_INGREDIENT_USE) == CHANGE_INGREDIENT_USE) {
		int effectsKnown = (len2 == 0) ? in.nextInt() : in.nextCompressedSegment(len1)[0];
		ingrs.put(ref, new INGR(0, effectsKnown));
	    } else {
		in.skip(len1);
	    }
	}
	return ingrs;
    }

    private static void parseFormIDTable(TESVSaveFile s, BinaryInputStream in) throws IOException {
	int count = in.nextInt();
	for (int i = 0; i < count; ++i) {
	    s.addForm(new FormID(in.nextInt()));
	}
    }

}
