package fr.bebedlastreat.livechat;

import fr.bebedlastreat.livechat.discord.MessageListener;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Data
public class DiscordBot {

    @Getter
    private static DiscordBot instance;

    private final String token;
    private final String guildId;
    private final String channelId;
    private final JDA jda;
    private final Guild guild;
    private final TextChannel channel;

    public DiscordBot(String token, String guildId, String channelId) throws InterruptedException  {
        this.token = token;
        this.guildId = guildId;
        this.channelId = channelId;

        instance = this;

        this.jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.DEFAULT)
                .setAutoReconnect(true)
                .addEventListeners(new MessageListener(this))
                .build();
        jda.awaitReady();
        guild = jda.getGuildById(guildId);
        channel = guild.getTextChannelById(channelId);
        System.out.println("Bot on");
        //channel.sendMessage("Salut").queue();
    }
}
