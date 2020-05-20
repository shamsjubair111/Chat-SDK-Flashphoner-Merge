package sdk.chat.demo.examples.api;

import android.content.Context;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome;

import java.util.concurrent.TimeUnit;

import co.chatsdk.firebase.file_storage.FirebaseUploadModule;
import co.chatsdk.firebase.module.FirebaseModule;
import co.chatsdk.firebase.push.FirebasePushModule;
import co.chatsdk.profile.pictures.ProfilePicturesModule;
import co.chatsdk.ui.icons.Icons;
import co.chatsdk.ui.module.DefaultUIModule;
import sdk.chat.core.interfaces.ThreadType;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.demo.examples.activities.AChatActivity;
import sdk.chat.demo.examples.activities.APrivateThreadsFragment;
import sdk.chat.demo.examples.activities.MessageTestChatOption;
import sdk.chat.realtime.R;
import sdk.chat.ui.extras.ExtrasModule;

public class CustomiseInterfaceExample extends BaseExample {
    public CustomiseInterfaceExample(Context context) {

        // There are many more configuration options,
        // explore them yourself by looking at the Configuration builder.
        try {

            ChatSDK.builder()

                    // Configure the library
                    .setGoogleMaps("AIzaSyCwwtZrlY9Rl8paM0R6iDNBEit_iexQ1aE")
                    .setAnonymousLoginEnabled(false)
                    .setDebugModeEnabled(true)
                    .setLogoDrawableResourceID(R.drawable.ic_launcher_big)
                    .setPublicChatRoomLifetimeMinutes(TimeUnit.HOURS.toMinutes(24))
                    .build()

                    // Add the network adapter module
                    .addModule(FirebaseModule.builder(config -> config
                            .setFirebaseRootPath("rootPath")
                    ))

                    // Add the UI module
                    .addModule(DefaultUIModule.builder(config -> config
                            .setPublicRoomCreationEnabled(true)
                            .setTheme(R.style.CustomChatSDKTheme)
                    ))

                    .addModule(ExtrasModule.builder(config -> config
                            .setDrawerEnabled(true)
                    ))

                    // Add modules to handle file uploads, push notifications
                    .addModule(FirebaseUploadModule.shared())
                    .addModule(FirebasePushModule.shared())
                    .addModule(ProfilePicturesModule.shared())

//                    .addModule(FirestreamModule.shared(config -> config
//                            .setRoot(rootPath)
//                            .setStartListeningFromLastSentMessageDateEnabled(false)
//                            .setListenToMessagesWithTimeAgo(FirestreamConfig.TimePeriod.days(7))
//                            .setDatabaseType(FirestreamConfig.DatabaseType.Realtime)
//                            .setDeleteMessagesOnReceiptEnabled(false)
//                            .setDeliveryReceiptsEnabled(false)
//                    ))

//                     Enable Firebase UI
//                    .addModule(FirebaseUIModule.shared(config -> config
//                            .setProviders(EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID)
//                    ))

                    // Activate
                    .build()
                    .activate(context);

            // Override the chat activity
            // All activities can be overridden
            ChatSDK.ui().setChatActivity(AChatActivity.class);

            // Override a fragment
            // All fragments can be overridden
            ChatSDK.ui().setPrivateThreadsFragment(new APrivateThreadsFragment());

            // Add an extra tab
            ChatSDK.ui().addTab("Title", Icons.get(Icons.choose().search, R.color.gray), new APrivateThreadsFragment());

            // Remove a tab
            ChatSDK.ui().removeTab(0);

            // Define custom chat options that are displayed
            // in the chat activity when the options button is pressed
            ChatSDK.ui().addChatOption(new MessageTestChatOption("Title", null));

            // Define when notifications are shown
            ChatSDK.ui().setLocalNotificationHandler(thread -> {
                if (thread.typeIs(ThreadType.Private1to1)) {
                    return true;
                }
                return false;
            });

        } catch (Exception e) {
            assert(false);
            // Handle error
        }

        Icons.shared().block = new IconicsDrawable(context, FontAwesome.Icon.faw_user);

        // Override some activities



    }
}