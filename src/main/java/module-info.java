/**
 *
 */
module com.example.connect4 {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires validatorfx;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.bootstrapfx.core;
	requires eu.hansolo.tilesfx;

	opens com.internshala.connectfour to javafx.fxml;
	exports com.internshala.connectfour;
}