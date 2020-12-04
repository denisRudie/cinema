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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class UpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean status = true;
        try (BufferedReader read = req.getReader();
             Writer writer = resp.getWriter()) {
            StringBuilder fullLine = new StringBuilder();
            String oneLine;
            while ((oneLine = read.readLine()) != null) {
                fullLine.append(oneLine);
            }
            JSONArray json = (JSONArray) new JSONParser().parse(fullLine.toString());

            for (Object o : json) {
                JSONObject j = (JSONObject) o;
                int sId = Integer.parseInt(j.get("id").toString());
                Seat seat = PsqlStore.instOf().getSeatById(sId);
                if (seat.getAccountId() != 0) {
                    status = false;
                }
            }
            writer.write(new Gson().toJson(status));
            writer.flush();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
