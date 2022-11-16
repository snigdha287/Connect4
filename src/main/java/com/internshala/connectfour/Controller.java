package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	//constants
	private static final int COLUMNS=7;
	private static final int ROWS=6;
	private static final int CIRCLE_DIAMETER=80;

	private static final String discColour1="#24303E";
	private static final String discColour2="#4CAA88";

	//variabels-player names
	private static String PLAYER_ONE="Player One";
	private static String PLAYER_TWO="Player Two";

	//only one player can play at a time
	//isPlayerOneTurn value toggles b/w true and false for each player's turns
	private boolean isPlayerOneTurn=true;
	//Structural purpose
	private final Disc[][] insertedDiscsArray= new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public Button setNamesButton;

	@FXML
	public TextField playerOneTextField;

	@FXML
	public TextField playerTwoTextField;

	private boolean isAllowedToInsert=true;

	public void createPlayground(){

		//method to handle the click event of setNamesButton.
		setNamesButton.setOnAction(actionEvent -> {
			PLAYER_ONE=playerOneTextField.getText();
			PLAYER_TWO=playerTwoTextField.getText();
		});

		Shape rectangleWithHoles=createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles,0,1);

		List<Rectangle> rectangleList=createClickableColumns();
		for (Rectangle rectangle:rectangleList) {
			rootGridPane.add(rectangle,0,1);
		}
	}

	private Shape createGameStructuralGrid(){

		Shape rectangleWithHoles=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				Circle circle =new Circle();
				circle.setRadius(CIRCLE_DIAMETER /2);
				circle.setCenterX(CIRCLE_DIAMETER/2);
				circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);

				circle.setTranslateX(col* (CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);
				//System.out.println(row* (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row* (CIRCLE_DIAMETER+5) + CIRCLE_DIAMETER/4);

				rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns(){

		List<Rectangle> rectangleList=new ArrayList<>();
		for (int col = 0; col < COLUMNS ; col++) {

			Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

			rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

			final int column=col;
			rectangle.setOnMouseClicked(mouseEvent ->{
				if(isAllowedToInsert){
					//blocks dropping of multiple disk
					isAllowedToInsert=false;
					insertDisc(new Disc(isPlayerOneTurn),column);
				}
			});

			rectangleList.add(rectangle);

		}

		return rectangleList ;
	}

	private void insertDisc(Disc disc, int column) {

		int row = ROWS - 1;
		while (row >= 0) {

			if (getDiskIsPresent(row,column) == null)
				break;

			row--;
		}

		if (row < 0)    // If it is full, we cannot insert anymore disc
			return;

		insertedDiscsArray[row][column] = disc;   // For structural Changes: For developers
		insertedDiscPane.getChildren().add(disc);// For Visual Changes : For Players

		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		//System.out.println(row* (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		translateTransition.setToY(row* (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		translateTransition.setOnFinished(event -> {

			//when disc is dropped allow next player to insert disc
			isAllowedToInsert=true;
			if (gameEnded(currentRow, column)) {
				gameOver();
				return;
			}

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
		});

		translateTransition.play();
	}

	private boolean gameEnded(int currentRow, int column) {

		/*
		[Row=6(0,1,2,3,4,5)][Column=7(0,1,2,3,4,5,6)]
			00  01  02  03  04  05  06
			10  11  12  13  14  15  16
			20  21  22  23  24  25  26
			30  31  32  33  34  35  36
			40  41  42  43  44  45  46
			50  51  52  53  54  55  56
		 */

		//Vertical combinations
		//Row-changes;column-constant

		//player inserted last disc at 2,3[R3][C4]
		//possible vertical combinations are with its column elements-03,13,33,43,53
		//point2D class hold values in terms of x and y-{03,13,23,33,43,53}

		// Range need to have 3 discs before and 3 discs after current inserted disk
		List<Point2D> verticalPoints =IntStream.rangeClosed(currentRow-3,currentRow+3)//setting Range to 'r'
				                          .mapToObj(r->new Point2D(r,column))//returns list of Point2D objects
				                           .collect(Collectors.toList());

		//Horizontal points
		//Row-constant;column-changes

		//player inserted last disc at 2,3[R3][C4]
		//possible vertical combinations are with its column elements-03,13,33,43,53
		//Point2D has values of 30,31,32,33,34,35,36

		//Range need to have 3 discs before and 3 discs after current inserted disk
		List<Point2D> horizontalPoints =IntStream.rangeClosed(column-3,column+3)//setting Range to 'c'
				.mapToObj(c->new Point2D(currentRow,c))//returns list of Point2D objects
				.collect(Collectors.toList());

		Point2D startPoint1=new Point2D(currentRow-3,column+3);
		List<Point2D>diagonal1Points=IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2=new Point2D(currentRow-3,column-3);
		List<Point2D>diagonal2Points=IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint2.add(i,i))
				.collect(Collectors.toList());

		boolean isEnded=checkCombinations(verticalPoints)||checkCombinations(horizontalPoints)
							||checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);

		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain=0;

		for (Point2D point:points) {

			int rowIndexForArray =(int) point.getX();
			int coloumnIndexForArray = (int) point.getY();

			Disc disc =getDiskIsPresent(rowIndexForArray,coloumnIndexForArray);

			if(disc!=null && disc.isPlayerOneMove==isPlayerOneTurn){

				chain++;
				if(chain==4) {
					return true;
				}
			}
			else{
				chain=0;
			}
		}
		return false;
	}

	//To prevent 'ArrayIndexOutOfBoundsException'
	private Disc getDiskIsPresent(int row,int column){

		//if row or column Index is invalid
		if(row>=ROWS||row<0||column>=COLUMNS||column<0){
			return null;
		}
		return insertedDiscsArray[row][column];
	}

	private void gameOver(){
		String winner=isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO;
		//System.out.println("Winner is "+winner);
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("Winner is "+winner);
		alert.setContentText(" Want to play again?");

		ButtonType yesBtn=new ButtonType("Yes");
		ButtonType noBtn=new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(()->{
			Optional<ButtonType> btnClicked=alert.showAndWait();
			if(btnClicked.isPresent()&&btnClicked.get()==yesBtn){
				//User choose YES, so RESET the game
				resetGame();
			}else{
				//User choose NO, so EXIT the game
				Platform.exit();
				System.exit(0);
			}
		});

	}

	public void resetGame() {
		//removes all inserted disks
		insertedDiscPane.getChildren().clear();
		//structural array should also be reset to NULL
		for(int row=0;row< insertedDiscsArray.length;row++){
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col]=null;
			}
		}

		isPlayerOneTurn=true;//Let player one start the game
		//Resetting names in Text fields to default
		playerOneTextField.clear();
		playerTwoTextField.clear();

		PLAYER_ONE="Player One";
		PLAYER_TWO="Player Two";

		//Resetting Label to default
		playerNameLabel.setText(PLAYER_ONE);

		//prepare a fresh playground
		createPlayground();
	}

	private static class Disc extends Circle{
		private final boolean isPlayerOneMove;
		//constructor
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove= isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			//color based on player playing
			setFill(isPlayerOneMove?Color.valueOf(discColour1):Color.valueOf(discColour2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);


		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}


}