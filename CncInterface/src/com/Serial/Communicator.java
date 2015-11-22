package com.Serial;

import com.Coordinates;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by pi on 14/11/15.
 */
public class Communicator implements Runnable{

    public static Coordinates coordinates=new Coordinates();

    public InputStream in;
    public OutputStream out;
    String line="";

    public Communicator() {
    }

    public void connect( String portName ) throws Exception {
        System.setProperty("gnu.io.rxtx.SerialPorts",portName);

        CommPortIdentifier portIdentifier = CommPortIdentifier
                .getPortIdentifier( portName );
        if( portIdentifier.isCurrentlyOwned() ) {
            System.out.println( "Error: Port is currently in use" );
        } else {
            int timeout = 2000;
            CommPort commPort = portIdentifier.open( this.getClass().getName(), timeout );

            if( commPort instanceof SerialPort) {
                SerialPort serialPort = ( SerialPort )commPort;

                serialPort.setSerialPortParams( 115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();

                ( new Thread( this ) ).start();
            } else {
                System.out.println( "Error: Only serial ports are handled by this example." );
            }
        }
    }



    public void run() {
        byte[] buffer = new byte[ 1024 ];
        int len = -1;
        try {
            while( ( len = this.in.read( buffer ) ) > -1 ) {
                line+=new String( buffer, 0, len );
                if(line.contains("\n")){
                    System.out.println(line);
                    // String []coordinateElements=line.split("#");
                    //coordinates.setX(Float.parseFloat(coordinateElements[0]));
                    //coordinates.setY(Float.parseFloat(coordinateElements[1]));
                    // coordinates.setZ(Float.parseFloat(coordinateElements[2]));

                    coordinates.setX(new Random().nextFloat()*10);
                    coordinates.setY(new Random().nextFloat()*10);
                    coordinates.setZ(new Random().nextFloat()*10);

                    String coords="0.1#0.2#0.3\n";

                    byte[] bytes=coords.getBytes();
                    out.write(bytes);
                    line="";
                }
            }
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
