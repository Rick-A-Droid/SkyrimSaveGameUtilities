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
package Debug;

/**
 *
 * @author Rick Falck
 * @version 1.0
 */
public class Log {

    public static void Msg(String msg, Exception e) {
	if (msg != null) {
	    System.out.println(msg);
	}

	if (e != null) {
	    e.printStackTrace();
	}
    }

    public static void Msg(String msg) {
	System.out.println(msg);
    }

}
