package ru.cinema.servlet;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.cinema.model.Seat;
import ru.cinema.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class PaymentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Map<Seat, String> seats = (HashMap<Seat, String>) session.getAttribute("chosenSeats");
        try (PrintWriter writer = resp.getWriter()) {
            JSONArray ja = new JSONArray();
            for (Map.Entry<Seat, String> seat : seats.entrySet()) {
                JSONObject obj = new JSONObject();
                obj.put("id", seat.getKey().getId());
                obj.put("price", seat.getKey().getPrice());
                obj.put("desc", seat.getValue());
                ja.add(obj);
            }
            String jsonOut = new Gson().toJson(ja);
            writer.write(jsonOut);
            writer.flush();
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
            Map<Seat, String> seats = new HashMap<>();

            for (Object o : json) {
                JSONObject j = (JSONObject) o;
                int sId = Integer.parseInt(j.get("id").toString());
                String desc = j.get("desc").toString();
                seats.put(PsqlStore.instOf().getSeatById(sId), desc);
            }
//            seats.values().forEach(System.out::println);
            HttpSession session = req.getSession();
//            System.out.println(session.getId());
            session.setAttribute("chosenSeats", seats);
            resp.sendRedirect("payment.html");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
