package fr.bebedlastreat.livechat.discord;

import fr.bebedlastreat.livechat.DiscordBot;
import fr.bebedlastreat.livechat.LiveChat;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {

    private final DiscordBot bot;

    public MessageListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Guild guild = e.getGuild();
        MessageChannelUnion channel = e.getChannel();
        if (channel.getIdLong() == bot.getChannel().getIdLong() && guild.getIdLong() == bot.getGuild().getIdLong()) {
            Message message = e.getMessage();
            if (!(message instanceof ReceivedMessage)) return;
            ReceivedMessage receivedMessage = (ReceivedMessage) message;
            System.out.println(message);
            System.out.println("message: " + message.getContentRaw());
            System.out.println("attachments: " + message.getAttachments());
            for (Message.Attachment attachment : message.getAttachments()) {
                System.out.println("url: " + attachment.getUrl());
            }
            String msg = message.getContentDisplay();
            if (msg.startsWith("//")) return;
            if (!msg.isEmpty()) {
                String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(msg);
                if (matcher.matches()) {
                    // discord sucks
                    if (msg.startsWith("https://tenor.com")) {
                        String[] parts = msg.split("\\.");
                        if (!parts[parts.length-1].startsWith(".gif")) {
                            msg += ".gif";
                        }
                    }
                    LiveChat.getSocketServer().sendMessageToAllClients("url: " + msg);
                } else {
                    LiveChat.getSocketServer().sendMessageToAllClients("text: " + msg);
                }
            }
            if (!message.getAttachments().isEmpty()) {
                LiveChat.getSocketServer().sendMessageToAllClients("url: " + message.getAttachments().get(0).getUrl());
            }
            User user = e.getAuthor();
            String avatar = user.getEffectiveAvatarUrl();
            LiveChat.getSocketServer().sendMessageToAllClients("discord: " + user.getEffectiveName().replace('-', ' ') + "-" + avatar);
        }
    }
}