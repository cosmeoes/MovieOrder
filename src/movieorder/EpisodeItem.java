/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieorder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 *
 * @author cosme
 */
public class EpisodeItem extends HBox{
    static Image I= new Image("/episode.png");
    ImageView i;
    String title;
    Episode e;
    Season s;
    MovieOrSeries mos;

    public EpisodeItem(Episode _e,Season _s,MovieOrSeries _mos) {
        this.e=_e;
        this.s=_s;
        this.mos=_mos;
        i= new ImageView(I);
        i.setFitHeight(40);
        i.setFitWidth(40);
        this.setSpacing(2);
        HBox.setMargin(this, new Insets(10));
        Text t= new Text(e.getName());
        this.getChildren().addAll(i, t);
        this.setAlignment(Pos.CENTER_LEFT);
        
        i.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                mos.setCurrentSeasonWatching(s,e);
                Episode.playEpisode(mos, s, e);
              
            }
        
        });
       
        
    }
    
     public void reproducir(){
              try{
                    String[] cm= new String[] {"vlc",e.getPath()};
                    Process proc = new ProcessBuilder(cm).start();
                    this.mos.setCurrentSeasonWatching(s,e);
                    if(!Files.exists(Paths.get(MovieOrder.CONFIGPATH+"/cwl"))){
                        Files.createFile(Paths.get(MovieOrder.CONFIGPATH+"/cwl"));
                    }
                    
                    ArrayList<String> cwl=(ArrayList) Files.readAllLines(Paths.get(MovieOrder.CONFIGPATH+"/cwl"));
                    String toWrite="";
                    for(String cw: cwl){
                        if (!cw.split(",")[0].equals(mos.getName())) {
                            toWrite +=cw+"\n";
                          }
                    }
                    toWrite+=mos.getName()+","+s.getSeasonNumber()+","+e.getName()+"\n";
                    Files.write(Paths.get(MovieOrder.CONFIGPATH+"/cwl"),toWrite.getBytes());
                    SeriesItem.upDateLastSeenText(e.getName());
                    
                }catch(IOException e){
                    e.printStackTrace();
                }
        }
    
    
}
