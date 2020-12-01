package ru.cinema.servlet;

import com.google.gson.Gson;
import ru.cinema.model.Seat;
import ru.cinema.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class HallServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("index.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {
            resp.setCharacterEncoding("UTF-8");
            Collection<Seat> allSeats = PsqlStore.instOf().getAllSeats();
            String jsonOut = new Gson().toJson(allSeats);
            out.write(jsonOut);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
