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
            String zSafe = request.getParameter("zSafe");     //      "Z above all obstacles"
            String zDepth = request.getParameter("zDepth");       //      "Z depth of cut"
            String zStep = request.getParameter("zStep");         //      "Z step of cutting"
            String zSurface = request.getParameter("zSurface");   //      "Z of the surface"
            String toolDiameter = request.getParameter("toolDiameter");
            PrintWriter writer = new PrintWriter(receivedFilePath + "/"+filename+".svg", "UTF-8");
            writer.print(svgFileContent);
            writer.close();


            String[] execCommand = new String[] {"python", gcodeTools ,
                    "-f", filename + ".ngc",
                    "-d", receivedFilePath ,
                    "--tool-diameter", toolDiameter,
                    "--Zsafe", zSafe,
                    "--Zsurface", zSurface,
                    "--Zdepth", zDepth,
                    "--Zstep", zStep,
                     receivedFilePath + "/" + filename + ".svg",
                    ">>",receivedFilePath+"/gcodeToolsErrors.log" };
            String testCmd="python /home/pi/Desktop/DiplomaProject/gcodetools/gcodetools.py -f receivedFile.ngc -d /home/pi/Desktop/SVG2GCode --tool-diameter 5 --Zsafe 30 --Zsurface 3  --Zdepth 1  --Zstep 2 /home/pi/Desktop/SVG2GCode/receivedFile.svg >> /home/pi/Desktop/SVG2GCode/receivedFile.log";
            /*
            PrintWriter pipe = new PrintWriter(receivedFilePath + "/svg", "UTF-8");
            pipe.print(execStr);
            pipe.close();
            */
//            python /home/pi/Desktop/DiplomaProject/gcodetools/gcodetools.py -f receivedFile.ngc -d /home/pi/Desktop/SVG2GCode --tool-diameter 5 --Zsafe 30 --Zsurface 3  --Zdepth 1  --Zstep 2 /home/pi/Desktop/SVG2GCode/receivedFile.svg
            //Process p = Runtime.getRuntime().exec(execStr);

            ProcessBuilder pp1=new ProcessBuilder("rm -f "+ receivedFilePath + "/"+filename+".ngc > /home/pi/Desktop/SVG2GCode/receivedFile.log");
            pp1.redirectErrorStream(true);
            Process p1 =pp1.start();

            try {
                p1.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ProcessBuilder pp2=new ProcessBuilder(testCmd);
            pp2.redirectErrorStream(true);
            Process p2 =pp2.start();

            try {
                p2.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
            File f = new File(receivedFilePath + "/gcode");
            while (!f.exists() || !f.isFile()) {}
            f.delete();
            */

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
        } else if (action != null && action.equals("stopCnc")) {
            Communicator.queue.clear();
        }
    }
}
