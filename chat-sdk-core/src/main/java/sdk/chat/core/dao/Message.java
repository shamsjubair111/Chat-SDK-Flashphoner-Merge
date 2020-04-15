package sdk.chat.core.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your token includes here


import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sdk.chat.core.base.AbstractEntity;
import sdk.chat.core.events.NetworkEvent;
import sdk.chat.core.interfaces.ThreadType;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.MessageSendProgress;
import sdk.chat.core.types.MessageSendStatus;
import sdk.chat.core.types.MessageType;
import sdk.chat.core.types.ReadStatus;
import sdk.chat.core.utils.DaoDateTimeConverter;

@Entity
public class Message extends AbstractEntity {

    @Id
    private Long id;

    @Unique
    private String entityID;

    @Convert(converter = DaoDateTimeConverter.class, columnType = Long.class)
    private DateTime date;
    private Integer type;
    private Integer status;
    private Long senderId;
    private Long threadId;
    private Long nextMessageId;
    private Long previousMessageId;

    @ToMany(referencedJoinProperty = "messageId")
    private List<ReadReceiptUserLink> readReceiptLinks;

    @ToOne(joinProperty = "senderId")
    private User sender;

    @ToOne(joinProperty = "threadId")
    private Thread thread;

    @ToOne(joinProperty = "nextMessageId")
    private Message nextMessage;

    @ToOne(joinProperty = "previousMessageId")
    private Message previousMessage;

