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
                    String coordinatesJSON="{\"X\":"+ Communicator.coordinates.getX()+",\"Y\":"+Communicator.coordinates.getY()+",\"Z\":"+Communicator.coordinates.getZ()+"}";
                    out.print(coordinatesJSON);
                    // get position of the spindle
                } else if (action.equals("alterPosition")) {
                    String axis = request.getParameter("axis");
                    String direction = request.getParameter("direction");

                    // alter position of the spindle
                }
            }
        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
