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

package Server;

import Debug.Log;
import Language.LanguageResources;
import Main.ReturnInfo;
import Main.UserData;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javafx.application.Platform;
import spm_library.NETWORK_MESSAGE;
import spm_zip.NetworkMsgException;
import spm_zip.NetworkMsgException.NETWORK_EXCEPTION_CAUSE;

/**
 * @author Rick Falck
 * @version 1.0
 */
public class SPMSocketService {

    public interface UICallbacks {
	void onServerReceiveRequest(String infoText);

	void onServerSentDataToAndroid(String infoText);

	void onServerException(String errorText);

	void onError(String errorText);

	void onServerUpdate(String msg);

    }

    public interface XML_File_Callback {
	ReturnInfo getResponse(In_ZIP_Message inMsg);

	void setXMLExportFilePath(In_ZIP_Message inMsg);

    }

    public interface PotionMaker_Callback {
	ReturnInfo getNextFile();

	void onClientResponse(NETWORK_MESSAGE msg);

    }

    private final UICallbacks mUICallbacks;
    private final PotionMaker_Callback mPMCallback;
    private final XML_File_Callback mXMLCallback;

    private MyServerSocket mServer = null;

    public SPMSocketService(UICallbacks ucb, PotionMaker_Callback pcb, XML_File_Callback xcb) {
	super();
	mUICallbacks = ucb;
	mPMCallback = pcb;
	mXMLCallback = xcb;
    }

    public void stop() {
	if (mServer != null) {
	    mServer.stop();
	    mServer = null;
	}
    }

    public void start() {
	if (mServer == null) {
	    mServer = new MyServerSocket();
	    Log.Msg("SPMSocketService - Started", null);
	    new Thread(mServer, "SocketServer").start();
	}
    }

    public boolean isRunning() {
	return mServer != null;
    }

    public void socketAborted() {
	Log.Msg("SPMSocketService - socketAborted", null);
	mServer = null;
    }

    private class MyServerSocket implements Runnable {
	private ServerSocket mServerSocket = null;
	private boolean mStop = false;

