package ru.cinema.services;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AsyncTasksService {
    private static final AsyncTasksService INST = new AsyncTasksService();
    private final List<AsyncContext> contexts = new LinkedList<>();

    private AsyncTasksService() {

    }

    public static AsyncTasksService getInstance() {
        return INST;
    }

    public void addContext(HttpServletRequest req, HttpServletResponse resp) {
        final AsyncContext asyncContext = req.startAsync(req, resp);
        asyncContext.setTimeout(30 * 1_000 * 60);
        contexts.add(asyncContext);
    }

    public void completeContexts(String jsonMsg) {
        List<AsyncContext> list = new ArrayList<>(contexts);
        contexts.clear();

        for (AsyncContext asyncContext : list) {
            try {
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try (PrintWriter writer = response.getWriter()) {
                    writer.write(jsonMsg);
                    writer.flush();
                }
                asyncContext.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int size() {
        return contexts.size();
    }
}
