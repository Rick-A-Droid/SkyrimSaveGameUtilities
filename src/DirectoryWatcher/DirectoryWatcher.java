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

package DirectoryWatcher;

import Debug.Log;
import Language.LanguageResources;
import Main.FilenameUtils;
import Main.UserData;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 *
 * @author Rick Falck
 * @version 1.0
 */
public class DirectoryWatcher implements Runnable {

    public interface DWCallbacks {
	/**
	 * Method invoked when a new file is created in the savegame directory
	 *
	 * @param filepath - The Filename only (no path)
	 * @return String - The new filename
	 */
	String onEventFileCreated(String filepath);

	void onEventFileDeleted(String filename);

    }

    /**
     * Filename of the quicksave file without extension
     */
    public static String sQuickSaveName;
    public static String sQuickSaveNameFull;
    public static String sSaviorSaveName;
    public static String sFullSavePrefix;
    public static String sSaveExt_ess;
    public static String sSaveExt_skse;
    public static String sDot = FilenameUtils.EXTENSION_SEPARATOR_STR;

    private WatchService mWatcher = null;
    private Path mPath;
    private WatchKey mKey;

    private final DWCallbacks mSaviorCallbacks;
    private final DWCallbacks mPotionMakerCallbacks;

    public DirectoryWatcher(DWCallbacks savior, DWCallbacks pm) {
	sQuickSaveName = LanguageResources.get("quicksave_filename");
	sSaviorSaveName = LanguageResources.get("savior_filename_prefix");
	sFullSavePrefix = LanguageResources.get("fullsave_filename_prefix");
	sSaveExt_ess = LanguageResources.get("filename_extension_ess");
	sSaveExt_skse = LanguageResources.get("filename_extension_skse");

	sQuickSaveNameFull = sQuickSaveName + sDot + sSaveExt_ess;

	mSaviorCallbacks = savior;
	mPotionMakerCallbacks = pm;
    }

    public void setPath(String path) {
	mPath = Paths.get(path);
    }

    public void start() throws IOException {
	if (mWatcher == null) {
	    mWatcher = FileSystems.getDefault().newWatchService();
	    mPath = Paths.get(UserData.get().getSaveGame_Directory());
	    mKey = mPath.register(mWatcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

	    new Thread(this, "DirectoryWatcher").start();
	    Log.Msg("DirectoryWatcher Started", null);
	}
    }

    public void stop() {
	if (mWatcher != null) {
	    try {
		mWatcher.close();
	    } catch (IOException e) {

	    } finally {
		mWatcher = null;
	    }
	}
    }

    public boolean isRunning() {
	return mWatcher != null;
    }

    @Override
    public void run() {
	//System.out.println("Thread started");
	while (true) {

	    // wait for key to be signalled
	    WatchKey key;
	    try {
		key = mWatcher.take();
	    } catch (ClosedWatchServiceException | InterruptedException ie) {
		Log.Msg("DirectoryWatcher ClosedWatchServiceException", null);
		return;
	    }

	    for (WatchEvent<?> event : key.pollEvents()) {
		WatchEvent.Kind kind = event.kind();

		// TBD - provide example of how OVERFLOW event is handled
		if (kind == StandardWatchEventKinds.OVERFLOW) {
		    continue;
		}

		String filename = event.context().toString();

		if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
		    Log.Msg("DirectoryWatcher file " + filename);

		    if (filename.endsWith(sSaveExt_ess)) {

			filename = mSaviorCallbacks.onEventFileCreated(filename);

			mPotionMakerCallbacks.onEventFileCreated(filename);
		    }

		} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
		    if (filename.startsWith(sSaviorSaveName) && filename.endsWith(sSaveExt_ess)) {
			mSaviorCallbacks.onEventFileDeleted(filename);
		    }
		}
	    }

	    // reset key and remove from set if directory no longer accessible
	    boolean valid = key.reset();
	    if (!valid) {
		Log.Msg("DirectoryWatcher !valid", null);
		break;
	    }
	}
	Log.Msg("DirectoryWatcher Thread stopped", null);
    }

    public enum EVENT_TYPE {

	CREATE,
	MODIFY,
	DELETE
    }
}
