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
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

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
        if (idParam != null && !idParam.isBlank()) {
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
            return;
        }

        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");
        boolean hasPage = pageParam != null && !pageParam.isBlank();
        boolean hasSize = sizeParam != null && !sizeParam.isBlank();

        if (!hasPage && !hasSize) {
            MAPPER.writeValue(response.getWriter(), userDAO.listAll());
            return;
        }

        int page = DEFAULT_PAGE;
        int size = DEFAULT_SIZE;
        try {
            if (hasPage) {
                page = Integer.parseInt(pageParam);
            }
            if (hasSize) {
                size = Integer.parseInt(sizeParam);
            }
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid page or size");
            return;
        }

        if (page < 1 || size < 1 || size > MAX_SIZE) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "page must be >= 1 and size must be between 1 and " + MAX_SIZE);
            return;
        }

        MAPPER.writeValue(response.getWriter(), userDAO.listPage(page, size));
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
