package olofos.example;

import olofos.sdemu.SdEmuDataInterface;
import olofos.sdemu.SdEmuSpi;

public class Spi {

    public static int transfer(SdEmuSpi sd, int value) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            sd.write(0, (value >> (7 - i)) & 1, 0);
            int b = sd.write(1, (value >> (7 - i)) & 1, 0);
            result = (result << 1) | b;
        }
        return result;
    }

    public static int transfer(SdEmuSpi sd, int... values) {
        int result = 0;
        for (int i = 0; i < values.length; i++) {
            result = transfer(sd, values[i]);
        }
        return result;
    }

    public static int wait(SdEmuSpi sd) {
        int res;
        do {
            res = transfer(sd, 0xFF);
        } while (res == 0xFF);
        return res;
    }

    public static void waitFF(SdEmuSpi sd) {
        int res;
        do {
            res = transfer(sd, 0xFF);
        } while (res != 0xFF);
    }

    public static void main(String args[]) {
        int[] memory = new int[512 * 8];
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 'A' + (i % 16);
        }
        SdEmuDataInterface data = new SdEmuDataArray(memory);
        SdEmuSpi sd = new SdEmuSpi(data);

        for (int i = 0; i < 80; i++) {
            sd.write(0, 1, 1);
            sd.write(1, 1, 1);
        }

        transfer(sd, 0x40, 0x00, 0x00, 0x00, 0x00, 0x95);
        wait(sd);
        waitFF(sd);
        transfer(sd, 0x48, 0x00, 0x00, 0x01, 0xAA, 0x87);
        wait(sd);
        transfer(sd, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF);
        waitFF(sd);
        transfer(sd, 0x77, 0x00, 0x00, 0x00, 0x00, 0x01);
        wait(sd);
        waitFF(sd);
        transfer(sd, 0x69, 0x40, 0x00, 0x00, 0x00, 0x01);
        wait(sd);
        waitFF(sd);
        transfer(sd, 0x7A, 0x00, 0x00, 0x00, 0x00, 0x01);
        wait(sd);

        transfer(sd, 0x51, 0x00, 0x00, 0x00, 0x00, 0x01);
        wait(sd);
        wait(sd);

        for (int i = 0; i < 16; i++) {
            System.out.print((char) transfer(sd, 0xFF));
        }
    }
}
