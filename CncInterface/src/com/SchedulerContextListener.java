package com;
/**
 * Created by Andrei on 11/8/2015.
 */
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SchedulerContextListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    private Scheduler scheduler;
    // Public constructor is required by servlet spec
    public SchedulerContextListener() {
    }


    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.scheduler = new Scheduler();
        scheduler.startThread();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
       this.scheduler.stopThread();
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is added to a session.
      */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
    }
}
