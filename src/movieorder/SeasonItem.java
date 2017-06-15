/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieorder;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author cosme
 */
public class SeasonItem extends VBox {
    static Image I = new Image("/play.png");
    String seasonNumber;
    ListView<EpisodeItem> episodeList;
    Season s;
    MovieOrSeries mos;

    public SeasonItem(Season _s, MovieOrSeries _mos) {
        this.s=_s;
        this.mos=_mos;
        this.seasonNumber = s.getSeasonNumber();
        this.setSpacing(5);
        Text sN= new Text("Season "+seasonNumber);
        HBox titleAndPlay= new HBox();
        Button btnPlay= new Button();
        btnPlay.setTooltip(new Tooltip("play all episodes in this season"));
        ImageView playImage= new ImageView(I);
        playImage.setFitHeight(18);
        playImage.setFitWidth(18);
        btnPlay.setGraphic(playImage);
        btnPlay.setPrefSize(10, 10);
        StackPane sp= new StackPane(btnPlay);
        sp.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(sp, Priority.ALWAYS);
        titleAndPlay.setAlignment(Pos.CENTER_LEFT);
        titleAndPlay.getChildren().addAll(sN,sp);
        titleAndPlay.setSpacing(10);
        this.getChildren().add(titleAndPlay);
        
        
        
        episodeList=new ListView<>();
        for(Episode e: s.getEpisodes()){
            episodeList.getItems().add(new EpisodeItem(e,s,mos));
        }
        episodeList.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
               if(event.getCode()==KeyCode.ENTER){
                   EpisodeItem e= episodeList.getSelectionModel().getSelectedItem();
                   e.reproducir();
               }
            }
            
        });
        
        btnPlay.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                s.playAllEpisodes();
            }
        });
        this.getChildren().add(episodeList);
        
    }
    
    
    
    
}
