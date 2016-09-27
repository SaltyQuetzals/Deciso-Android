package com.example.quont.queueband;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

/**
 * Created by quont on 6/11/2016.
 */

public class SocketService extends Service {
    Socket socket;
    String roomID;
    String userID;
    String tempRoomID;
    String tempUserID;
    ArrayList<JSONObject> suggestionSongs;
    int users;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SocketService() {
        super();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        suggestionSongs = new ArrayList<JSONObject>();
        if (intent != null) {
            tempUserID = intent.getExtras().getString("userid", "0");
            Log.i("SocketService", "userID=" + userID);
            tempRoomID = intent.getExtras().getString("code");
            Log.i("SocketService", "roomID=" + roomID);
            roomID = tempRoomID;
            userID = tempUserID;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        socket = IO.socket("https://deciso.audio");
                        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.i("Internet-Task", "Connected to deciso.audio");
                            }

                        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                            @Override
                            public void call(Object... arg0) {
                                socket.disconnect();
                                Log.i(getClass().getCanonicalName(), "Disconnected from server");
                            }

                        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.i("Server Error", args[0].toString());
                            }
                        }).on("room closed", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.i("SocketService-closed", "Room Closed.");
                                socket.disconnect();
                            }
                        }).on("song add", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    String json = args[0].toString();
                                    Log.i("SocketService-add", json);
                                    JSONObject jsonObject = new JSONObject(json);
                                    EventBus.getDefault().post(new SuggestionEvent(jsonObject));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).on("song update", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    Log.i("Song-update", args[0].toString());
                                    EventBus.getDefault().post(new VoteUpdateEvent(new JSONObject(args[0].toString())));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).on("song remove", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                if (args[0] != null) {
                                    Log.i("SocketService-remove", args[0].toString());
                                    EventBus.getDefault().post(new SuggestionRemoveEvent(args[0].toString()));
                                }
                            }
                        }).on("song change", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    JSONObject jsonObject = new JSONObject(args[0].toString());
                                    Log.i("SocketService-change", args[0].toString());
                                    EventBus.getDefault().post(new ChangedEvent(jsonObject, (float) jsonObject.getDouble("position")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).on("user update", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.i("Internet-Task", "Number of users updated. Is now: " + Integer.parseInt((args[0].toString())));
                            }
                        });

                        socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Transport transport = (Transport) args[0];

                                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                                    @Override
                                    public void call(Object... args) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                                        // modify request headers
                                        Log.i("SocketService", "Headers requested.  Transmitting roomID = " + roomID + ", and userID = " + userID);
                                        headers.put("userid", Arrays.asList(userID));
                                        headers.put("roomid", Arrays.asList(roomID));
                                    }
                                });

                                transport.on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                                    @Override
                                    public void call(Object... args) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                                        // access response headers
                                        String cookie = headers.get("Set-Cookie").get(0);
                                    }
                                });
                            }
                        });
                        socket.connect();
                    } catch (URISyntaxException e) {
                        Log.e(getClass().getCanonicalName(), e.toString());
                    }
                }
            };
            thread.start();
        }
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
