package chat.server;

import ChatApp.ChatException;
import ChatApp.ChatMessage;
import ChatApp.ChatRoom;
import com.zeroc.Ice.Current;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class ChatRoomImp implements ChatRoom {

  private final Set<String> loggedUsers = ConcurrentHashMap.newKeySet();
  private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
  private final List<ChatMessage> messageHistory = new CopyOnWriteArrayList<>();
  private final AtomicLong messageIdCounter = new AtomicLong(0);

  @Override
  public void login(String nickname, Current current) throws ChatException {
    if (nickname == null || nickname.trim().isEmpty()) {
      throw new ChatException("Nickname cannot be null");
    }

    String cleanNick = nickname.trim();

    if (loggedUsers.contains(cleanNick)) {
      throw new ChatException("The user " + cleanNick + " is already connected");
    }


    loggedUsers.add(cleanNick);

    String now = LocalTime.now().format(timeFormat);
    long id = messageIdCounter.incrementAndGet();
    ChatMessage joinMsg = new ChatMessage(id, " SYSTEM ", cleanNick + " joined the room.", now);
    messageHistory.add(joinMsg);
  }

  @Override
  public void postMessage(String nickname, String message, Current current) throws ChatException {
    if (nickname == null || nickname.trim().isEmpty()) {
      throw new ChatException("Nickname cannot be null");
    }

    if (message == null || message.trim().isEmpty()) {
      throw new ChatException("Message cannot be null");
    }

    String cleanNick = nickname.trim();

    if (!loggedUsers.contains(cleanNick)) {
      throw new ChatException("Access denied user you must be logged in");
    }

    String cleanMessageText = message.trim();
    long messageId = messageIdCounter.incrementAndGet();
    String now = LocalTime.now().format(timeFormat);

    ChatMessage newMessage = new ChatMessage(messageId, cleanNick, cleanMessageText, now);
    messageHistory.add(newMessage);
  }

  @Override
  public ChatMessage[] getPendingMessages(String nickname, long lastMessageId, Current current) {
    return messageHistory.stream().filter(m -> m.id > lastMessageId).toList().toArray(new ChatMessage[0]);
  }

  @Override
  public String[] getOnlineUsers(Current current) {
    return loggedUsers.toArray(new String[0]);
  }

  @Override
  public void logout(String nickname, Current current) {
    if (loggedUsers.remove(nickname)) {
      System.out.println("[ICE - SERVER] User disconnected: " + nickname);
      String now = LocalTime.now().format(timeFormat);
      long id = messageIdCounter.incrementAndGet();
      ChatMessage leaveMsg = new ChatMessage(id, " SYSTEM ", nickname + " left the room.", now);
      messageHistory.add(leaveMsg);
    }
  }
}
