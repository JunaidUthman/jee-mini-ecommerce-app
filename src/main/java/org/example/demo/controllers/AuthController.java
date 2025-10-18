package org.example.demo.controllers;


import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.demo.entities.Client;
import org.example.demo.services.ClientService;

import java.io.IOException;

@WebServlet("/auth")
public class AuthController extends HttpServlet {

    @Inject
    private ClientService clientService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        System.out.println("AuthController: action = " + action);


        if ("register".equals(action)) {

            // forward to register.jsp page
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/authViews/register.jsp");
            dispatcher.forward(request, response);
        } else if ("login".equals(action)) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/authViews/login.jsp");
            dispatcher.forward(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "saveUser" : saveUser(request , response);
                break;
            case "logUser" : logUser(request , response);
            break;
        }
    }


    private void saveUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Client client = new Client();
        client.setNom(request.getParameter("nom"));
        client.setPrenom(request.getParameter("prenom"));
        client.setPassword(request.getParameter("password"));
        client.setEmail(request.getParameter("email"));

        try{
            System.out.println("trying to save user ");
            if(clientService.save(client)){
                response.sendRedirect(request.getContextPath() + "/auth?action=login&registered=true");
            }
            else{
                request.setAttribute("error", "Email déjà utilisé !");
                request.getRequestDispatcher("/WEB-INF/authViews/register.jsp").forward(request, response);
            }
        }catch(Exception e){
            throw new ServletException(e);
        }
    }

    public void logUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("about to log the user");

        if (clientService.clientExists(email, password)) {
            System.out.println("the user exists");

            // Credentials are valid → log the user in
            HttpSession session = request.getSession(true); // create session if it doesn't exist
            session.setAttribute("userEmail", email); // store user identifier
            session.setAttribute("loggedIn", true);

            // Redirect to home page or dashboard
            response.sendRedirect(request.getContextPath() + "/index");


        } else {
            System.out.println("the user doesn't exists");
            // Invalid credentials → back to login page with error message
            request.setAttribute("loginError", "Invalid email or password!");
            request.getRequestDispatcher("/WEB-INF/authViews/login.jsp")
                    .forward(request, response);
        }
    }



}
