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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author trira
 * @version 1.0
 */
public class TESVSaveFile {
    private String path;

    private boolean hasDawnguardDLC;
    private boolean hasHearthfireDLC;
    private boolean hasDragonbornDLC;

    private long changeFormTableOffset;
    private int changeFormTableCount;
    private long formIDTableOffset;

    private Map<FormID, INGR> ingredients;
    private List<FormID> formIDTable;
    
    // Rick's additions
    public ArrayList<String> modsList = new ArrayList<>();
    public String playerName;

    public TESVSaveFile(String path) {
        this.path = path;
        this.ingredients = new HashMap<>();
        this.formIDTable = new ArrayList<>();
    }

    public void addForm(FormID f) {
        this.formIDTable.add(f);
    }

    public void addIngredient(INGR i) {
        this.ingredients.put(i.getFormID(), i);
    }

    /*
     * Getters and Setters
     */
    public String getPath() {
        return this.path;
    }

    public final boolean hasDawnguardDLC() {
        return this.hasDawnguardDLC;
    }

    public final boolean hasHearthfireDLC() {
        return this.hasHearthfireDLC;
    }

    public final boolean hasDragonbornDLC() {
        return this.hasDragonbornDLC;
    }

    public Collection<INGR> getIngredients() {
        return this.ingredients.values();
    }

    public INGR getIngredient(FormID fid) {
        return this.ingredients.get(fid);
    }

    public FormID getFormID(int index) {
        return this.formIDTable.get(index);
    }

    public FormID getFormID(RefID ref) {
        switch (ref.type) {
            case INDEX:
                return this.formIDTable.get((ref.val == 0) ? 0 : ref.val - 1);
            case DEFAULT:
                return new FormID(0, ref.val);
            case CREATED:
                return new FormID(0xFF, ref.val);
            default:
                return null;
        }
    }

    public final long getCfTableOffset() {
        return this.changeFormTableOffset;
    }

    public final int getCfTableCount() {
        return this.changeFormTableCount;
    }

    public final long getFidTableOffset() {
        return this.formIDTableOffset;
    }

    public int getFidTableCount() {
        return this.formIDTable.size();
    }

    public final void setHasDawnguardDLC() {
        this.hasDawnguardDLC = true;
    }

    public final void setHasHearthfireDLC() {
        this.hasHearthfireDLC = true;
    }

    public final void setHasDragonbornDLC() {
        this.hasDragonbornDLC = true;
    }

    public final void setCfTableOffset(long cfTableOffset) {
        this.changeFormTableOffset = cfTableOffset;
    }

    public final void setCfTableCount(int cfTableCount) {
        this.changeFormTableCount = cfTableCount;
    }

    public final void setFidTableOffset(long fidTableOffset) {
        this.formIDTableOffset = fidTableOffset;
    }
}
