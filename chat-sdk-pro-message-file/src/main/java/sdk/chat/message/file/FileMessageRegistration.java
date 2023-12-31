package sdk.chat.message.file;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;

import org.pmw.tinylog.Logger;

import java.util.List;

import sdk.chat.core.dao.Keys;
import sdk.chat.core.dao.Message;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.MessageType;
import sdk.chat.message.file.v2.V2File;
import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.chat.model.MessageHolder;
import sdk.chat.ui.custom.DefaultMessageRegistration;

public class FileMessageRegistration extends DefaultMessageRegistration {

    @Override
    public List<Byte> getTypes() {
        return types(MessageType.File);
    }

    @Override
    public boolean hasContentFor(MessageHolder holder) {
        return holder.getClass().equals(FileMessageHolder.class);
    }

    @Override
    public void onBindMessageHolders(Context ctx, MessageHolders holders) {
        holders.registerContentType(
                (byte) MessageType.File,
                V2File.IncomingMessageViewHolder.class,
                R.layout.view_holder_incoming_text_message,
                V2File.OutcomingMessageViewHolder.class,
                R.layout.view_holder_outcoming_text_message,
                ChatSDKUI.shared().getMessageRegistrationManager());
    }

    @Override
    public MessageHolder onNewMessageHolder(Message message) {
        if (message.getMessageType().is(MessageType.File)) {
            return new FileMessageHolder(message);
        }
        return null;
    }

    @Override
    public boolean onClick(Activity activity, View rootView, Message message) {
        if (!super.onClick(activity, rootView, message)) {
            if (message.getMessageType().is(MessageType.File)) {
                String url = message.stringForKey(Keys.MessageFileURL);
                Uri uri = Uri.parse(url);
                final String mimeType = message.stringForKey(Keys.MessageMimeType);

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(uri.toString()), mimeType);
                    ChatSDK.core().addBackgroundDisconnectExemption();
                    activity.startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    Logger.debug(e);
                    ChatSDK.core().addBackgroundDisconnectExemption();
                    activity.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), activity.getText(R.string.open_with)));
                }
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(Activity activity, View rootView, Message message) {
        return false;
    }
}
