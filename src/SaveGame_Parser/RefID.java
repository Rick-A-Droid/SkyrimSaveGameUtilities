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
public class RefID {
    public enum Type {
        INDEX, DEFAULT, CREATED, UNKNOWN
    }

    public final int val;
    public final Type type;

    public RefID(int ref) {
        this.val = ref & 0x3FFFFF;
        switch ((ref >>> 22) & 0b11) {
            case 0:
                this.type = Type.INDEX;
                break;
            case 1:
                this.type = Type.DEFAULT;
                break;
            case 2:
                this.type = Type.CREATED;
                break;
            default:
                this.type = Type.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder().append("RefID[type=").append(this.type.name()).append("; val=")
                .append(this.val).append(']').toString();
    }
}
