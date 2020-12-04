package ru.cinema.servlet;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.cinema.model.Seat;
import ru.cinema.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet(urlPatterns = "/hall")
public class HallServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        Collection<Seat> allSeats = PsqlStore.instOf().getAllSeats();
        String jsonOut = new Gson().toJson(allSeats);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(jsonOut);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (BufferedReader read = req.getReader()) {
            StringBuilder fullLine = new StringBuilder();
            String oneLine;
            while ((oneLine = read.readLine()) != null) {
                fullLine.append(oneLine);
            }
            JSONArray json = (JSONArray) new JSONParser().parse(fullLine.toString());

            HttpSession session = req.getSession();
            List<Seat> seats = new ArrayList<>();
            for (Object o : json) {
                JSONObject j = (JSONObject) o;
                int sId = Integer.parseInt(j.get("id").toString());
                PsqlStore.instOf().tempSeatOwner(sId, -1);
                Seat seat = PsqlStore.instOf().getSeatById(sId);
                seats.add(seat);
            }
            session.setAttribute("chosenSeats", seats);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
