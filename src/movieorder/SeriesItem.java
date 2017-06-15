/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieorder;

import java.util.Comparator;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author cosme
 */
public class SeriesItem extends VBox{
    static Image PLAY_IMAGE=new Image("/play.png");
    static Image RANDOM_PLAY_IMAGE= new Image("/rnd.png");
    public MovieOrSeries mOrs;
    Text infoLastSeen;
    Text name;

    public SeriesItem(MovieOrSeries mOrs) {
        this.mOrs = mOrs;
        this.name=new Text();
        this.name.setText(mOrs.getName());
        infoLastSeen=new Text();
        
        if(mOrs.getType()==1){
            this.setStyle("-fx-color: #aaa");
        }
        this.getChildren().add(name);
    }

    public MovieOrSeries getmOrs() {
        return mOrs;
    }

    public String getName() {
        return name.getText();
    }
    
    public static Comparator c= new Comparator<SeriesItem>() {
        @Override
        public int compare(SeriesItem o1, SeriesItem o2) {
                return o2.getmOrs().getType()-o1.getmOrs().getType();
        }
    };
    
    public void display(){
        if(mOrs.getType()==MovieOrSeries.TYPE_SERIES){        
            //center pane
            VBox centerPane= createCenterPane();
            //Rigth Pane
            VBox rightPane= createRightPane();
            Platform.runLater(new Runnable() {
            @Override
                public void run() {
                    MovieOrder.bp.setCenter(centerPane);
                    MovieOrder.bp.setRight(rightPane);
                }
            });
            

        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    MovieOrder.bp.setRight(null);
                    MovieOrder.bp.setCenter(null);
                }
            });

        }
    }
    
    public static void upDateLastSeenText(String NameOfLastSeen){
        if (NameOfLastSeen.length() > 37) {
            NameOfLastSeen = NameOfLastSeen.substring(0, 37) + "...";
        }
        final String finalCname = NameOfLastSeen;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((Text) ((VBox) MovieOrder.bp.getRight()).getChildren().get(3)).setText("Last Episode seen:\n" + finalCname);
            }
        });
            
    }

    private VBox createCenterPane() {
        VBox centerPane= new VBox();
         centerPane.setPadding(new Insets(10));
            ListView<VBox> listSeasons= new ListView<>();
            centerPane.autosize();
            centerPane.setAlignment(Pos.TOP_CENTER);
            centerPane.setSpacing(5);
            String formatedName=formatName(name.getText());
            Text title=new Text(formatedName);
            title.setStyle("-fx-font-size:18");

            for(Season s: mOrs.getSeasons()){
                if(s.getEpisodes().size()>0){
                    listSeasons.getItems().add(new SeasonItem(s,mOrs));
                }
            }
            VBox.setVgrow(listSeasons, Priority.ALWAYS);
           centerPane.getChildren().addAll(title,listSeasons);
           return centerPane;
    }

    private VBox createRightPane() {
        VBox rightVBox = new VBox();

        rightVBox.setPrefWidth(350);
        rightVBox.setPadding(new Insets(10));
        String formatedName= formatName(name.getText());
        Text mosName = new Text(formatedName);
        mosName.setStyle("-fx-font-size: 25");
        mosName.setTextAlignment(TextAlignment.CENTER);
        String nOfSeasons = mOrs.getSeasons().size() - 1 + "";
        int nOfEp = 0;
        for (Season s : mOrs.getSeasons()) {
            nOfEp += s.getEpisodes().size();
        }

        Text infoSeasons = new Text("Number of seasons: " + nOfSeasons);
        Text infoEp = new Text("Number of episodes: " + nOfEp);
        String currenEName = mOrs.getCurrentSeasonWatching().getCurrentEpisodeWhatching().getName();
        if (currenEName.length() > 37) {
            currenEName = currenEName.substring(0, 37) + "...";
        }
        infoLastSeen = new Text("Last Episode seen:\n" + currenEName);
        ImageView playNextIV = new ImageView(PLAY_IMAGE);
        playNextIV.setFitWidth(60);
        playNextIV.setFitHeight(60);
        Button playNextButton = new Button("", playNextIV);
        playNextButton.setStyle("-fx-background-radius: 100");
        playNextButton.setTooltip(new Tooltip("Play next episode"));
        playNextButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Season currentSeason = mOrs.getCurrentSeasonWatching();
                Episode lastEpisode = currentSeason.getCurrentEpisodeWhatching();
                int index = currentSeason.getEpisodes().indexOf(lastEpisode);

                if (index >= currentSeason.getEpisodes().size() - 1) {
                    int indexSeason = mOrs.getSeasons().indexOf(currentSeason);
                    Season seasonToWatch;
                    if (indexSeason >= mOrs.getSeasons().size() - 1) {
                        seasonToWatch = mOrs.getSeasons().get(0);
                    } else {
                        seasonToWatch = mOrs.getSeasons().get(indexSeason + 1);
                    }
                    mOrs.setCurrentSeasonWatching(seasonToWatch, seasonToWatch.getEpisodes().get(0));
                } else {
                    Episode episodeToWatch = currentSeason.getEpisodes().get(index + 1);
                    currentSeason.setCurrentEpisodeWhatching(episodeToWatch);
                }

                String currenEName = mOrs.getCurrentSeasonWatching().getCurrentEpisodeWhatching().getName();
                SeriesItem.upDateLastSeenText(currenEName);

                Episode.playEpisode(mOrs, mOrs.getCurrentSeasonWatching(), mOrs.getCurrentSeasonWatching().getCurrentEpisodeWhatching());

            }
        });

        ImageView rndIV = new ImageView(RANDOM_PLAY_IMAGE);
        rndIV.setFitWidth(60);
        rndIV.setFitHeight(60);
        Button rndBtn = new Button("", rndIV);
        rndBtn.setTooltip(new Tooltip("Randomly play all episodes"));
        rndBtn.setStyle("-fx-background-radius: 100");
        rndBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mOrs.playAllEpisodes();
            }
        });
        HBox playPane = new HBox();
        playPane.setSpacing(10);
        playPane.setPadding(new Insets(10));
        playPane.getChildren().addAll(playNextButton, rndBtn);
        playPane.setAlignment(Pos.CENTER);
        HBox.setMargin(playNextIV, new Insets(10));
        rightVBox.getChildren().addAll(mosName, infoSeasons, infoEp, infoLastSeen, playPane);
       return rightVBox;
    }

    private String formatName(String name) {
        if (name.length() > 25) {
            name = name.substring(0, 25) + "...";
        }
        return name;
    }
    
}
