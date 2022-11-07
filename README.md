# SdEmu - SD card simulator written in Java

SdEmu is a simple SD card simulator written in Java. It supports most of the basic commands of the SD card SPI interface.

## API

There are two interfaces. A byte level interface accessed through the `SdEmu` class and a bit level interface accessed through the `SdEmuSpi` class.


### `SdEmu`

* Constructor `SdEmu(SdEmuDataInterface data)`: `data` is the interface through which any data is read and written
* `int transferByte(int value)`: writes `value` and returns the previous output byte
* `public void clear()`: clear the input and output buffers

### `SdEmuSpi`
* Constructor `public SdEmuSpi(SdEmuDataInterface data)`: `data` is the interface through which any data is read and written
* `int write(int clk, int di, int cs)`: perform a single input step of the simulation. The parameters `clk`, `di` and `cs` correspond to the clock, data in, and chip select input pins of the SD card, and the output gives the value of the `do` output pin.

### `SdEmuDataInterface`

All data access is handled through the `SdEmuDataInterface` interface. The interface has three methods

- `int getValue(int address)`: read the byte at `address`
- `void setValue(int address, int value)`: write `value` to `address`
- `int getSize()`: return the size of the memory in bytes

## Examples

The directory `olofos/examples/` contains two examples: 
* `Spi.java` gives a simple example of how the SPI interface can be used
* `SdServer.java` uses the byte level interface to implement a simple TCP server which listens for commands. 
  
  The `examples/client/` directory contains a C implementation of a TCP client which talks to the TCP server and uses Elm ChaN's [FatFS](http://elm-chan.org/fsw/ff/00index_e.html) library to read and write files to a FAT file system.
  
