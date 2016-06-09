package com.Serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pi on 14/11/15.
 */
public class Communicator implements Runnable{

    // public static Coordinates coordinates=new Coordinates();
    public static volatile Boolean spindleRunning = false;
    public static volatile String linie="";
    public static volatile Boolean queueIsEmpty=true;
    public static ConcurrentLinkedQueue<String> queue=new ConcurrentLinkedQueue<String>();
    SerialPort serialPort;

    public InputStream in;
    public OutputStream out;
    public Random rand=new Random();
    CharSequence lineBuffer = new StringBuffer();

    public Communicator() {
    }

    public void run() { // TO DO explicist restart thread when serial is broken
        BufferedReader br=new BufferedReader(new InputStreamReader(this.in));
        handShake(br);
        System.out.println("Am facut handshake ! ---------------------------------------------------------");
        try {
            while(br.ready()){
                br.read();
            }
        } catch (IOException e) {
        }
        System.out.println("Teoretic am golit bufferul  ! ---------------------------------------------------------");
        String currentLine;
        while(true){
	    queueIsEmpty=queue.isEmpty();
            if(!queueIsEmpty){
                System.out.println("Sunt pe cale sa trimit "+queue.size()+" "+queue.peek()+"  ! ---------------------------------------------------------");
                try {
                    out.write(queue.remove().getBytes());
		    //queueIsEmpty=queue.isEmpty();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                waitForResponse(br);
            }
        }
    }
    public void waitForResponse(BufferedReader br){
        System.out.println("Astept sa primesc ceva ! ---------------------------------------------------------");
        String line="";
        while(true){
            try {
                if(br.ready()) {
                    line = br.readLine();
                }
            } catch (IOException e) {

            }
            if(!line.isEmpty() &&  line.contains("#")){
                System.out.println(linie);
                linie=line;
                break;
            }
        }
    }
    public void handShake(BufferedReader br){
        String line="";
        String patternString = "\\d+.\\d+#\\d+.\\d+#\\d+.\\d+";
        Pattern pattern = Pattern.compile(patternString);
        while(true){
            try {
                if(br.ready()){
                    line = br.readLine();
                }
            } catch (IOException e) {
            }
            Matcher matcher = pattern.matcher(line);
            if(matcher.matches()){
                try {
                    linie=line;
                    out.write("HandShake\n".getBytes());
                    break;
                } catch (IOException e) {
                }
            }
        }
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
                serialPort = ( SerialPort )commPort;

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




}
