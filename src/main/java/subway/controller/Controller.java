package subway.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import subway.controller.manager.Manager;
import subway.domain.SubwayIntializer;
import subway.exception.Validator;
import subway.screen.ActionType;
import subway.screen.Choice;
import subway.screen.EntityType;
import subway.screen.Screen;
import subway.screen.ScreenRepository;
import subway.screen.ScreenRepositoryInitializer;
import subway.screen.ScreenType;
import subway.view.View;

public class Controller {
    private final Stack<Screen> screenStack = new Stack<>();
    private final View view;
    private final Manager manager;
    
    public Controller(Scanner scanner) {
        SubwayIntializer.initialize();
        ScreenRepositoryInitializer.initialize();
        screenStack.add(ScreenRepository.getScreenByType(ScreenType.MAIN));
        view = new View(scanner);
        manager = new Manager(this, view);
    }
    
    public void run() {
        Screen currentScreen;
        String userCommand;
        
        while(!screenStack.isEmpty()) {
            currentScreen = screenStack.peek();
            userCommand = view.askUserCommand(currentScreen);
            operateUserCommand(userCommand, currentScreen);
        }
    }
    
    public String askName(EntityType entityType, ActionType actionType) throws IllegalArgumentException {
        String userInputName = view.askName(entityType, actionType);
        Validator.checkValidName(userInputName, entityType, actionType);
        return userInputName;
    }
    
    public List<String> askEndStationNames() throws IllegalArgumentException {
        List<String> endStationNames = new ArrayList<>();
        
        askUpwardEndStationNames(endStationNames);
        askDownwardEndStationNames(endStationNames);
        
        return endStationNames;
    }

    public String askStationNameToRegisterToRoute(String lineName) throws IllegalArgumentException {
        String stationNameToRegisterToRoute = view.askStationNameToRegisterToRoute();
        Validator.checkValidStationNameToRegisterToRoute(stationNameToRegisterToRoute, lineName);
        return stationNameToRegisterToRoute;
    }
    
    public String askStationOrderInRoute(String lineName) {
        String stationOrderInRoute = view.askStationOrderInRoute();
        Validator.checkStationOrderInRoute(stationOrderInRoute, lineName);
        return stationOrderInRoute;
    }

    public String askStationNameToDeleteFromRoute(String lineName) {
        String stationNameToDeleteFromRoute = view.askStationNameToDeleteFromRoute();
        Validator.checkValidStationNameToDeleteFromRoute(stationNameToDeleteFromRoute, lineName);
        return stationNameToDeleteFromRoute;
    }
    
    private void operateUserCommand(String userCommand, Screen currentScreen) {
        Choice userChoice;
        
        try {
            Validator.checkValidUserCommand(userCommand, currentScreen);
            userChoice = currentScreen.getChoiceByCommand(userCommand);
            operateUserChoice(userChoice);
        } catch (IllegalArgumentException exception) {
            view.printErrorMessage(exception);
        }
    }
    
    private void operateUserChoice(Choice userChoice) {
        if(userChoice.actionTypeEquals(ActionType.MANAGE)) {
            addScreenByEntityType(userChoice.getEntityType());
            return;
        }
        if(userChoice.actionTypeEquals(ActionType.MOVE_BACK) || userChoice.actionTypeEquals(ActionType.EXIT)) {
            popScreen();
            return;
        }
        manager.manageEntity(userChoice);
    }
    
    private void addScreenByEntityType(EntityType entityType) {
        if(entityType == EntityType.STATION) {
            screenStack.add(ScreenRepository.getScreenByType(ScreenType.STATION_MANAGEMENT));
        }
        if(entityType == EntityType.LINE) {
            screenStack.add(ScreenRepository.getScreenByType(ScreenType.LINE_MANAGEMENT));
        }
        if(entityType == EntityType.ROUTE) {
            screenStack.add(ScreenRepository.getScreenByType(ScreenType.ROUTE_MANAGEMENT));
        }
    }
    
    private void popScreen() {
        screenStack.pop();
    }
    
    private void askUpwardEndStationNames(List<String> endStationNames) {
        String upwardEndStationName = view.askUpwardEndStationName();
        Validator.checkValidEndStationName(upwardEndStationName, endStationNames);
        endStationNames.add(upwardEndStationName);
    }
    
    private void askDownwardEndStationNames(List<String> endStationNames) {
        String downwardEndStationName = view.askDownwardEndStationName();
        Validator.checkValidEndStationName(downwardEndStationName, endStationNames);
        endStationNames.add(downwardEndStationName);
    }
}
