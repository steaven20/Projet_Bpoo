package ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;


public class CandyCrush extends Application {

	/**
	 * Cette constantes définit la durée pendant laquelle la keyframe1 sera affichée (ici, 0.1 seconde)
	 */
	private static final double TEMPS_AFFICHAGE_KEYFRAME1 = 0.1;
	
	
	/**
	 * Nombre de bonbons (candies) pris en charge pour la démo. En réalité, il y en a plus...
	 */
	private static final int NOMBRE_DE_CANDIES = 10;
	
	
	private Scene scene;
	private Pane root;

	/**
	 * Coordonnées des pixels de départ et de fin du DnD
	 */
	private int xd, yd, xf, yf;
	
	/**
	 * Un canvas pour affiche rdu graphisme (ici, des images
	 */
	private Canvas grillePane;
	private GraphicsContext gc;
	
	/**
	 * Le tableau contenant les images des bonbons
	 */
	private Image[] candies;
	
	private Timeline timeline;
	
	/**
	 * Pour la gestion et l'affichage du chrono
	 */
	private Label lChrono;
	private	int secondesEcoulees = 0;
	private Timeline timelineChrono;
	
	/**
	 * Tableau 2D d'entiers. Chaque entier correspond à l'indice d'une image (0-->Candy_0, 1-->Candy_1,...)
	 * Faudra faire mieux évidemment dans le projet...
	 */
	private	int[][] grille = new int[10][10];


	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Candy Crush");

			
			initImagesCandies();
			
			root = new BorderPane(grillePane);
			initGrille();
			initChrono();

			scene = new Scene(root);

			initTimelineJeu();
			initTimelineChrono();

			primaryStage.setScene(scene);
			primaryStage.show();

			
			demarrerPartie();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initChrono() {
		HBox hbox = new HBox();
		lChrono = new Label();
		hbox.getChildren().add(lChrono);
		((BorderPane)root).setBottom(hbox);
		
	}

	private void demarrerPartie() {
		dessinerPlateau();

		timeline.play();
		
		/**
		 * Si on veut suspendre le déroulement d'une timeline --> timeline.pause()
		 * Si on veut l'arrêter --> timeline.stop()
		 */
	}


	private void initGrille() {
		creerGrilleTest();

		
		grillePane = new Canvas(640, 640);
		((BorderPane) root).setCenter(grillePane);

		gc = grillePane.getGraphicsContext2D();

		
		grillePane.setOnDragDetected(new DragDetectedEvent());	// Si on veut inactiver le DnD --> grillePane.setOnDragDetected(null)
		grillePane.setOnDragOver(new DragOverEvent());
		grillePane.setOnDragDropped(new DragDroppedEvent());

	}

	private void creerGrilleTest() {
		/**
		 * Pour cette démo, on initialise une simple grille "en dur" pour faire des tests.
		 * D'abord on la remplit de vide, puis on place quelques combinaisons
		 */
		int Vide = 0; 
		for (int l=0; l<10; l++) {
			for (int c=0; c<10; c++) {
				grille[l][c] = Vide;
			}
		}
		
		
		/**
		 * On ajoute 3 bonbons bleus alignés horizontalement et commençant en (2,4)
		 */
		int Bleu = 1;
		grille[2][4] = Bleu;
		grille[2][5] = Bleu;
		grille[2][6] = Bleu;
		
		/**
		 * On ajoute 3 bonbons violets alignés verticalement et commençant en (2,7)
		 */
		int Violet = 2;
		grille[2][7] = Violet;
		grille[3][7] = Violet;
		grille[4][7] = Violet;
		
		/**
		 * On ajoute 3 bonbons jaunes alignés horizontalement et commençant en (3,3). Le bonbon du milieu est rayé
		 */
		int Jaune = 3;
		grille[3][3] = Jaune;
		grille[3][4] = Jaune+4;
		grille[3][5] = Jaune;
		
		
		/**
		 * On ajoute 3 bonbons verts alignés verticalement et commençant en (2,2). Le bonbon du bas est rayé
		 */
		int Vert = 4;
		grille[2][2] = Vert;
		grille[3][2] = Vert;
		grille[4][2] = Vert+4;

		/**
		 * On ajoute 4 bonbons verts alignés verticalement et commençant en (3,6). Le bonbon du bas est rayé
		 */
		grille[3][6] = Vert;
		grille[4][6] = Vert;
		grille[5][6] = Vert;
		grille[6][6] = Vert;		
		
		/**
		 * On ajoute 4 bonbons jaunes alignés horizontalement et commençant en (7,4). Le bonbon du bas est rayé
		 */
		grille[7][4] = Jaune;
		grille[7][5] = Jaune;
		grille[7][6] = Jaune;
		grille[7][7] = Jaune;

		/**
		 * On ajoute 9 bonbons au milieu de la grille pour lesquels il n'y a pas de combinaisons
		 * Mais si l'on échange (4,4) et (5,4) alors une combinaison de 3 verts apparaît
		 */
		grille[4][3] = Jaune;
		grille[4][4] = Vert;
		grille[4][5] = Violet;
		grille[5][3] = Bleu;
		grille[5][4] = Bleu;
		grille[5][5] = Vert;		
		grille[6][3] = Bleu;
		grille[6][4] = Jaune;
		grille[6][5] = Violet;
		
		/**
		 * On place une meringue en (3,8)
		 */
		int Meringue = 9;
		grille[3][8] = Meringue;
		
		/**
		 * Si l'on échange ces deux cases, une combinaison de 4 vilets apparaît, donc bonbon rayé
		 */
		grille[1][6] = Violet;
		grille[1][7] = Bleu;
		
		
		
		/** 
		 * Pour afficher la liste des images
		 */
		grille[9][0] = 0;
		grille[9][1] = 1;
		grille[9][2] = 2;
		grille[9][3] = 3;
		grille[9][4] = 4;
		grille[9][5] = 5;
		grille[9][6] = 6;
		grille[9][7] = 7;
		grille[9][8] = 8;
		grille[9][9] = 9;
	}

