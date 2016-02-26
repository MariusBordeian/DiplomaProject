package com.Serial;

import com.Coordinates;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by pi on 14/11/15.
 */
public class Communicator implements Runnable{

   // public static Coordinates coordinates=new Coordinates();
    public static String linie="";
    public static Queue<String> queue=new LinkedList<String>();

    public InputStream in;
    public OutputStream out;
    public Random rand=new Random();
    StringBuilder lineBuffer = new StringBuilder("");

    public Communicator() {
    }

    public void connect( String portName ) throws Exception {
        System.setProperty("gnu.io.rxtx.SerialPorts",portName);
        System.out.println("Trying to connect on : "+ portName);
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



    public void run() { // TO DO explicist restart thread when serial is broken
        byte[] buffer = new byte[ 1024 ];
        boolean precessed = false;
        int len = -1;

        try {
            while( ( len = this.in.read( buffer ) ) > -1 ) {
                lineBuffer.append(new String(buffer,0,len));
                if(lineBuffer.indexOf("\n") > -1){
                    linie=lineBuffer.toString();
                    // to do : match with previous line
                    if(linie.length() > 1 && !linie.contains("@")){
                        System.out.println("Am primit : "+linie);
                    }
                    if(!queue.isEmpty()){
                        String toSend=queue.remove();
                        System.out.println("Am trimis : "+toSend);
                        byte[] bytes=toSend.getBytes();
                        out.write(bytes);
                        precessed = true;
                    }
                    else if (precessed) {
                        String toSend="done";
                        byte[] bytes=toSend.getBytes();
                        out.write(bytes);
                        precessed = false;
                    }
                    lineBuffer = new StringBuilder("");
                }
            }
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
