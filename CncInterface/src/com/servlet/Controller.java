package com.servlet;

import com.Serial.Communicator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Andrei on 11/5/2015.
 */

public class Controller extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String load = request.getParameter("load");
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        if (load == null) {
            request.getRequestDispatcher("/jsp/gui.jsp").forward(request, response);
        } else {
            if(action!=null) {
                if (action.equals("getSpindlePosition")) {
                    //String coordinatesJSON="{\"X\":"+ Communicator.coordinates.getX()+",\"Y\":"+Communicator.coordinates.getY()+",\"Z\":"+Communicator.coordinates.getZ()+"}";
                    String linie=Communicator.linie;
                    out.print(linie);

                    //out.print(linie);
                    // get position of the spindle
                } else if (action.equals("alterPosition")) {
                    String axis = request.getParameter("axis");
                    String direction = request.getParameter("dir");
                    String currX=request.getParameter("currX");
                    String currY=request.getParameter("currY");
                    String currZ=request.getParameter("currZ");
                    System.out.println(axis);
                    System.out.println(direction);
                    System.out.println(currX);
                    System.out.println(currY);
                    System.out.println(currZ);

                    String commandLine="";

                    if(axis.equals("X")){
                        if(direction.equals("plus")){
                            commandLine=(Float.parseFloat(currX)+1)+"#"+currY+"\n";
                        }else{
                            commandLine=(Float.parseFloat(currX)-1)+"#"+currY+"\n";
                        };
                    }else if(axis.equals("Y")){
                        if(direction.equals("plus")){
                            commandLine=currX+"#"+(Float.parseFloat(currY)+1)+"\n";
                        }else{
                            commandLine=currX+"#"+(Float.parseFloat(currY)-1)+"\n";
                        };
                    }else if(axis.equals("Z")){
                        if(direction.equals("plus")){
                            commandLine=(Float.parseFloat(currZ)+1)+"\n";
                        }else{
                            commandLine=(Float.parseFloat(currZ)-1)+"\n";
                        };
                    }
                    //System.out.println(commandLine);
                    Communicator.queue.add(commandLine);
                    //System.out.println(Communicator.queue.peek());// 10#10
                    // alter position of the spindle
                }
            }
        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