	@Override
	public void run() {
	    int port = UserData.get().getPortNumber();
	    Socket socket = null;

	    try {
		mServerSocket = new ServerSocket(port);

		ReturnInfo waitInfo = new ReturnInfo();
		waitInfo.setMessage(LanguageResources.get("main_socket_waiting"));
		while (mStop == false) {

		    try {
			Platform.runLater(new UpdateMainThread(waitInfo));

			// Wait for message from Android ///////////////
			socket = mServerSocket.accept();
			////////////////////////////////////////////////

			System.out.println("InetAddress: " + socket.getInetAddress());
			System.out.println("Local Address: " + socket.getLocalAddress());
			System.out.println("Local Socket Address: " + socket.getLocalSocketAddress());

			Log.Msg("SPMSocketService - Request received from Android", null);

			In_ZIP_Message inMsg = new In_ZIP_Message();
			mXMLCallback.setXMLExportFilePath(inMsg);
			NETWORK_MESSAGE request;
			try {
			    inMsg.parseInput(socket.getInputStream());
			    request = inMsg.getNetworkMessage();
			    if (request == null) {
				throw new NetworkMsgException(NetworkMsgException.NETWORK_EXCEPTION_CAUSE.NETWORK_MESSAGE);
			    }
			} catch (NetworkMsgException ex) {
			    ReturnInfo result = getZipMessageError(ex);
			    result.setErrorType(ReturnInfo.ERROR_TYPE.FILE_IO);
			    result.mOutZipMessage.setNetworkMessage(NETWORK_MESSAGE.RESPONSE_SERVER_ERROR);
			    Log.Msg("SPMSocketService - parseInpt() failed " + result.getMessage(), null);
			    Platform.runLater(new UpdateMainThread(result));
			    socket.close();
			    continue;
			}

			ReturnInfo result = null;
			ReturnInfo ui_info = new ReturnInfo();
			Log.Msg("SPMSocketService - Request " + request.name(), null);
			switch (request) {
			    case REQUEST_PLAYER_DATA:
				ui_info.setMessage(LanguageResources.get("network_info_playerdatarequest_received"));
				Platform.runLater(new UpdateMainThread(ui_info));
				result = mPMCallback.getNextFile();
				break;
			    case RESPONSE_PLAYER_DATA_RECEIVED_OK:
				ui_info.setMessage(LanguageResources.get("network_info_playerdata_receivedAndroid"));
				Platform.runLater(new UpdateMainThread(ui_info));
				mPMCallback.onClientResponse(request);
				// No result for this
				break;
			    case REQUEST_WITH_PCSYNC_XML_DATA:
				ui_info.setMessage(LanguageResources.get("network_info_xml_export_received"));
				Platform.runLater(new UpdateMainThread(ui_info));
				result = mXMLCallback.getResponse(inMsg);
				break;
			    case REQUEST_INGREDIENTS_DATA:
				ui_info.setMessage(LanguageResources.get("network_info_xmlingredients_import_received"));
				Platform.runLater(new UpdateMainThread(ui_info));
				result = mXMLCallback.getResponse(inMsg);
				break;
			    case REQUEST_EFFECTS_DATA:
				ui_info.setMessage(LanguageResources.get("network_info_xmleffects_import_received"));
				Platform.runLater(new UpdateMainThread(ui_info));
				result = mXMLCallback.getResponse(inMsg);
				break;
			    case RESPONSE_PCSYNC_XML_RECEIVED:
				ui_info.setMessage(LanguageResources.get("network_info_xml_received"));
				Platform.runLater(new UpdateMainThread(ui_info));
				break;
			    case RESPONSE_XML_ERROR:
				ui_info.setMessage(LanguageResources.get("error_android_bad_data"));
				Platform.runLater(new UpdateMainThread(ui_info));
				break;
			    case REQUEST_PING:
				ui_info.setMessage(LanguageResources.get("network_info_ping_received"));
				Platform.runLater(new UpdateMainThread(ui_info));
				result = new ReturnInfo();
				result.mOutZipMessage = new Out_ZIP_Message(NETWORK_MESSAGE.RESPONSE_PING);
				result.setMessage(LanguageResources.get("network_info_ping_sent"));
				break;
			    default:
			    case MESSAGE_DATA_ERROR:
				throw new NetworkMsgException(NETWORK_EXCEPTION_CAUSE.NETWORK_MESSAGE);
			}

			if (result != null) {
			    Log.Msg("SPMSocketService - Sending response " + result.mOutZipMessage.getNetworkMessage(), null);
			    Platform.runLater(new UpdateMainThread(result));

			    if (result.mOutZipMessage.getNetworkMessage() != null) {
				try {
				    result.mOutZipMessage.writeOutput(socket.getOutputStream());
				} catch (NetworkMsgException ex) {
				    Log.Msg("Error writing the response to the server: ", ex);
				    ReturnInfo err = getZipMessageError(ex);
				    Platform.runLater(new UpdateMainThread(err));
				}
			    }

			} else {
			    Log.Msg("SPMSocketService - No response to send", null);
			}

			socket.close();
			socket = null;
		    } catch (SocketException se) {
			Log.Msg("SPMSocketService - SocketException ", se);
			ReturnInfo rInfo = new ReturnInfo();
			rInfo.setMessage(LanguageResources.get("main_socket_closed"));
			Platform.runLater(new UpdateMainThread(rInfo));
		    } catch (FileNotFoundException fe) {
			Log.Msg("SPMSocketService - FileNotFoundException ", fe);
			Platform.runLater(new UpdateMainThread(fe));
		    } catch (IOException e) {
			Log.Msg("SPMSocketService - IOException ", e);
			Platform.runLater(new UpdateMainThread(e));
		    } catch (NetworkMsgException ex) {
			Log.Msg("SPMSocketService - NetworkMsgException ", ex);
			ReturnInfo result = getZipMessageError(ex);
			Platform.runLater(new UpdateMainThread(result));
		    } catch (Exception ex) {
			Log.Msg("SPMSocketService - Other Exception " + ex.toString(), ex);
			ReturnInfo rInfo = new ReturnInfo();
			rInfo.setMessage("Other Exception: " + ex.toString());
			Platform.runLater(new UpdateMainThread(ex));
		    } finally {
			if (socket != null) {
			    ReturnInfo rInfo = new ReturnInfo();
			    rInfo.setMessage(LanguageResources.get("main_socket_closed"));
			    Platform.runLater(new UpdateMainThread(rInfo));
			    Log.Msg("SPMSocketService - In Socket Closed");

			    socket.close();
			    socket = null;

			}
		    }

		} // while()
	    } catch (IOException e) {
		Log.Msg("SPMSocketService - IOException ", e);
		Platform.runLater(new UpdateMainThread(e));
		socketAborted();
	    } finally {
		try {
		    mServerSocket.close();
		} catch (IOException e) {

		}
	    }

	    Log.Msg("SPMSocketService - Closed ", null);
	}

	public void stop() {
	    mStop = true;
	    try {
		mServerSocket.close();
	    } catch (IOException e) {

	    }
	}

	private ReturnInfo getZipMessageError(NetworkMsgException ex) {

	    ReturnInfo result = new ReturnInfo(false);
	    result.setMessage(NW_EXC.getErrorText(ex));

	    return result;
	}

	private class UpdateMainThread implements Runnable {
	    private final ReturnInfo mInfo;

	    public UpdateMainThread(ReturnInfo info) {
		mInfo = info;
	    }

	    public UpdateMainThread(Exception exc) {
		mInfo = new ReturnInfo(false);
		mInfo.setMessage(LanguageResources.get("error_exception") + exc.getMessage());
	    }

	    @Override
	    public void run() {
		mUICallbacks.onServerUpdate(mInfo.getMessage());
	    }

	}
    }
}
