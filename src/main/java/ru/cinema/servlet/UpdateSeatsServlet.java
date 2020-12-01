package ru.cinema.servlet;

import com.google.gson.Gson;
import ru.cinema.model.Seat;
import ru.cinema.store.PsqlStore;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Async update reservation status of seats in cinema hall.
 */
@WebServlet (urlPatterns = "/update", asyncSupported = true)
public class UpdateSeatsServlet extends HttpServlet {
    List<AsyncContext> contexts = new LinkedList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AsyncContext asyncContext = req.startAsync(req, resp);
        asyncContext.setTimeout(10 * 60 * 1_000);
        contexts.add(asyncContext);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<AsyncContext> asyncContexts = new ArrayList<>(this.contexts);
        this.contexts.clear();

        Collection<Seat> allSeats = PsqlStore.instOf().getAllSeats();
        String jsonOut = new Gson().toJson(allSeats);

        for (AsyncContext asyncContext : asyncContexts) {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter writer = response.getWriter()) {
                writer.write(jsonOut);
                writer.flush();
            }
            asyncContext.complete();
        }
    }
}
