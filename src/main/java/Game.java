import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Game extends Application {

    public static ArrayList<Platform> platforms = new ArrayList<>();
    private HashMap<KeyCode,Boolean> keys = new HashMap<>();
    Image backgroundImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/back/black.jpeg")));

    public static final int BLOCK_SIZE = 68;

    public static Pane appRoot = new Pane();
    public static Pane gameRoot = new Pane();
    public Character player;
    public static int score = 0;
    Text scoreText = new Text();
    Text gameoverText = new Text();
    public boolean checkPlayerPos;

    private void initContent(){
        ImageView background = new ImageView(backgroundImg);
        background.setFitHeight(700);
        background.setFitWidth(450);
        int shift = 650;
        int min = 130;
        int max = 160;
        for (int i = 0; i < 8; i++) { //целые
            shift-=min+(int)(Math.random()*((max-min)+1));
            platforms.add(new Platform(1,(int)(Math.random()*5*BLOCK_SIZE),shift));
        }
        for (int i = 0; i < 4; i++) { //сломанные
            shift-=min+(int)(Math.random()*((max-min)+1));
            platforms.add(new Platform(2,(int)(Math.random()*5*BLOCK_SIZE),shift));
        }
        addCharacters(background);
    }

    public void addCharacters(ImageView background) {
        player = new Character(0);
        player.setTranslateX(185);
        player.setTranslateY(650);
        player.translateYProperty().addListener((obs,old,newValue)->{
            checkPlayerPos = false;
            if(player.getTranslateY()<300){
                checkPlayerPos = true;
                for(Platform platform : Game.platforms){
                    platform.setTranslateY(platform.getTranslateY()+0.5);
                    if(platform.getTranslateY()==701){
                        if (platform.id == 2){
                            platform.brownPlatform();
                            platform.setDestroyOnce(false);
                        }
                        platform.setTranslateY(1);
                        platform.setTranslateX(Math.random()*6*BLOCK_SIZE);
                    }
                }
            }
        });

        gameRoot.getChildren().add(player);
        appRoot.getChildren().addAll(background, gameRoot);
    }

    private void update(){
        playerControll();
        playerScore();
        checkSide();
        if (player.ifFalls()){
            gameOverText();
            gameoverText.setText("        Good result!" + "\nPress the space bar \nto continue the game :)");
            scoreText.setTranslateY(250);
            scoreText.setTranslateX(200);
            if(isPressed(KeyCode.SPACE)){
                restart();
            }
        }
    }

    private void gameOverText(){

        gameRoot.getChildren().remove(gameoverText);
        gameoverText.setText(null);
        gameoverText.setTranslateX(150);
        gameoverText.setTranslateY(330);

        gameoverText.setScaleX(2);
        gameoverText.setScaleY(2);
        gameRoot.getChildren().add(gameoverText);
    }

    private void playerScore() {
        gameRoot.getChildren().remove(scoreText);

        if(checkPlayerPos == true){
                score+=1;
        }
        scoreText.setText("Score: "+ score);
        scoreText.setTranslateY(30);
        scoreText.setTranslateX(50);
        scoreText.setScaleX(2);
        scoreText.setScaleY(2);
        gameRoot.getChildren().add(scoreText);
    }

    private void checkSide() {
        if(player.getTranslateX()<-59){
            player.setTranslateX(520);
        }else if(player.getTranslateX()>450 && player.getTranslateX()>519){
            player.setTranslateX(-59);
        }
    }

    private void playerControll() {
        if(player.getTranslateY()>=5){
            player.jumpPlayer();
            player.setCanJump(false);
        }
        if(isPressed(KeyCode.LEFT)){
            player.setScaleX(-1);
            player.moveX(-7);
        }
        if(isPressed(KeyCode.RIGHT)){
            player.setScaleX(1);
            player.moveX(7);
        }
        if(player.playerVelocity.getY()<10){
            player.playerVelocity = player.playerVelocity.add(0,1);
        } else player.setCanJump(false);
        player.moveY((int)player.playerVelocity.getY());
    }

    public void restart(){
        platforms.clear();
        keys.clear();
        Game.score = 0;
        gameRoot.getChildren().remove(gameoverText);
        gameRoot.getChildren().clear();
        appRoot.getChildren().removeAll(gameRoot);
        gameRoot.getChildren().clear();

        initContent();
    }

    private boolean isPressed(KeyCode key){
        return keys.getOrDefault(key,false);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        initContent();
        Scene scene = new Scene(appRoot,450,700);

        scene.setOnKeyPressed(event-> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> {
            keys.put(event.getCode(), false);
        });
        primaryStage.setTitle("Doodle Jump");
        primaryStage.setScene(scene);
        primaryStage.show();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}