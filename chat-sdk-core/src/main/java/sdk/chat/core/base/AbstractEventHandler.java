package sdk.chat.core.base;

import io.reactivex.Scheduler;
import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.events.EventType;
import sdk.chat.core.events.NetworkEvent;
import sdk.chat.core.handlers.EventHandler;
import sdk.chat.core.interfaces.ThreadType;
import sdk.chat.core.notifications.NotificationDisplayHandler;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.ReadStatus;
import sdk.chat.core.utils.AppBackgroundMonitor;
import sdk.guru.common.DisposableMap;
import io.reactivex.Observable;
import sdk.guru.common.RX;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import sdk.guru.common.RX;
import io.reactivex.subjects.PublishSubject;

public abstract class AbstractEventHandler implements EventHandler {

    final protected PublishSubject<NetworkEvent> eventSource = PublishSubject.create();
    final protected PublishSubject<Throwable> errorSource = PublishSubject.create();

    final protected DisposableMap dm = new DisposableMap();

    public Disposable localNotificationDisposable;

    public AbstractEventHandler() {
        dm.add(source().filter(NetworkEvent.filterType(EventType.Logout)).subscribe(networkEvent -> {
            dm.dispose();
        }, this));

        if (localNotificationDisposable != null) {
            localNotificationDisposable.dispose();
        }

        localNotificationDisposable = sourceOn(RX.quick())
                .filter(NetworkEvent.filterType(EventType.MessageAdded))
                .subscribe(networkEvent -> {
                    Message message = networkEvent.getMessage();
                    Thread thread = networkEvent.getThread();
                    if (message != null && !AppBackgroundMonitor.shared().inBackground() && thread.isMuted()) {
                        if (thread.typeIs(ThreadType.Private) || (thread.typeIs(ThreadType.Public) && ChatSDK.config().localPushNotificationsForPublicChatRoomsEnabled)) {
                            if (!message.getSender().isMe() && !message.isDelivered() && ChatSDK.ui().showLocalNotifications(message.getThread()) || NotificationDisplayHandler.connectedToAuto(ChatSDK.ctx())) {
                                ReadStatus status = message.readStatusForUser(ChatSDK.currentUser());
                                if (!message.isRead() && !status.is(ReadStatus.delivered()) && !status.is(ReadStatus.read())) {
                                    // Only show the alert if we'recyclerView not on the private threads tab
                                    RX.onMain(() -> ChatSDK.ui().notificationDisplayHandler().createMessageNotification(message));
                                }
                            }
                        }
                    }
                });

    }

    public PublishSubject<NetworkEvent> source() {
        return eventSource;
    }

    public Observable<NetworkEvent> sourceOnMain () {
        return source().hide().observeOn(RX.main());
    }

    public Observable<NetworkEvent> sourceOn (Scheduler scheduler) {
        return source().hide().observeOn(scheduler);
    }

    public Observable<Throwable> errorSourceOnMain() {
        return errorSource.hide().observeOn(RX.main());
    }

    public void accept(Throwable t) throws Exception {
        onError(t);
    };

    public void disposeOnLogout(Disposable d) {
        dm.add(d);
    }

    public void onSubscribe(@NonNull Disposable d) {
        dm.add(d);
    }

    public void onComplete() {}

    public void onError(@NonNull Throwable e) {
        errorSource.onNext(e);
    }


}
