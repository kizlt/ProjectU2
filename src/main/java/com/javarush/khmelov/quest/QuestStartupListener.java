package com.javarush.khmelov.quest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class QuestStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        int scenes = QuestGraph.all().size();
        event.getServletContext().log("«Дорога домой»: загружено сцен — " + scenes);
    }
}
