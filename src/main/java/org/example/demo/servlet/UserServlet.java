package org.example.demo.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.UserDAO;
import org.example.demo.model.User;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@WebServlet(name = "userServlet", value = "/api/users")
public class UserServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private UserDAO userDAO;

    @Override
    public void init() {
        this.userDAO = Objects.requireNonNull(
                (UserDAO) getServletContext().getAttribute("userDAO"),
                "UserDAO not initialized in ServletContext"
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            MAPPER.writeValue(response.getWriter(), userDAO.listAll());
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
            return;
        }

        User user = userDAO.getById(id);
        if (user == null) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        MAPPER.writeValue(response.getWriter(), user);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User payload;
        try {
            payload = MAPPER.readValue(request.getReader(), User.class);
        } catch (JsonProcessingException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        if (payload == null
                || payload.getUsername() == null || payload.getUsername().isBlank()
                || payload.getEmail() == null || payload.getEmail().isBlank()) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "username and email are required");
            return;
        }

        User created = userDAO.create(new User(null, payload.getUsername().trim(), payload.getEmail().trim()));
        response.setStatus(HttpServletResponse.SC_CREATED);
        MAPPER.writeValue(response.getWriter(), created);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        MAPPER.writeValue(response.getWriter(), Map.of("error", message));
    }
}