    @ToMany(referencedJoinProperty = "messageId")
    private List<MessageMetaValue> metaValues;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 859287859)
    private transient MessageDao myDao;

    @Generated(hash = 405082643)
    public Message(Long id, String entityID, DateTime date, Integer type, Integer status, Long senderId,
            Long threadId, Long nextMessageId, Long previousMessageId) {
        this.id = id;
        this.entityID = entityID;
        this.date = date;
        this.type = type;
        this.status = status;
        this.senderId = senderId;
        this.threadId = threadId;
        this.nextMessageId = nextMessageId;
        this.previousMessageId = previousMessageId;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    @Generated(hash = 880682693)
    private transient Long sender__resolvedKey;

    @Generated(hash = 1974258785)
    private transient Long thread__resolvedKey;

    @Generated(hash = 992601680)
    private transient Long nextMessage__resolvedKey;

    @Generated(hash = 829136111)
    private transient Long previousMessage__resolvedKey;

    public boolean isRead() {
        if (sender != null && sender.isMe()) {
            return true;
        } else {
            ReadStatus status = readStatusForUser(ChatSDK.currentUser());
            if (status != null && status.is(ReadStatus.read())) {
                return true;
            }
        }
        return false;
    }

    public void markReadIfNecessary() {
        if (!isRead()) {
            markRead();
        }
    }

    public void markRead() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (ChatSDK.readReceipts() != null) {
                    ChatSDK.readReceipts().markRead(Message.this);
                }
                setUserReadStatus(ChatSDK.currentUser(), ReadStatus.read(), new DateTime());
            }
        });
    }

    public boolean isDelivered () {
        if (sender != null && sender.isMe()) {
            return true;
        } else {
            ReadStatus status = readStatusForUser(ChatSDK.currentUser());
            if (status != null && status.getValue() >= ReadStatus.Delivered) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void markDelivered() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (!isRead()) {
                    setUserReadStatus(ChatSDK.currentUser(), ReadStatus.delivered(), new DateTime());
                }
            }
        });
    }

    @Override
    public String toString() {
        return String.format("Message, id: %s, type: %s, Sender: %s", id, type, getSender());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityID() {
        return this.entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public DateTime getDate() {
        return this.date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public HashMap<String, Object> getMetaValuesAsMap() {
        HashMap<String, Object> values = new HashMap<>();
        for (MessageMetaValue v : getMetaValues()) {
            values.put(v.getKey(), v.getValue());
        }
        return values;
    }

    public void setMetaValues(@Nullable Map<String, Object> json) {
        if (json != null) {
            for (String key : json.keySet()) {
                setMetaValue(key, json.get(key));
            }
        }
    }

    protected void setMetaValue(String key, Object value) {
        MessageMetaValue metaValue = (MessageMetaValue) metaValue(key);
        if (metaValue == null) {
            metaValue = ChatSDK.db().createEntity(MessageMetaValue.class);
            metaValue.setMessageId(this.getId());
            getMetaValues().add(metaValue);
        }
        metaValue.setValue(MetaValueHelper.toString(value));
        metaValue.setKey(key);
        metaValue.update();
//        this.update();
    }

    protected MetaValue metaValue (String key) {
        return MetaValueHelper.metaValueForKey(key, getMetaValues());
    }

    public Object valueForKey (String key) {
        MetaValue value = metaValue(key);
        if (value != null && value.getValue() != null) {
            return MetaValueHelper.toObject(value.getValue());
        } else {
            return null;
        }
    }

    public String stringForKey (String key) {
        Object value = valueForKey(key);
        if (value == null) return "";
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    public Double doubleForKey (String key) {
        Object value = valueForKey(key);
        if (value instanceof Double) {
            return (Double) value;
        }
        else {
            return (double) 0;
        }
    }

    public ReadReceiptUserLink linkForUser (User user) {
        return ChatSDK.db().readReceipt(getId(), user.getId());
    }

    public boolean setUserReadStatus (User user, ReadStatus status, DateTime date) {
        return setUserReadStatus(user, status, date, true);
    }

    public boolean setUserReadStatus (User user, ReadStatus status, DateTime date, boolean notify) {
        ReadReceiptUserLink link = linkForUser(user);

        if (link == null || link.getStatus() <= status.getValue()) {
            if(link == null) {
                link = ChatSDK.db().createEntity(ReadReceiptUserLink.class);
                link.setMessageId(this.getId());
                getReadReceiptLinks().add(link);
            }

            link.setUser(user);
            link.setStatus(status.getValue());
            link.setDate(date);

            link.update();

            if (notify) {
                ChatSDK.events().source().onNext(NetworkEvent.messageReadReceiptUpdated(this));
            }
            return true;
        }
        return false;
    }

    public LatLng getLocation () {
        Double latitude = doubleForKey(Keys.MessageLatitude);
        Double longitude = doubleForKey(Keys.MessageLongitude);
        return new LatLng(latitude, longitude);
    }

    public void setValueForKey (Object payload, String key) {
        setMetaValue(key, payload);
    }

    public String getText() {
        return stringForKey(Keys.MessageText);
    }

    public void setText(String text) {
        setValueForKey(text, Keys.MessageText);
    }

    public Integer getType () {
        return this.type;
    }

    public MessageType getMessageType() {
        if(this.type != null) {
            return new MessageType(this.type);
        }
        return new MessageType(MessageType.None);
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public void setMessageType(MessageType type) {
        this.type = type.value();
    }

    public Integer getStatus() {
        return this.status;
    }
    public MessageSendStatus getMessageStatus() {
        if(this.status != null) {
            return MessageSendStatus.values()[this.status];
        }
        return MessageSendStatus.None;
    }

    public void setMessageStatus(MessageSendStatus status) {
        setMessageStatus(status, true);
    }

    public void setMessageStatus(MessageSendStatus status, boolean notify) {
        if (status != null || this.status != status.ordinal()) {
            this.status = status.ordinal();
            this.update();
            if (notify) {
                ChatSDK.events().source().onNext(NetworkEvent.messageSendStatusChanged(new MessageSendProgress(this)));
            }
        }
    }
    public void setStatus(Integer status) {
        this.status = status;
    }


    public Long getThreadId() {
        return this.threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public ReadStatus readStatusForUser (User user) {
        return readStatusForUser(user.getId());
    }

    public String getTextRepresentation () {
        if (getMessageType().is(MessageType.Location) && ChatSDK.locationMessage() != null) {
            ChatSDK.locationMessage().textRepresentation(this);
        }
        else if (getMessageType().is(MessageType.Image) && ChatSDK.imageMessage() != null) {
            return ChatSDK.imageMessage().textRepresentation(this);
        }
        else if (getMessageType().is(MessageType.Audio) && ChatSDK.audioMessage() != null) {
            return ChatSDK.audioMessage().textRepresentation(this);
        }
        else if (getMessageType().is(MessageType.Video) && ChatSDK.videoMessage() != null) {
            return ChatSDK.videoMessage().textRepresentation(this);
        }
        else if (getMessageType().is(MessageType.File) && ChatSDK.fileMessage() != null) {
            return ChatSDK.fileMessage().textRepresentation(this);
        }
        return getText();
    }

    public ReadStatus readStatusForUser (Long userId) {
        ReadReceiptUserLink link = ChatSDK.db().readReceipt(getId(), userId);
        if (link != null) {
            return new ReadStatus(link.getStatus());
        }
        return ReadStatus.none();
    }

    public ReadStatus getReadStatus () {
        if (getThread().typeIs(ThreadType.Public)) {
            return ReadStatus.hide();
        }

        int userCount = 0;
        int deliveredCount = 0;
        int readCount = 0;
        for(ReadReceiptUserLink link : getReadReceiptLinks()) {
            if (link.getStatus() != ReadStatus.Hide) {
                if (link.getStatus() == ReadStatus.Delivered) {
                    deliveredCount++;
                }
                if (link.getStatus() == ReadStatus.Read) {
                    deliveredCount++;
                    readCount++;
                }
                userCount++;
            }
        }
        if (readCount == userCount && userCount != 0) {
            return ReadStatus.read();
        }
        else if (deliveredCount == userCount && userCount != 0) {
            return ReadStatus.delivered();
        }
        else {
            return ReadStatus.none();
        }
    }

    public Long getSenderId() {
        return this.senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void cascadeDelete () {
        for (MessageMetaValue value : getMetaValues()) {
            value.delete();
        }
        for (ReadReceiptUserLink link : getReadReceiptLinks()) {
            link.delete();
        }
        delete();
    }

    public Long getNextMessageId() {
        return this.nextMessageId;
    }

    public void setNextMessageId(Long nextMessageId) {
        this.nextMessageId = nextMessageId;
    }

    public Long getPreviousMessageId() {
        return this.previousMessageId;
    }

    public void setPreviousMessageId(Long previousMessageId) {
        this.previousMessageId = previousMessageId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1145839495)
    public User getSender() {
        Long __key = this.senderId;
        if (sender__resolvedKey == null || !sender__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User senderNew = targetDao.load(__key);
            synchronized (this) {
                sender = senderNew;
                sender__resolvedKey = __key;
            }
        }
        return sender;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1434008871)
    public void setSender(User sender) {
        synchronized (this) {
            this.sender = sender;
            senderId = sender == null ? null : sender.getId();
            sender__resolvedKey = senderId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1483947909)
    public Thread getThread() {
        Long __key = this.threadId;
        if (thread__resolvedKey == null || !thread__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreadDao targetDao = daoSession.getThreadDao();
            Thread threadNew = targetDao.load(__key);
            synchronized (this) {
                thread = threadNew;
                thread__resolvedKey = __key;
            }
        }
        return thread;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1938921797)
    public void setThread(Thread thread) {
        synchronized (this) {
            this.thread = thread;
            threadId = thread == null ? null : thread.getId();
            thread__resolvedKey = threadId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 871948279)
    public Message getNextMessage() {
        Long __key = this.nextMessageId;
        if (nextMessage__resolvedKey == null || !nextMessage__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MessageDao targetDao = daoSession.getMessageDao();
            Message nextMessageNew = targetDao.load(__key);
            synchronized (this) {
                nextMessage = nextMessageNew;
                nextMessage__resolvedKey = __key;
            }
        }
        return nextMessage;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1912932494)
    public void setNextMessage(Message nextMessage) {
        synchronized (this) {
            this.nextMessage = nextMessage;
            nextMessageId = nextMessage == null ? null : nextMessage.getId();
            nextMessage__resolvedKey = nextMessageId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2025183823)
    public List<ReadReceiptUserLink> getReadReceiptLinks() {
        if (readReceiptLinks == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReadReceiptUserLinkDao targetDao = daoSession.getReadReceiptUserLinkDao();
            List<ReadReceiptUserLink> readReceiptLinksNew = targetDao
                    ._queryMessage_ReadReceiptLinks(id);
            synchronized (this) {
                if (readReceiptLinks == null) {
                    readReceiptLinks = readReceiptLinksNew;
                }
            }
        }
        return readReceiptLinks;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 273652628)
    public synchronized void resetReadReceiptLinks() {
        readReceiptLinks = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2015206446)
    public List<MessageMetaValue> getMetaValues() {
        if (metaValues == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MessageMetaValueDao targetDao = daoSession.getMessageMetaValueDao();
            List<MessageMetaValue> metaValuesNew = targetDao._queryMessage_MetaValues(id);
            synchronized (this) {
                if (metaValues == null) {
                    metaValues = metaValuesNew;
                }
            }
        }
        return metaValues;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 365870950)
    public synchronized void resetMetaValues() {
        metaValues = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 747015224)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMessageDao() : null;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1032175552)
    public Message getPreviousMessage() {
        Long __key = this.previousMessageId;
        if (previousMessage__resolvedKey == null || !previousMessage__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MessageDao targetDao = daoSession.getMessageDao();
            Message previousMessageNew = targetDao.load(__key);
            synchronized (this) {
                previousMessage = previousMessageNew;
                previousMessage__resolvedKey = __key;
            }
        }
        return previousMessage;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 222971428)
    public void setPreviousMessage(Message previousMessage) {
        synchronized (this) {
            this.previousMessage = previousMessage;
            previousMessageId = previousMessage == null ? null : previousMessage.getId();
            previousMessage__resolvedKey = previousMessageId;
        }
    }

    public boolean isReply() {
        String reply = getReply();
        if (reply != null && !reply.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getReply() {
        Object replyObject = valueForKey(Keys.Reply);
        if (replyObject instanceof String) {
            return (String) replyObject;
        }
        return null;
    }

    public String imageURL() {
        return stringForKey(Keys.MessageImageURL);
    }

}
