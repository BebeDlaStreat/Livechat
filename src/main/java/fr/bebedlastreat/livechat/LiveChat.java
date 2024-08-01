package fr.bebedlastreat.livechat;

import lombok.Data;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

@Data
public class LiveChat {

    @Getter
    private static DiscordBot discordBot;
    @Getter
    private static SocketServer socketServer;

    public static void cloneFileFromResources(String resourcePath, File destinationFile) throws IOException {
        // Get the resource as an InputStream
        InputStream resourceStream = LiveChat.class.getClassLoader().getResourceAsStream(resourcePath);

        if (resourceStream == null) {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }

        // Create the destination file if it doesn't exist
        if (!destinationFile.exists()) {
            if (destinationFile.getParentFile() != null) {
                destinationFile.getParentFile().mkdirs();
            }
            destinationFile.createNewFile();
        }

        // Copy the resource to the destination file
        try (OutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            resourceStream.close();
        }
    }

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        File file = new File("config.yml");
        if (!file.exists()) {
            try {
                cloneFileFromResources("config.yml", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Map<String, Object> data;
        try {
            data = yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        int port = (int) data.get("port");
        socketServer = new SocketServer(new InetSocketAddress(port));
        socketServer.start();
        System.out.println("WebSocket server started on port " + port);
        try {
            discordBot = new DiscordBot((String) data.get("token"),
                    (String) data.get("guild"), (String) data.get("channel"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
