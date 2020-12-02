package ru.cinema.servlet;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.cinema.model.Seat;
import ru.cinema.store.TestStore;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet (urlPatterns = "/test", asyncSupported = true)
public class TestServlet extends HttpServlet {
    TestAsyncTasksService asyncTasksService = TestAsyncTasksService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action.equals("new")) {
            resp.setCharacterEncoding("UTF-8");
            Collection<Seat> allSeats = TestStore.getInst().getAllSeats();
            String jsonOut = new Gson().toJson(allSeats);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(jsonOut);
                writer.flush();
            }
        } else if (action.equals("update")) {
            asyncTasksService.addContext(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (BufferedReader reader = req.getReader()) {
            StringBuilder fullLine = new StringBuilder();
            String oneLine;
            while ((oneLine = reader.readLine()) != null) {
                fullLine.append(oneLine);
            }
            JSONArray json = (JSONArray) new JSONParser().parse(fullLine.toString());

            List<Seat> bookedSeats = new ArrayList<>();
            for (Object o : json) {
                JSONObject j = (JSONObject) o;
                int sId = Integer.parseInt(j.get("id").toString());
                TestStore.getInst().setSeatOwner(sId, 213);
                bookedSeats.add(TestStore.getInst().getSeatById(sId));
            }

            String jsonOut = new Gson().toJson(bookedSeats);
            asyncTasksService.completeContexts(jsonOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
