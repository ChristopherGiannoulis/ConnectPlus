package app;

import DataAccsessinterfaces.FileDAO;
import DataAccsessinterfaces.UserFactory;
import interface_adapter.GameBuild.GameBuildViewModel;
import interface_adapter.Home.EndViewModel;
import interface_adapter.Setup.SetupViewModel;
import view.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {

    private static JPanel views;

    public static void main(String[] args) {
        // Build the main program window, the main panel containing the
        // various cards, and the layout, and stitch them together.

        // The main application window.
        JFrame application = new JFrame("Login Example");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();

        // The various View objects. Only one view is visible at a time.
        views = new JPanel(cardLayout);
        application.add(views);

        // This keeps track of and manages which view is currently showing.
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout, viewManagerModel);

        // The data for the views, such as username and password, are in the ViewModels.
        // This information will be changed by a presenter object that is reporting the
        // results from the use case. The ViewModels are observable, and will
        // be observed by the Views.
        LoginViewModel loginViewModel = new LoginViewModel();
        LoggedInViewModel loggedInViewModel = new LoggedInViewModel();
        GameBuildViewModel gameBuildViewModel = new GameBuildViewModel();
        SetupViewModel setupViewModel = new SetupViewModel();
        EndViewModel EndViewModel = new EndViewModel();

        FileDAO userDataAccessObject;
        //TODO: access the file if already exists
        try {
            userDataAccessObject = new FileDAO("./users.csv", new UserFactory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        LoginView loginView = LoginUseCaseFactory.create(viewManagerModel, loginViewModel, loggedInViewModel, userDataAccessObject);
        views.add(loginView, loginView.viewName);

        LoggedInView loggedInView = LoggedInUseCaseFactory.create(viewManagerModel, loggedInViewModel, gameBuildViewModel);
        views.add(loggedInView, loggedInView.viewName);

        EndView endView = EndviewUseCaseFactory.create(viewManagerModel, EndViewModel, gameBuildViewModel);
        views.add(endView, endView.viewName);

        JPanel[] gameViews = GameBuildUseCaseFactory.create(viewManagerModel, loggedInViewModel, gameBuildViewModel, setupViewModel);
        GameBuildView gameBuildView = (GameBuildView) gameViews[0];
        views.add(gameBuildView, gameBuildView.viewName);

        GameView gameView = (GameView) gameViews[1];
        views.add(gameView, gameView.viewName);

        viewManagerModel.setActiveView(loginView.viewName);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setVisible(true);
    }


}
