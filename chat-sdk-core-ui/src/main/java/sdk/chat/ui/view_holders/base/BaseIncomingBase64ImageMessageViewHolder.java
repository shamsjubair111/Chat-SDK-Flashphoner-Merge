package sdk.chat.ui.view_holders.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;

import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.R;
import sdk.chat.ui.chat.model.Base64ImageMessageHolder;
import sdk.chat.ui.module.UIModule;
import sdk.chat.ui.utils.ImageLoaderPayload;

public class BaseIncomingBase64ImageMessageViewHolder <T extends Base64ImageMessageHolder> extends MessageHolders.BaseIncomingMessageViewHolder<T>  {

    protected ImageView image;
    protected ImageView imageOverlay;
    protected LinearLayout imageOverlayContainer;

    protected View onlineIndicator;
    protected TextView userName;

    public BaseIncomingBase64ImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);

        image = itemView.findViewById(R.id.image);
        imageOverlay = itemView.findViewById(R.id.imageOverlay);
        imageOverlayContainer = itemView.findViewById(R.id.imageOverlayContainer);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
        userName = itemView.findViewById(R.id.userName);


    }

    @Override
    public void onBind(T message) {
        super.onBind(message);

        image.setImageBitmap(message.image());

        imageOverlay.setImageDrawable(ChatSDKUI.icons().get(imageOverlay.getContext(), ChatSDKUI.icons().check, R.color.white));

        if (imageOverlayContainer != null) {
            imageOverlayContainer.setVisibility(isSelected() ? View.VISIBLE : View.INVISIBLE);
        }

        boolean isOnline = message.getUser().isOnline();
        UIModule.shared().getOnlineStatusBinder().bind(onlineIndicator, isOnline);

        UIModule.shared().getNameBinder().bind(userName, message);
        UIModule.shared().getTimeBinder().bind(time, message);

    }

    /**
     * Override this method to have ability to pass custom data in ImageLoader for loading image(not avatar).
     *
     * @param message Message with image
     */
    protected Object getPayloadForImageLoader(T message) {
        return new ImageLoaderPayload(message.placeholder());
    }

}
