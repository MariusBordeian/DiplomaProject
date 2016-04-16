package com.servlet;

import com.Serial.Communicator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class Controller extends HttpServlet {
    Boolean spindleRunning = false;
    String receivedFilePath = "";
    Properties properties = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (properties == null) {
            properties = new Properties();
            FileInputStream input = new FileInputStream(request.getServletContext().getRealPath("/config/config.properties"));
            properties.load(input);
        }
        receivedFilePath = properties.getProperty("receivedFilePath");

        String load = request.getParameter("load");
        String action = request.getParameter("action");

        PrintWriter out = response.getWriter();
        if (load == null) {
            request.getRequestDispatcher("/jsp/gui.jsp").forward(request, response);
        } else {
            if (action != null) {
                if (action.equals("getSpindlePosition")) {
                    out.print(Communicator.linie + "#" + (spindleRunning ? "on" : "off") + "#" + (Communicator.queueIsEmpty ? "off" : "on"));
                } else if (action.equals("toggleSpindle")) {
                    String state = request.getParameter("state");
                    if (state.equals("on")) {
                        Communicator.queue.add("@1\n");
                        spindleRunning = true;
                    } else {
                        Communicator.queue.add("@0\n");
                        spindleRunning = false;
                    }
                } else if (action.equals("alterPosition")) {
                    String axis = request.getParameter("axis");
                    String direction = request.getParameter("dir");
                    String currX = request.getParameter("currX");
                    String currY = request.getParameter("currY");
                    String currZ = request.getParameter("currZ");
                    String incrementScale = request.getParameter("incrementScale");
                    String speed = request.getParameter("speed");
                    Integer incrementScaleInt;
                    try {
                        incrementScaleInt = Integer.parseInt(incrementScale);
                        String commandLine = "";

                        if (axis.equals("X")) {
                            if (direction.equals("plus")) {
                                commandLine = speed + "#" + (Float.parseFloat(currX) + incrementScaleInt) + "#" + currY + "\n";
                            } else {
                                commandLine = speed + "#" + (Float.parseFloat(currX) - incrementScaleInt) + "#" + currY + "\n";
                            }
                        } else if (axis.equals("Y")) {
                            if (direction.equals("plus")) {
                                commandLine = speed + "#" + currX + "#" + (Float.parseFloat(currY) + incrementScaleInt) + "\n";
                            } else {
                                commandLine = speed + "#" + currX + "#" + (Float.parseFloat(currY) - incrementScaleInt) + "\n";
                            }
                        } else if (axis.equals("Z")) {
                            if (direction.equals("plus")) {
                                commandLine = speed + "#" + (Float.parseFloat(currZ) + incrementScaleInt) + "\n";
                            } else {
                                commandLine = speed + "#" + (Float.parseFloat(currZ) - incrementScaleInt) + "\n";
                            }
                        }
                        Communicator.queue.add(commandLine);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (action.equals("zeroMachine")) {
                    String speed = request.getParameter("speed");
                    Communicator.queue.add(speed + "#" + "0.0\n");
                    Communicator.queue.add(speed + "#" + "0.0#0.0\n");
                }

            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = properties.getProperty("fileName");
        String gcodeTools = properties.getProperty("gcodeTools");
        String action = request.getParameter("action");
        if (action.equals("sendToCnc")) {
            Communicator.queue.addAll(Arrays.asList(request.getParameter("data").split(",")));
        } else if (action.equals("getGCodeFromSVGFile")) {
            String svgFileContent = request.getParameter("data");
            String zSafe = request.getParameter("zSafe");     //      "Z above all obstacles"
            String zDepth = request.getParameter("zDepth");       //      "Z depth of cut"
            String zStep = request.getParameter("zStep");         //      "Z step of cutting"
            String zSurface = request.getParameter("zSurface");   //      "Z of the surface"
            String toolDiameter = request.getParameter("toolDiameter");
            PrintWriter writer = new PrintWriter(receivedFilePath + "/"+filename+".svg", "UTF-8");
            writer.print(svgFileContent);
            writer.close();
            Process p = Runtime.getRuntime().exec("python \"" + gcodeTools + "\" -f " + filename + ".ngc -d \"" + receivedFilePath + "\" --tool-diameter " + toolDiameter
                    + " --Zsafe " + zSafe
                    + " --Zsurface " + zSurface
                    + " --Zdepth " + zDepth
                    + " --Zstep " + zStep +
                    "  \"" + receivedFilePath + "/" + filename + ".svg\"");
            while (p.isAlive()) {
            }

            BufferedReader br;
            StringBuilder gcodeContent = new StringBuilder();
            try {
                FileReader fr = new FileReader(receivedFilePath + "/" + filename + ".ngc");
                br = new BufferedReader(fr);
                try {
                    String x;
                    while ((x = br.readLine()) != null) {
                        gcodeContent.append(x).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fr.close();
            } catch (FileNotFoundException e) {
                System.out.println(e);
                e.printStackTrace();
            }

            response.getWriter().write(gcodeContent.toString());
        } else if (action.equals("stopCnc")) {
            Communicator.queue.clear();
        }
    }
}
