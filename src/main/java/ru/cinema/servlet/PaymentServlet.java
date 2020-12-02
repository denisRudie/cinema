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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class PaymentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        resp.setCharacterEncoding("UTF-8");
        List<Seat> seats = (List<Seat>) session.getAttribute("chosenSeats");
        String jsonOut = new Gson().toJson(seats);
        try (PrintWriter writer = resp.getWriter()) {
//            JSONArray ja = new JSONArray();
//            for (Map.Entry<Seat, String> seat : seats.entrySet()) {
//                JSONObject obj = new JSONObject();
//                obj.put("id", seat.getKey().getId());
//                obj.put("price", seat.getKey().getPrice());
//                obj.put("desc", seat.getValue());
//                ja.add(obj);
//            }
            writer.write(jsonOut);
            writer.flush();
        }
    }

    //TODO реализовать считываение имени и телефона, при нажатии кнопки сохранять пользователя в
    // бд. Связывать сессию с этим пользователем. Добавить Listener, который будет уничтожать
    // записи в бд, относящиеся к этому пользователю при протухании сессии.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        PsqlStore.instOf().setSetSeatOwner(1);
        resp.sendRedirect(req.getContextPath() + "/hall");
    }
}
