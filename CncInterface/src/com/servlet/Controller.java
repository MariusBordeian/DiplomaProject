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
                String speed = request.getParameter("speed");

                if (action.equals("getSpindlePosition")) {
                    out.print(Communicator.linie + "#" + (Communicator.spindleRunning ? "on" : "off") + "#" + (Communicator.queueIsEmpty ? "off" : "on"));
                } else if (action.equals("toggleSpindle")) {
                    String state = request.getParameter("state");
                    if (state.equals("on")) {
                        Communicator.queue.add("@1\n");
                        Communicator.spindleRunning = true;
                    } else {
                        Communicator.queue.add("@0\n");
                        Communicator.spindleRunning = false;
                    }
                } else if (action.equals("alterPosition")) {
                    String axis = request.getParameter("axis");
                    String direction = request.getParameter("dir");
                    String currX = request.getParameter("currX");
                    String currY = request.getParameter("currY");
                    String currZ = request.getParameter("currZ");
                    String incrementScale = request.getParameter("incrementScale");

                    Float incrementScaleInt;
                    try {
                        incrementScaleInt = Float.parseFloat(incrementScale);
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
                    Communicator.queue.add(speed + "#" + "0.0#0.0\n");
                    Communicator.queue.add(speed + "#" + "0.0\n");
                } else if (action.equals("setCoords")) {
                    String newX = request.getParameter("newX");
                    String newY = request.getParameter("newY");
                    String newZ = request.getParameter("newZ");

                    Communicator.queue.add(speed + "#" + newX + "#" + newY + "#" + newZ + "\n");
                }

            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = properties.getProperty("fileName");
        String gcodeTools = properties.getProperty("gcodeTools");
        String action = request.getParameter("action");
        if (action != null && action.equals("sendToCnc")) {
            Communicator.queue.addAll(Arrays.asList(request.getParameter("data").split(",")));
        } else if (action != null && action.equals("getGCodeFromSVGFile")) {
            String svgFileContent = request.getParameter("data");
            String zSafe = request.getParameter("zSafe");     			//      "Z above all obstacles"
            String zDepth = request.getParameter("zDepth");       		//      "Z depth of cut"
            String zStep = request.getParameter("zStep");         		//      "Z step of cutting"
            String zSurface = request.getParameter("zSurface");   		//      "Z of the surface"
            String toolDiameter = request.getParameter("toolDiameter");

            File f1 = new File(receivedFilePath + "/" + filename + ".svg");
            if (f1.exists() && f1.isFile()) {
            	f1.delete();
        	}

        	File f2 = new File(receivedFilePath + "/" + filename + ".ngc");
            if (f2.exists() && f2.isFile()) {
            	f2.delete();
        	}

        	PrintWriter writer1 = new PrintWriter(receivedFilePath + "/"+filename+".svg", "UTF-8");
            writer1.print(svgFileContent);															// write received svg to file
            writer1.close();

            String execCommand = gcodeTools +
                    " -f " + filename + ".ngc" + 
                    " -d " + receivedFilePath  + 
                    " --tool-diameter " + toolDiameter + 
                    " --Zsafe " + zSafe + 
                    " --Zsurface " + zSurface + 
                    " --Zdepth " + zDepth +
                    " --Zstep " + zStep + 
                    " " + receivedFilePath + "/" + filename + ".svg" + 
                    " 2> " + receivedFilePath + "/" + filename + ".log";							// create the gcodetools.py command
			System.out.println(execCommand);

			PrintWriter writer2 = new PrintWriter(receivedFilePath + "/execCommand", "UTF-8");
            writer2.print(execCommand);																// write command for gcodetools_listener.py
            writer2.close();            
		
			PrintWriter writer3 = new PrintWriter(receivedFilePath + "/SVGReceived", "UTF-8");		// create "alert" file for gcodetools_listener.py
            writer3.close();            

            File d1 = new File(receivedFilePath + "/gcodeDone");
            while (!d1.exists()) {}
            d1.delete();
            
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

            File f3 = new File(receivedFilePath + "/execCommand");
            if (f3.exists() && f3.isFile()) {
            	f3.delete();
        	}

            File f4 = new File(receivedFilePath + "/SVGReceived");
            if (f4.exists() && f4.isFile()) {
            	f4.delete();
        	}
        } else if (action != null && action.equals("stopCnc")) {
            Communicator.queue.clear();
        }
    }
}
