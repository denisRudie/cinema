package ru.cinema.servlet;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.cinema.model.Account;
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
import java.util.List;

public class PaymentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        resp.setCharacterEncoding("UTF-8");
        List<Seat> seats = (List<Seat>) session.getAttribute("chosenSeats");
        String jsonOut = new Gson().toJson(seats);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(jsonOut);
            writer.flush();
        }
    }

    //TODO реализовать считываение имени и телефона, при нажатии кнопки сохранять пользователя в
    // бд. Связывать сессию с этим пользователем. Добавить Listener, который будет уничтожать
    // записи в бд, относящиеся к этому пользователю при протухании сессии.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Account acc = new Account();
        try (BufferedReader read = req.getReader()) {
            StringBuilder fullLine = new StringBuilder();
            String oneLine;
            while ((oneLine = read.readLine()) != null) {
                fullLine.append(oneLine);
            }
            JSONObject json = (JSONObject) new JSONParser().parse(fullLine.toString());

            String name = json.get("name").toString();
            String email = json.get("email").toString();
            acc.setName(name);
            acc.setEmail(email);
            int id = PsqlStore.instOf().addAcc(acc);
            acc.setId(id);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        HttpSession session = req.getSession();
        List<Seat> seats = (List<Seat>) session.getAttribute("chosenSeats");
        for (Seat seat : seats) {
            PsqlStore.instOf().tempSeatOwner(seat.getId(), acc.getId());
        }

        PsqlStore.instOf().setSetSeatOwner(acc.getId());
        resp.sendRedirect(req.getContextPath() + "/hall");
    }
}
