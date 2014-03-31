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

public class FormID {
    public final int id;

    public FormID(int id) {
        this.id = id;
    }

    public FormID(int pos, int id) {
        this.id = (pos << 24) | (id & 0xFFFFFF);
    }

    /**
     * @return The load order position of this form's plugin.
     */
    public int pos() {
        return this.id >>> 24;
    }

    /**
     * @return The unique identifier of this form.
     */
    public int id() {
        return this.id & 0xFFFFFF;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("pos: ").append(this.pos()).append("; id: ")
                .append(Integer.toHexString(this.id())).toString();
    }
}
