package com.servlet;

import com.Serial.Communicator;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Andre on 09-Jun-16.
 */
public class WSocket extends WebSocketServlet {
    //    private static ArrayList<MyMessageInbound> clients = new ArrayList<MyMessageInbound>();
    private static HashMap<Long,Thread> links=new HashMap<>();


    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest httpServletRequest) {
        String id=httpServletRequest.getParameter("id");
        //System.out.println(id);
        return new MyMessageInbound(Long.parseLong(id));
    }

    private class MyMessageInbound extends MessageInbound {
        WsOutbound myoutbound;
        long sessionId=-1;

        public MyMessageInbound(long sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void onOpen(WsOutbound outbound) {
            this.myoutbound = outbound;
 /*           System.out.println("Open Client.");
            this.myoutbound = outbound;
            clients.add(this);
            try {
                myoutbound.writeTextMessage(CharBuffer.wrap("connected"));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(sessionId+" connected.");
                    String oldLine=Communicator.linie + "#" + (Communicator.spindleRunning ? "on" : "off") + "#" + (Communicator.queueIsEmpty ? "off" : "on");
                    String newLine="";
                    Boolean sentFlag=false;
                    if(oldLine.length()>0){
                        try {
                            myoutbound.writeTextMessage(CharBuffer.wrap(oldLine));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    while(true) {
                        if( sessionId==Communicator.sessionId || Communicator.sessionId==0) {
                            newLine = Communicator.linie + "#" + (Communicator.spindleRunning ? "on" : "off") + "#" + (Communicator.queueIsEmpty ? "off" : "on");
                            if (!oldLine.equals(newLine)) {
                                if (newLine.length() > 0) {
                                    try {
                                        myoutbound.writeTextMessage(CharBuffer.wrap(newLine));
                                        myoutbound.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                oldLine = newLine;

                            }
                            sentFlag=false;
                        }else{
                            if(!sentFlag){
                                try {
                                    myoutbound.writeTextMessage(CharBuffer.wrap("device in use"));
                                    sentFlag=true;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
            t.start();
            if(links.containsKey(sessionId)){
                System.out.println("Already exists :" + sessionId);
            }else{
                links.put(sessionId,t);
            }

        }

        @Override
        public void onClose(int status) {
            links.get(sessionId).stop();
            System.out.println(sessionId+ " left.");
            links.remove(this.sessionId);
        }

        @Override
        public void onTextMessage(CharBuffer cb) throws IOException {
            System.out.println(cb);
        }

        @Override
        public void onBinaryMessage(ByteBuffer bb) throws IOException {
        }
    }
}
