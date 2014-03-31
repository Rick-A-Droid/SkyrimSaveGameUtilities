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
/**
 * @author trira
 * @version 1.0
 */
public class INGR {
    private FormID form;
    private int effectsKnown;

    public INGR(int form, int effectsKnown) {
        this.form = new FormID(form);
        this.effectsKnown = effectsKnown;
    }

    public boolean hasHasEffect1() {
        return (this.effectsKnown & 0b0001) != 0;
    }

    public boolean hasHasEffect2() {
        return (this.effectsKnown & 0b0010) != 0;
    }

    public boolean hasHasEffect3() {
        return (this.effectsKnown & 0b0100) != 0;
    }

    public boolean hasHasEffect4() {
        return (this.effectsKnown & 0b1000) != 0;
    }

    public FormID getFormID() {
        return this.form;
    }

    public void setFormID(FormID form) {
        this.form = form;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("INGR[fid=[").append(this.form)
                .append("]; known effects=").append(this.effectsKnown).append(']')
                .toString();
    }
}
