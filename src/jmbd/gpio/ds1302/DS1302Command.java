package jmbd.gpio.ds1302;

public class DS1302Command {

    private static final int WRITE_TO_RAM_CMD = 0b11000000;
    private static final int READ_FROM_RAM_CMD = 0b11000001;

    private static final int WRITE_TO_RTC_CMD = 0b10000000;
    private static final int READ_FROM_RTC_CMD = 0b10000001;

    private final DS1302SerialInterface dS1302SerialInterface;

    public DS1302Command(DS1302SerialInterface dS1302SerialCommunication) {
        this.dS1302SerialInterface = dS1302SerialCommunication;
    }

    public void writeToRam(int reg, int val) {

        int command = WRITE_TO_RAM_CMD | ((reg & 0x1F) << 1);
        writeToDevice(command, val);
    }

    public int readFromRam(int reg) {

        int command = READ_FROM_RAM_CMD | ((reg & 0x1F) << 1);
        return readFromDevice(command);
    }

    public void writeToRtc(int reg, int val) {

        int command = WRITE_TO_RTC_CMD | ((reg & 0x1F) << 1);
        writeToDevice(command, val);
    }

    public int readFromRtc(int reg) {

        int command = READ_FROM_RTC_CMD | ((reg & 0x1F) << 1);
        return readFromDevice(command);
    }

    private void writeToDevice(int command, int data) {

        try {
            dS1302SerialInterface.enableDataTransfer();

            if (dS1302SerialInterface.dataTransferEnabled()) {
                dS1302SerialInterface.clockIn(command);
                dS1302SerialInterface.clockIn(data);
            } else {
                throw new RuntimeException("Serial interface has data transfer disabled");
            }
        } finally {
            dS1302SerialInterface.disableDataTransfer();
        }
    }

    private int readFromDevice(int command) {

        try {
            dS1302SerialInterface.enableDataTransfer();

            if (dS1302SerialInterface.dataTransferEnabled()) {
                dS1302SerialInterface.clockIn(command);
                return dS1302SerialInterface.clockOut();
            }
            throw new RuntimeException("Serial interface has data transfer disabled");
        } finally {
            dS1302SerialInterface.disableDataTransfer();
        }
    }
}
