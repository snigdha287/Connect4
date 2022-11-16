package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	private Controller controller;
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("game.fxml"));
		GridPane rootGridPane=loader.load();

		controller=loader.getController();
		controller.createPlayground();

		MenuBar menuBar=createMenu();
		menuBar.prefWidthProperty().bind(stage.widthProperty());

		Pane menuPane=(Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene=new Scene(rootGridPane);

		stage.setScene(scene);
		stage.setTitle("Connect Four");
		stage.setResizable(false);
		stage.show();

	}

	private MenuBar createMenu(){
		//File menu
		Menu fileMenu= new Menu("File");
		//File menu items
		MenuItem newGame=new MenuItem("New game");
		newGame.setOnAction(actionEvent -> controller.resetGame());

		MenuItem resetGame=new MenuItem("Reset Game");
		resetGame.setOnAction(actionEvent -> controller.resetGame());

		SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();
		MenuItem exitGame=new MenuItem("Exit Game");
		exitGame.setOnAction(actionEvent -> exitGame());

		//Adding file menu items to File menu
		fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
		//Help menu
		Menu helpMenu= new Menu("Help");
		//Help menu items
		MenuItem aboutGame=new MenuItem("About Game");
		aboutGame.setOnAction(actionEvent -> aboutConnect4());

		SeparatorMenuItem separatorMenuItem1=new SeparatorMenuItem();
		MenuItem aboutMe=new MenuItem("About Me");
		aboutMe.setOnAction(actionEvent -> aboutMe());
		//Adding Help menu items to Help menu
		helpMenu.getItems().addAll(aboutGame,separatorMenuItem1,aboutMe);
		//creating Menu Bar
		MenuBar menuBar=new MenuBar();
		//adding File menu and Help menu to Menu Bar
		menuBar.getMenus().addAll(fileMenu,helpMenu);

		return menuBar;
	}

	private void aboutMe() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Developer");
		alert.setHeaderText("Snigdha Shriya");
		alert.setContentText("I am a third year student of Engineering who loves coding. "+
				"Connect 4 is a game which I find interesting and fun. " +
				"I like to spend my free time with my near and dear ones");
		alert.show();
	}

	private void aboutConnect4() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Connect Four");
		alert.setHeaderText("How To Play?");
		alert.setContentText("Connect Four is a two-player connection game "+"" +
				"in which the players first choose a color and then"+
				" take turns dropping colored discs from the top into"+
				" a seven-column, six-row vertically suspended grid."+
				" The pieces fall straight down, occupying the next"+
				" available space within the column. The objective of " +
				"the game is to be the first to form a horizontal, " +
				"vertical, or diagonal line of four of one's own discs." +
				"Connect Four is a solved game. The first player can " +
				"always win by playing the right moves.");
		alert.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	/*
	//private void resetGame() {
	//	controller.resetGame();
	//}
	*/


	public static void main(String[] args) {
		launch();
	}
}