	private void initTimelineChrono() {
		// Cette KeyFrame apparaît 1s après le début de la timeline
		KeyFrame k = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent event) {
				secondesEcoulees++;
				mettreAJourTemps();
			}

			private void mettreAJourTemps() {
				int s, m;

				m = secondesEcoulees / 60;
				s = secondesEcoulees % 60;
				lChrono.setText(""+m+"m "+s+"s");
			}
		});

		timelineChrono = new Timeline(k);
		// La timeline va boucler à l'infinie. Donc la keyframe k sera bien dclenchée toutes les secondes
		timelineChrono.setCycleCount(Animation.INDEFINITE);
		timelineChrono.play();
	}


	private void initTimelineJeu() {
		final KeyFrame keyframe1 = new KeyFrame(Duration.seconds(0), new KeyFrame1()); // Cette KF sera affichée tout de suite et restera jusqu'à la prochaine KF
		final KeyFrame keyframe2 = new KeyFrame(Duration.seconds(TEMPS_AFFICHAGE_KEYFRAME1 ), new KeyFrame2()); // Cette KF2 sera affichée après 0.1s, c'est-à-dire la durée que l'on a défini pour la KF 1
		timeline = new Timeline(keyframe1, keyframe2);
		timeline.setCycleCount(Animation.INDEFINITE); // L'animation va également boucler à l'infinie
	}

	private void dessinerPlateau() {

		for (int l = 0; l < 10; l++) {
			for (int c = 0; c < 10; c++) {

				gc.drawImage(candies[grille[l][c]], c * 64, l * 64);
			}
		}
	}

	private void initImagesCandies() {
		candies = new Image[NOMBRE_DE_CANDIES];

		try {
			for (int i = 0; i < candies.length; i++) {
				candies[i] = new Image(getClass().getResourceAsStream("/Candy_" + i + ".png"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final class KeyFrame2 implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {

			gc.clearRect(0, 0, 640, 640);
			// Dans cette frame, on est sensé voir des étoiles qui matérialisent la prochaine
			// disparition des bonbons (etoile = case vide)

		}
	}

	private final class KeyFrame1 implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {

			// Dans cette frame, pour la démo, on redessine la grille et c'est tout
			// Pour le projet, il y a sans doute des choses à faire pour compter les points, faire apparaître des bonbons spéciaux
			// ou autre chose encore...
			dessinerPlateau();
		}
	}

	private final class DragDetectedEvent implements EventHandler<MouseEvent> {

		private DragDetectedEvent() {
		}

		/**
		 * Début du DnD (Drag and Drop)
		 */
		public void handle(MouseEvent event) {
			Dragboard db = grillePane.startDragAndDrop(TransferMode.ANY);

			/**
			 * Coordonnées du pixel de départ
			 */
			xd = (int) event.getX();
			yd = (int) event.getY();

			int l = yd / 64;
			int c = xd / 64;

			/**
			 * On définit l'image qui va suivre la souris pendant le DnD
			 */
			db.setDragView(candies[grille[l][c]]);

			ClipboardContent content = new ClipboardContent();
			content.putString("");
			db.setContent(content);

			event.consume();
		}
	}

	private final class DragOverEvent implements EventHandler<DragEvent> {

		private DragOverEvent() {
		}

		public void handle(DragEvent event) {

			event.acceptTransferModes(TransferMode.ANY);
			event.consume();
		}
	}

	private final class DragDroppedEvent implements EventHandler<DragEvent> {
		private DragDroppedEvent() {
		}

		/**
		 * Fin du Drag and Drop 
		 */
		public void handle(DragEvent event) {

			/**
			 * Coordonnées du pixel de fin
			 */
			xf = (int) event.getX();
			yf = (int) event.getY();

			echangerSourceTarget();

			event.consume();
		}

		private void echangerSourceTarget() {
			int ls = 0, cs = 0, lt = 0, ct = 0;

			/** coordonnées de la case de départ (s comme source) */
			ls = yd / 64;
			cs = xd / 64;
			
			/** coordonnées de la case d'arrivée du DnD (t comme target) */
			lt = yf / 64;
			ct = xf / 64;

			/** On échange les deux entiers, c'est tout ce que l'on fait dans la démo */
			int temp = grille[ls][cs];
			grille[ls][cs] = grille[lt][ct];
			grille[lt][ct] = temp;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
