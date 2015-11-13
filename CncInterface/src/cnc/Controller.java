package cnc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Andrei Ostafciuc on 05-Nov-15.
 */
public class Controller extends HttpServlet {
    public static float x=0;
    public static float y=0;
    public static float z=0;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String load = request.getParameter("load");
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        if (load == null) {
            request.getRequestDispatcher("/jsp/gui.jsp").forward(request, response);
        } else {
            if(action!=null) {
                if (action.equals("getSpindlePosition")) {
                    HashMap<String,Float> map=getPosition();
                    String coordinatesJSON="{\"X\":"+map.get("X")+",\"Y\":"+map.get("Y")+",\"Z\":"+map.get("Z")+"}";
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
    public HashMap<String,Float> getPosition(){
        HashMap<String,Float> map=new HashMap<String, Float>();
        Random random=new Random();

        map.put("X",x--);
        map.put("Y",y--);
        map.put("Z",z--);
        return map;
    }
}

