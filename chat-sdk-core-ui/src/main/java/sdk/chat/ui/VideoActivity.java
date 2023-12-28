package sdk.chat.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.bean.Connection;
import com.flashphoner.fpwcsapi.bean.Data;
import com.flashphoner.fpwcsapi.bean.StreamStatus;
import com.flashphoner.fpwcsapi.layout.PercentFrameLayout;
import com.flashphoner.fpwcsapi.room.Message;
import com.flashphoner.fpwcsapi.room.Participant;
import com.flashphoner.fpwcsapi.room.Room;
import com.flashphoner.fpwcsapi.room.RoomEvent;
import com.flashphoner.fpwcsapi.room.RoomManager;
import com.flashphoner.fpwcsapi.room.RoomManagerEvent;
import com.flashphoner.fpwcsapi.room.RoomManagerOptions;
import com.flashphoner.fpwcsapi.room.RoomOptions;
import com.flashphoner.fpwcsapi.session.RestAppCommunicator;
import com.flashphoner.fpwcsapi.session.Stream;
import com.flashphoner.fpwcsapi.session.StreamStatusEvent;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class VideoActivity extends AppCompatActivity {

    private static String TAG = VideoActivity.class.getName();

    private static final int PUBLISH_REQUEST_CODE = 100;

    // UI references.

    private EditText mLoginView;
    private TextView mConnectStatus;


    private TextView mJoinStatus;




    private TextView mParticipantName;

    private TextView mPublishStatus;

    private Switch mMuteAudio;
    private Switch mMuteVideo;

    private RelativeLayout relativeLayout;



    /**
     * RoomManager object is used to manage connection to server and video chat room.
     */
    private RoomManager roomManager;

    /**
     * Room object is used for work with the video chat room, to which the user is joined.
     */
    private Room room;

    private SurfaceViewRenderer localRenderer;

    private Queue<ParticipantView> freeViews = new LinkedList<>();
    private Map<String, ParticipantView> busyViews = new ConcurrentHashMap<>();

    private Stream stream;

    private ImageView callCancelButton;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        relativeLayout = findViewById(R.id.relativeLayout);

        if(type.equals("audio")){
            relativeLayout.setBackgroundColor(R.color.brilliant_blue);
        }



        callCancelButton = findViewById(R.id.callCancelButton);


        callCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        /**
         * Initialization of the API.
         */
        Flashphoner.init(this);




        mConnectStatus = (TextView) findViewById(R.id.connect_status);


        /**
         * Connection to server will be established when Connect button is clicked.
         */


                    /**
                     * The connection options are set.
                     * WCS server URL and user name are passed when RoomManagerOptions object is created.
                     */

        Random random = new Random();


        int randomNumber = 10000 + random.nextInt(90000);
                    RoomManagerOptions roomManagerOptions = new RoomManagerOptions("wss://tb.intercloud.com.bd:8443", ""+randomNumber);

                    /**
                     * RoomManager object is created with method createRoomManager().
                     * Connection session is created when RoomManager object is created.
                     */
                    roomManager = Flashphoner.createRoomManager(roomManagerOptions);

                    /**
                     * Callback functions for connection status events are added to make appropriate changes in controls of the interface when connection is established and closed.
                     */
                    roomManager.on(new RoomManagerEvent() {
                        @Override
                        public void onConnected(final Connection connection) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    if(connection.getStatus().equals("ESTABLISHED")){
                                        mConnectStatus.setText("Calling "+randomNumber);
                                    }


                                    if(connection.getStatus().equals("ESTABLISHED")){

                                        /**
                                         * Room name is set with method RoomOptions.setName().
                                         */
                                        RoomOptions roomOptions = new RoomOptions();
                                        roomOptions.setName("test2");

                                        /**
                                         * The participant joins a video chat room with method RoomManager.join().
                                         * RoomOptions object is passed to the method.
                                         * Room object is created and returned by the method.
                                         */
                                        room = roomManager.join(roomOptions);

                                        /**
                                         * Callback functions for events occurring in video chat room are added.
                                         * If the event is related to actions performed by one of the other participants, Participant object with data of that participant is passed to the corresponding function.
                                         */
                                        room.on(new RoomEvent() {
                                            @Override
                                            public void onState(final Room room) {
                                                /**
                                                 * After joining, Room object with data of the room is received.
                                                 * Method Room.getParticipants() is used to check the number of already connected participants.
                                                 * The method returns collection of Participant objects.
                                                 * The collection size is determined, and, if the maximum allowed number (in this case, three) has already been reached, the user leaves the room with method Room.leave().
                                                 */
                                                if (room.getParticipants().size() >= 2) {
                                                    room.leave(null);
                                                    runOnUiThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mJoinStatus.setText("Room is full");

                                                                }
                                                            }
                                                    );
                                                    return;
                                                }


                                                final StringBuffer chatState = new StringBuffer("participants: ");

                                                /**
                                                 * Iterating through the collection of the other participants returned by method Room.getParticipants().
                                                 * There is corresponding Participant object for each participant.
                                                 */
                                                for (final Participant participant : room.getParticipants()) {
                                                    /**
                                                     * A player view is assigned to each of the other participants in the room.
                                                     */
                                                    final ParticipantView participantView = freeViews.poll();
                                                    if (participantView != null) {
                                                        chatState.append(participant.getName()).append(",");
                                                        busyViews.put(participant.getName(), participantView);

                                                        /**
                                                         * Playback of the stream being published by the other participant is started with method Participant.play().
                                                         * SurfaceViewRenderer to be used to display the video stream is passed when the method is called.
                                                         */
                                                        runOnUiThread(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        participant.play(participantView.surfaceViewRenderer);
                                                                        participantView.login.setText(participant.getName());
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                                runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                mJoinStatus.setText("");
                                                                ActivityCompat.requestPermissions(VideoActivity.this,
                                                                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},
                                                                        100); //Apple publish



                                                            }
                                                        }
                                                );
                                            }

                                            @Override
                                            public void onJoined(final Participant participant) {
                                                /**
                                                 * When a new participant joins the room, a player view is assigned to that participant.
                                                 */
                                                final ParticipantView participantView = freeViews.poll();
                                                if (participantView != null) {
                                                    runOnUiThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    participantView.login.setText(participant.getName());

                                                                }
                                                            }
                                                    );
                                                    busyViews.put(participant.getName(), participantView);
                                                }
                                            }

                                            @Override
                                            public void onLeft(final Participant participant) {
                                                /**
                                                 * When one of the other participants leaves the room, player view assigned to that participant is freed.
                                                 */
                                                final ParticipantView participantView = busyViews.remove(participant.getName());
                                                if (participantView != null) {
                                                    runOnUiThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    participantView.login.setText("NONE");

                                                                    participantView.surfaceViewRenderer.release();
                                                                }
                                                            }
                                                    );
                                                    freeViews.add(participantView);
                                                }
                                            }

                                            @Override
                                            public void onPublished(final Participant participant) {
                                                /**
                                                 * When one of the other participants starts publishing, playback of the stream published by that participant is started.
                                                 */
                                                final ParticipantView participantView = busyViews.get(participant.getName());
                                                if (participantView != null) {
                                                    runOnUiThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    participant.play(participantView.surfaceViewRenderer);
                                                                }
                                                            }
                                                    );
                                                }
                                            }

                                            @Override
                                            public void onFailed(Room room, final String info) {
                                                room.leave(null);
                                                runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mJoinStatus.setText(info);

                                                            }
                                                        }
                                                );
                                            }

                                            @Override
                                            public void onMessage(final Message message) {
                                                /**
                                                 * When one of the participants sends a text message, the received message is added to the messages log.
                                                 */
                                                runOnUiThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {

                                                            }
                                                        });
                                            }
                                        });


                                    }
                                }
                            });
                        }

                        @Override
                        public void onDisconnection(final Connection connection) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    mMuteAudio.setEnabled(false);
                                    mMuteAudio.setChecked(false);
                                    mMuteVideo.setEnabled(false);
                                    mMuteVideo.setChecked(false);
                                    stream = null;
                                    mConnectStatus.setText(connection.getStatus());
                                    Iterator<Map.Entry<String, ParticipantView>> i = busyViews.entrySet().iterator();
                                    while (i.hasNext()) {
                                        Map.Entry<String, ParticipantView> e = i.next();
                                        e.getValue().login.setText("NONE");
                                        e.getValue().surfaceViewRenderer.release();
                                        i.remove();
                                        freeViews.add(e.getValue());
                                    }

                                }
                            });
                        }
                    });






        mJoinStatus = (TextView) findViewById(R.id.join_status);


        /**
         * The participant will join to video chat room when Join button is clicked.
         */





        mPublishStatus = (TextView) findViewById(R.id.publish_status);


        /**
         * The participant starts publishing video stream when Publish button is clicked.
         */


        /**
         * MuteAudio switch is used to mute/unmute audio of the published stream.
         * Audio is muted with method Stream.muteAudio() and unmuted with method Stream.unmuteAudio().
         */
        mMuteAudio = (Switch) findViewById(R.id.mute_audio);
        mMuteAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stream.muteAudio();
                } else {
                    stream.unmuteAudio();
                }
            }
        });

        /**
         * MuteVideo switch is used to mute/unmute video of the published stream.
         * Video is muted with method Stream.muteVideo() and unmuted with method Stream.unmuteVideo().
         */
        mMuteVideo = (Switch) findViewById(R.id.mute_video);
        mMuteVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stream.muteVideo();
                } else {
                    stream.unmuteVideo();
                }
            }
        });



        localRenderer = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
        PercentFrameLayout localRenderLayout = (PercentFrameLayout) findViewById(R.id.local_video_layout);
        localRenderLayout.setPosition(0, 0, 100, 100);
        localRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        localRenderer.setMirror(true);
        localRenderer.requestLayout();


        SurfaceViewRenderer remote1Render = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);
        PercentFrameLayout remote1RenderLayout = (PercentFrameLayout) findViewById(R.id.remote_video_layout);
        remote1RenderLayout.setPosition(0, 0, 100, 100);
        remote1Render.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        remote1Render.setMirror(false);
        remote1Render.requestLayout();

        mParticipantName = (TextView) findViewById(R.id.participant_name);
        freeViews.add(new ParticipantView(remote1Render, mParticipantName));

        if(type.equals("audio")){
            localRenderer.setVisibility(View.INVISIBLE);
            remote1Render.setVisibility(View.INVISIBLE);
            mMuteVideo.setVisibility(View.INVISIBLE);
            mMuteAudio.setVisibility(View.INVISIBLE);
        }
        mParticipantName.setVisibility(View.INVISIBLE);
        mPublishStatus.setVisibility(View.GONE);



    }



    private class ParticipantView {

        SurfaceViewRenderer surfaceViewRenderer;
        TextView login;

        public ParticipantView(SurfaceViewRenderer surfaceViewRenderer, TextView login) {
            this.surfaceViewRenderer = surfaceViewRenderer;
            this.login = login;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PUBLISH_REQUEST_CODE: {
                if (grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    /**
                     * Stream is created and published with method Room.publish().
                     * SurfaceViewRenderer to be used to display video from the camera is passed to the method.
                     */
                    stream = room.publish(localRenderer);

                    /**
                     * Callback function for stream status change is added to make appropriate changes in controls of the interface when stream is being published.
                     */
                    stream.on(new StreamStatusEvent() {
                        @Override
                        public void onStreamStatus(final Stream stream, final StreamStatus streamStatus) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (StreamStatus.PUBLISHING.equals(streamStatus)) {

                                        {
                                            if(getIntent().getStringExtra("type").equals("audio")){
                                                mMuteAudio.setEnabled(true);
                                                mMuteVideo.setEnabled(false);


                                            }
                                            else{
                                                mMuteAudio.setEnabled(true);
                                                mMuteVideo.setEnabled(true);

                                            }
                                        }
                                    } else {

                                        mMuteAudio.setEnabled(false);
                                        mMuteAudio.setChecked(false);
                                        mMuteVideo.setEnabled(false);
                                        mMuteVideo.setChecked(false);
                                        VideoActivity.this.stream = null;
                                    }



                                    mPublishStatus.setText(streamStatus.toString());

                                    mPublishStatus.setText("Connected");
                                }
                            });
                        }
                    });
                    Log.i(TAG, "Permission has been granted by user");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomManager != null) {
            roomManager.disconnect();
        }
    }



}