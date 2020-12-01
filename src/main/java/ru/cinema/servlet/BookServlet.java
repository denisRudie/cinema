package ru.cinema.servlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.cinema.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class BookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (BufferedReader read = req.getReader()) {
            StringBuilder fullLine = new StringBuilder();
            String oneLine;
            while ((oneLine = read.readLine()) != null) {
                fullLine.append(oneLine);
            }
            JSONArray json = (JSONArray) new JSONParser().parse(fullLine.toString());
            for (Object o : json) {
                JSONObject j = (JSONObject) o;
                int sId = Integer.parseInt(j.get("id").toString());
                PsqlStore.instOf().setSeatOwner(sId, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/hall");
    }
}
