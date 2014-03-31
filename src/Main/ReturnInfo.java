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

import Server.Out_ZIP_Message;

/**
 * <h3>public class ReturnInfo</h3>
 * <p>
 * This class is used to return the results of a function call that requires a result code and
 * additional data.
 * <h3>Constructors</h3>
 * <ul>
 * <li><code>ReturnInfo()</code> - Default for Success = true
 * <li><code>public ReturnInfo(boolean response)</code> - For setting Success to false
 * <li><code>public ReturnInfo(ERROR_TYPE errorType, String errorMsg)</code> - For errors
 *
 * @author Rick Falck
 * @version 1.0
 */
public class ReturnInfo {
    private boolean mSuccess = true;
    private String mMessage = null;
    private ERROR_TYPE mErrorType;
    private Exception mException = null;
    
    public Out_ZIP_Message mOutZipMessage = new Out_ZIP_Message();
    
    /**
     * <h3>public ReturnInfo()</he>
     * <p>
     * Default constructor for a successful response
     */
    public ReturnInfo() {
    }

    /**
     * <h3>public ReturnInfo(boolean response)</he>
     * <p>
     * Constructor for an error response
     * @param response
     */
    public ReturnInfo(boolean response) {
	mSuccess = response;
    }

    /**
     * <h3>public ReturnInfo(ERROR_TYPE errorType, String errorMsg)</he>
     * <p>
     * Constructor for an error response, that takes the error type and error message
     * @param errorType
     * @param errorMsg
     */
    public ReturnInfo(ERROR_TYPE errorType, String errorMsg) {
	mSuccess = false;
	mErrorType = errorType;
	mMessage = errorMsg;
    }

    public boolean isSuccess() {
	return mSuccess;
    }

    public void setSuccess(boolean success) {
	mSuccess = success;
    }

    public String getMessage() {
	return mMessage;
    }

    public void setMessage(String message) {
	mMessage = message;
    }

    public ERROR_TYPE getErrorType() {
	return mErrorType;
    }

    public void setErrorType(ERROR_TYPE errorType) {
	mErrorType = errorType;
    }

    public Exception getException() {
	return mException;
    }

    public void setException(Exception exception) {
	mException = exception;
    }

    /**
     * <h3>public enum ERROR_TYPE</h3>
     * <p>
     * The type of error that occurred
     *
     */
    public enum ERROR_TYPE {
	/**
	 * IOException and other file errors
	 */
	FILE_IO,
	/**
	 * XML XMLStreamException, IOException and other errors reading an XML file
	 */
	XML_READ,
	/**
	 * XML XMLStreamException, IOException and other errors writing an XML file
	 */
	XML_WRITE,
	/**
	 * Edit error on a field
	 */
	FIELD_FORMAT,
	/**
	 * IOException and other errors with the ServerSocket
	 */
	SERVER_SOCKET,
	/**
	 * IOException and other errors responding to the client
	 */
	CLIENT_SOCKET,
	/**
	 * Other error
	 */
	OTHER
    }

}
