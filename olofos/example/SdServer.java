package olofos.example;

import java.io.*;
import java.net.*;

import java.nio.file.*;

import olofos.sdemu.SdEmu;
import olofos.sdemu.SdEmuDataInterface;

class SdServerThread extends Thread {
    private Socket socket;
    private SdEmu sd;
    private SdEmuDataInterface data;
    private String diskImage;

    public SdServerThread(Socket socket, String diskImage) throws IOException {
        this.socket = socket;
        this.diskImage = diskImage;

        Path path = Paths.get(diskImage);
        byte[] values = Files.readAllBytes(path);
        data = new SdEmuDataArray(values);
        sd = new SdEmu(data);
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String line;
            boolean done = false;

            while (!done) {
                line = reader.readLine();
                String[] inArray = line.split(" ", 2);

                switch (inArray[0]) {
                    case "QUIT": {
                        System.out.println("Quit");
                        System.out.println("Saving disk image");
                        Path path = Paths.get(diskImage + "-new.img");

                        byte[] rawData = new byte[data.getSize()];
                        for (int i = 0; i < data.getSize(); i++) {
                            rawData[i] = (byte) data.getValue(i);
                        }

                        Files.write(path, rawData, StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING);
                        done = true;
                        break;
                    }

                    case "CS":
                        if (inArray[1].equals("ASSERT")) {
                            sd.assertCS();
                        } else {
                            sd.deassertCS();
                        }
                        break;

                    case "XFER":
                        int value = Integer.parseInt(inArray[1], 16);
                        int result = sd.transferByte(value);
                        writer.println(String.format("%02X", result));
                        break;
                }
            }

            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

public class SdServer {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java SdServer disk.img [port]");
            System.exit(1);
        }
        String diskImage = args[0];

        int port = 6868;

        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            Socket socket = serverSocket.accept();
            System.out.println("Client connected");

            SdServerThread thread = new SdServerThread(socket, diskImage);
            thread.start();
            thread.join();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
