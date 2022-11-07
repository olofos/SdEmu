package olofos.example;

import olofos.sdemu.SdEmuDataInterface;

public class SdEmuDataArray implements SdEmuDataInterface {
    private final int[] data;

    public SdEmuDataArray(int size) {
        data = new int[size];
    }

    public SdEmuDataArray(int[] data) {
        this.data = data;
    }

    public SdEmuDataArray(byte[] data) {
        this.data = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = Byte.toUnsignedInt(data[i]);
        }
    }

    @Override
    public void setValue(int address, int value) {
        data[address] = value & 0xFF;
    }

    @Override
    public int getValue(int address) {
        return data[address];
    }

    @Override
    public int getSize() {
        return data.length;
    }
}
