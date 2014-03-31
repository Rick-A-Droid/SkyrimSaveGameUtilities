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

package Function_Savegame_Savior;

import Debug.Log;
import DirectoryWatcher.DirectoryWatcher;
import DirectoryWatcher.DirectoryWatcher.DWCallbacks;
import Language.LanguageResources;
import Main.FilenameUtils;
import Main.UserData;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;
import javafx.application.Platform;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class SaveGameSavior implements DWCallbacks {

    public interface UICallback {
	void onSaviorEvent(String eventText);

    }

    private final String mDateSeparator = "-";
    private final int mNumToSave;
    private final TreeMap<Long, String> mSavedFilesMap;

    private boolean mRunning = false;
    private long mLastFileDeleted = -1;
    private final UICallback mUICallback;

    public SaveGameSavior(UICallback uicb) {
	mUICallback = uicb;
	mNumToSave = UserData.get().getNumberSaves();
	mSavedFilesMap = new TreeMap<>();
    }

    public void start() {
	if (mRunning == false) {
	    mRunning = true;
	    setupCurrentFiles();
	}
    }

    public void stop() {
	mRunning = false;
    }

    public void setupCurrentFiles() {
	File dir = new File(UserData.get().getSaveGame_Directory());

	String[] files = dir.list(new SaveFilenameFilter());

	if (files != null && files.length > 0) {
	    for (String fn : files) {

		if (fn.startsWith(DirectoryWatcher.sSaviorSaveName)) {
		    long ms = getMilliseconds(fn);

		    if (ms != -1) {
			mSavedFilesMap.put(ms, FilenameUtils.getBaseName(fn));
		    }
		}
	    }
	}

	if (mSavedFilesMap.size() > mNumToSave) {
	    for (int i = mSavedFilesMap.size() - mNumToSave; i >= 0; i--) {
		deleteFirstSave();
	    }
	}
    }

    /**
     * <h3>public void onEventFileCreated(String filename)</h3>
     * <p>
     * This method is called by the DirectoryWatcher on a daemon thread.
     *
     * @param filename
     */
    @Override
    public String onEventFileCreated(String filename) {
	if (UserData.get().isRunSavior() && filename.startsWith(DirectoryWatcher.sQuickSaveName)) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {

	    }

	    return renameQuickSave(filename);

	} else {
	    return filename;
	}
    }

    @Override
    public void onEventFileDeleted(String filename) {
	//Log.Msg("onEventFileDeleted() called for " + filename);

	if (filename.startsWith(DirectoryWatcher.sSaviorSaveName)) {
	    long ms = getMilliseconds(filename);

	    if (ms != -1 && ms != mLastFileDeleted) {   // don't attempt to delete files we deleted
		String old = mSavedFilesMap.remove(ms);
		if (old == null) {
		    Platform.runLater(new UpdateMainThread(LanguageResources.get("main_savior_update_delete_failed") + filename));
		    Log.Msg("onEventFileDeleted() failed");
		} else {
		    Platform.runLater(new UpdateMainThread(LanguageResources.get("main_savior_update_delete_ok") + filename));
		    Log.Msg("onEventFileDeleted() deleted " + old);
		}

	    }
	}
    }

    private long getMilliseconds(String fn) {
	String datePart = FilenameUtils.getBaseName(fn)
		.substring(fn.indexOf("-") + 1);

	//Log.Msg("Date part = " + datePart);
	try {
	    //gamesave_Sun Apr 15 2014 10-10-10
	    String[] elems = datePart.split(" ");
	    String[] hms = elems[4].split(mDateSeparator);

	    int year = Integer.parseInt(elems[3]);

	    int month = LanguageResources.getMonthIndex(elems[1]);
	    if (month == -1) {
		return -1;
	    }

	    int day = Integer.parseInt(elems[2]);

	    int hour = Integer.parseInt(hms[0]);
	    int min = Integer.parseInt(hms[1]);
	    int sec = Integer.parseInt(hms[2]);

	    GregorianCalendar gcalendar = new GregorianCalendar(year,
		    month, day, hour, min, sec);

	    return gcalendar.getTimeInMillis();
	} catch (ArrayIndexOutOfBoundsException | NumberFormatException ne) {
	    Log.Msg("Date part cannot be converted", ne);
	    return -1;
	}
    }

    private String renameQuickSave(String filename) {
	File curr = new File(UserData.get().getSaveGame_Directory(), filename);

	GregorianCalendar gcalendar = new GregorianCalendar();

	//Log.Msg("DATE: " + gcalendar.get(Calendar.DAY_OF_WEEK) + " " + gcalendar.get(Calendar.MONTH) + " "
	//	+ gcalendar.get(Calendar.DAY_OF_MONTH));
	//Sun Apr 15 2014 10-10-10
	StringBuilder fnBuilder = new StringBuilder();
	fnBuilder.append(DirectoryWatcher.sSaviorSaveName);

	fnBuilder.append(" -");
	fnBuilder.append(LanguageResources.getDay(gcalendar.get(Calendar.DAY_OF_WEEK) - 1));
	fnBuilder.append(" ");
	fnBuilder.append(LanguageResources.getMonth(gcalendar.get(Calendar.MONTH)));
	fnBuilder.append(" ");
	fnBuilder.append(String.valueOf(gcalendar.get(Calendar.DAY_OF_MONTH)));
	fnBuilder.append(" ");
	fnBuilder.append(String.valueOf(gcalendar.get(Calendar.YEAR)));
	fnBuilder.append(" ");
	fnBuilder.append(String.valueOf(gcalendar.get(Calendar.HOUR_OF_DAY)));
	fnBuilder.append(mDateSeparator);
	fnBuilder.append(String.valueOf(gcalendar.get(Calendar.MINUTE)));
	fnBuilder.append(mDateSeparator);
	fnBuilder.append(String.valueOf(gcalendar.get(Calendar.SECOND)));

	//Log.Msg("SaveGameSavior renameQuickSave " + fnBuilder.toString(), null);
	File next = new File(UserData.get().getSaveGame_Directory(), fnBuilder.toString()
		+ DirectoryWatcher.sDot + DirectoryWatcher.sSaveExt_ess);

	mSavedFilesMap.put(gcalendar.getTimeInMillis(), fnBuilder.toString());

	curr.renameTo(next);

	// Attempt to rename the .skse file if it exists
	String currentSKSE = FilenameUtils.getBaseName(filename) + DirectoryWatcher.sDot
		+ DirectoryWatcher.sSaveExt_skse;

	curr = new File(UserData.get().getSaveGame_Directory(), currentSKSE);
	if (curr.exists()) {
	    next = new File(UserData.get().getSaveGame_Directory(), fnBuilder.toString()
		    + DirectoryWatcher.sDot + DirectoryWatcher.sSaveExt_skse);
	    curr.renameTo(next);
	}
	if (mSavedFilesMap.size() > mNumToSave) {
	    deleteFirstSave();
	}

	fnBuilder.append(DirectoryWatcher.sDot);
	fnBuilder.append(DirectoryWatcher.sSaveExt_ess);
	Platform.runLater(new UpdateMainThread(LanguageResources.get("main_savior_update_rename_ok") + fnBuilder.toString()));

	return fnBuilder.toString();
    }

    private void deleteFirstSave() {
	long k = mSavedFilesMap.firstKey();
	String fn = mSavedFilesMap.get(k);
	Log.Msg("Attempting to Delete save: " + fn);
	File essFile = new File(UserData.get().getSaveGame_Directory(), fn + DirectoryWatcher.sDot
		+ DirectoryWatcher.sSaveExt_ess);
	if (essFile.delete()) {
	    mLastFileDeleted = k;
	    File skseFile = new File(UserData.get().getSaveGame_Directory(), fn + DirectoryWatcher.sDot
		    + DirectoryWatcher.sSaveExt_skse);
	    if (skseFile.exists()) {
		skseFile.delete();
	    }
	}

	mSavedFilesMap.remove(k);
	Log.Msg("Deleted save: " + fn);
    }

    private class SaveFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
	    return (name.startsWith(DirectoryWatcher.sSaviorSaveName)
		    && name.endsWith(DirectoryWatcher.sSaveExt_ess));
	}

    }

    private class UpdateMainThread implements Runnable {

	private final String mEventText;
	public UpdateMainThread(String event) {
	    mEventText = event;
	}

	@Override
	public void run() {
	    mUICallback.onSaviorEvent(mEventText);
	}

    }

}
