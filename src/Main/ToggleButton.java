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

package Main;

import Language.LanguageResources;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class ToggleButton extends Button {

    public interface ICallback {
	void onToggleButtonClick(BUTTON_TYPE type, boolean bStart);

    }

    private final Paint mRed = Paint.valueOf("red");
    private final Paint mGreen = Paint.valueOf("green");

    private final Button mToggleButton;
    private final ICallback mCallback;

    private boolean mOn;
    private final BUTTON_TYPE mType;

    public ToggleButton(ICallback callback, BUTTON_TYPE type, Button startButton) {
	mCallback = callback;
	mType = type;
	mToggleButton = startButton;
	mOn = false;

	mToggleButton.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		onClick();
	    }

	});

	setButtonState();
    }

    public void toggle() {
	mOn = !mOn;
	setButtonState();
    }

    public void setState(boolean bOn) {
	mOn = bOn;
	setButtonState();
    }

    public boolean isStarted() {
	return mOn;
    }

    private void onClick() {
	toggle();
	setButtonState();
	mCallback.onToggleButtonClick(mType, mOn);
    }

    private void setButtonState() {
	if (mOn) {
	    mToggleButton.setTextFill(mRed);
	    if (mType == BUTTON_TYPE.START_STOP) {
		mToggleButton.setText(LanguageResources.get("main_start_button_stop"));
	    } else {
		mToggleButton.setText(LanguageResources.get("main_button_on"));
	    }
	} else {
	    mToggleButton.setTextFill(mGreen);
	    if (mType == BUTTON_TYPE.START_STOP) {
		mToggleButton.setText(LanguageResources.get("main_start_button_start"));
	    } else {
		mToggleButton.setText(LanguageResources.get("main_button_off"));
	    }
	}
    }

    public enum BUTTON_TYPE {
	START_STOP,
	SAVIOR,
	PC_SYNC_XML,
	POTION_MAKER
    }

}
