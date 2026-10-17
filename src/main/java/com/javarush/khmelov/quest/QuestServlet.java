package com.javarush.khmelov.quest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/quest")
public class QuestServlet extends HttpServlet {

    private static final String STATE_ATTR = "questState";
    private static final String VIEW = "/WEB-INF/quest.jsp";
    private static final String PARAM_TAB = "tab";
    private static final String PARAM_NEW = "new";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tab = tabOf(req);
        if (tab == null) {
            resp.sendRedirect(req.getContextPath() + "/quest?" + PARAM_TAB + "=" + UUID.randomUUID());
            return;
        }
        QuestState state = state(req, tab);
        if (req.getParameter(PARAM_NEW) != null) {
            state.reset();
        }
        show(req, resp, state, tab);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String tab = tabOf(req);
        if (tab == null) {
            resp.sendRedirect(req.getContextPath() + "/quest");
            return;
        }
        QuestState state = state(req, tab);
        QuestEngine.step(state, req.getParameter("scene"), parseIndex(req.getParameter("choice")));
        resp.sendRedirect(req.getContextPath() + "/quest?" + PARAM_TAB + "=" + tab);
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, QuestState state, String tab)
            throws ServletException, IOException {
        Scene scene = QuestEngine.current(state);
        req.setAttribute("sceneText", scene.render(state));
        req.setAttribute("sceneId", scene.getId());
        req.setAttribute("tabId", tab);
        req.setAttribute("choices", scene.getChoices(state));
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    private String tabOf(HttpServletRequest req) {
        String raw = req.getParameter(PARAM_TAB);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(raw).toString();
        } catch (IllegalArgumentException notATab) {
            return null;
        }
    }

    private QuestState state(HttpServletRequest req, String tab) {
        HttpSession session = req.getSession();
        Object stored = session.getAttribute(STATE_ATTR);
        Tabs tabs = stored instanceof Tabs existing ? existing : new Tabs();
        session.setAttribute(STATE_ATTR, tabs);
        return tabs.computeIfAbsent(tab, id -> new QuestState());
    }

    private int parseIndex(String value) {
        if (value == null) {
            return -1;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static final class Tabs extends LinkedHashMap<String, QuestState> {

        private static final long serialVersionUID = 1L;
        private static final int MAX_TABS = 8;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, QuestState> eldest) {
            return size() > MAX_TABS;
        }
    }
}
