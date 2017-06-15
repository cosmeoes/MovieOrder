/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieorder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author cosme
 */
public class MovieOrder extends Application{
    Stage v;
    static String CONFIGPATH= System.getProperty("user.home")+File.separator+".MovieOrder";
    public static ArrayList<String> folders;
    public static ArrayList<String> cwl=new ArrayList<String>();
    Text loading= new Text("Loading");
    ArrayList<SeriesItem> l;
    ListView<SeriesItem> seriesList;
    ProgressBar pb;
    public static BorderPane bp;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            createConfigFile();
            readLastSeenEpisodes();
            folders=(ArrayList) Files.readAllLines(Paths.get(CONFIGPATH+"/conf"));
            for(String path : folders){
                System.out.println(path);
            }
        }catch(Exception e){e.printStackTrace();}
        launch(args);
    }
    
    public static void createConfigFile() throws IOException {
        if (!Files.exists(Paths.get(CONFIGPATH))) {
            Files.createDirectory(Paths.get(CONFIGPATH));
            Files.createFile(Paths.get(CONFIGPATH + "/conf"));
            System.out.println("Created config path at " + CONFIGPATH);
        } else if (!Files.exists(Paths.get(CONFIGPATH + "/conf"))) {
            Files.createFile(Paths.get(CONFIGPATH + "/conf"));
            System.out.println("Created config file ");
        }
    }

    public static void readLastSeenEpisodes() throws IOException {
        if(Files.exists(Paths.get(CONFIGPATH+"/cwl"))){
            cwl=(ArrayList) Files.readAllLines(Paths.get(CONFIGPATH+"/cwl"));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
       
        v=primaryStage;
        v.setTitle("MovieOrder");
        
        HBox panelSearch = new HBox();
        TextField fieldSearch= new TextField();
        fieldSearch.setStyle("-fx-font-size:18");
        HBox.getHgrow(fieldSearch);
        fieldSearch.setPromptText("Search");
        
      
        panelSearch.setAlignment(Pos.CENTER);
        HBox.setHgrow(panelSearch, Priority.ALWAYS);
        panelSearch.setPadding(new Insets(10,0,0,0));
        panelSearch.setSpacing(5);
        panelSearch.getChildren().addAll(fieldSearch);
        
        HBox addDirPane= new HBox();
        Button btnAddFile= new Button("+");
        btnAddFile.setStyle("-fx-font-size:20; -fx-color: red");
        Tooltip tTip=new Tooltip("Add a movie file");
        tTip.setStyle("-fx-font-size:12");
        btnAddFile.setTooltip(tTip);
        btnAddFile.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc= new FileChooser();
                File f=fc.showOpenDialog(v);
                if(f!=null){
                    try{
                        Path source = f.toPath();
                        if (Files.probeContentType(source).contains("video")) {
                            String toWrite="";
                            for(String w: folders){
                                toWrite+=w+("\n");
                            }
                            toWrite+=f.getPath()+"\n";
                            Files.write(Paths.get(CONFIGPATH+"/conf"), toWrite.getBytes());
                            folders=(ArrayList) Files.readAllLines(Paths.get(CONFIGPATH+"/conf"));
                            findMoviesOrSeries(folders);
                        }else{
                            Alert a= new Alert(Alert.AlertType.ERROR,"File choosed isn't a video file");
                            a.show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                
            }
        });
        
        Button btnAddDir= new Button("+");
        btnAddDir.setStyle("-fx-font-size:20; -fx-color: darkblue");
        Tooltip dirTip=new Tooltip("Add a directory");
        dirTip.setStyle("-fx-font-size:12");
        btnAddDir.setTooltip(dirTip);
        btnAddDir.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser dc= new DirectoryChooser();
                File f= dc.showDialog(v);
                try{
                    String toWrite = "";
                    for (String w : folders) {
                        if(w.equals(f.getPath())){
                            return;
                        }
                        toWrite += w + ("\n");
                        
                    }
                    toWrite += f.getPath() + "\n";

                    Files.write(Paths.get(CONFIGPATH + "/conf"), toWrite.getBytes());
                    folders=(ArrayList) Files.readAllLines(Paths.get(CONFIGPATH+"/conf"));
                    findMoviesOrSeries(folders);
                }catch(Exception e){
                    e.printStackTrace();
                }
                
            }
        });
        
        addDirPane.setPickOnBounds(true);
        addDirPane.getChildren().addAll(btnAddFile,btnAddDir);
        addDirPane.setPadding(new Insets(10,30,10,0));
        addDirPane.setSpacing(5);
        addDirPane.setAlignment(Pos.CENTER_RIGHT);
        
        HBox topPane =new HBox();
        topPane.getChildren().addAll(panelSearch,addDirPane);
        topPane.setAlignment(Pos.CENTER);
        topPane.setStyle("-fx-background-color:#313131");
         l= new ArrayList<SeriesItem>();
         
        seriesList= new ListView<>();
        seriesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SeriesItem>(){
            @Override
            public void changed(ObservableValue<? extends SeriesItem> observable, SeriesItem oldValue, SeriesItem newValue) {
                Task task=new Task() {
                    @Override
                    protected void setException(Throwable t) {
                        t.printStackTrace();
                    }
                    
                    @Override
                    protected Void call() throws Exception {
                        newValue.display();
                        return null;
                    }
                };
                Thread t= new Thread(task);

                t.setDaemon(true);
                t.start();
                
            }
        });

        seriesList.getItems().addAll(l);
        seriesList.setStyle("-fx-background-color:#919191");
        
        fieldSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String searchQuery= fieldSearch.getText().toLowerCase();
                findQuertyInSeriesList(searchQuery);
            }

            public void findQuertyInSeriesList(String searchQuery) {
                for(int i=0;i< seriesList.getItems().size();i++){
                    SeriesItem mI= seriesList.getItems().get(i);
                    String lowerCaseName= mI.getName().toLowerCase();
                    if(lowerCaseName.contains(searchQuery)){
                        seriesList.getSelectionModel().selectIndices(i);
                        fieldSearch.setStyle("-fx-font-size:18; -fx-background-color: white");
                    }else{
                        fieldSearch.setStyle("-fx-font-size:18; -fx-background-color: red");
                    }
                    
                }
            }
        });
        
        Text mvListTitle= new Text("Movies/series");
         pb= new ProgressBar();
         pb.setProgress(0);
         
        VBox leftPane = new VBox();
        leftPane.getChildren().addAll(mvListTitle,pb);
        VBox.setVgrow(seriesList, Priority.ALWAYS);
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.setPadding(new Insets(10,5,10,5));
        leftPane.setStyle("-fx-background-color:#919191");
       
       
        bp= new BorderPane();
       
        bp.setTop(topPane);
        bp.setLeft(leftPane);
        Screen s= Screen.getPrimary();
        Rectangle2D r= s.getVisualBounds();
        Scene sc= new Scene(bp, r.getWidth()-50,r.getHeight()-50);
        v.setScene(sc);
        v.show();
        findMoviesOrSeries(folders);
       
    }
   
   
    public void findMoviesOrSeries(ArrayList<String> f) {
        Task task= new Task<Void>(){
            @Override
            public Void call() throws Exception {
                l.clear();
                ArrayList<MovieOrSeries> listOfMoviesOrSeries = new ArrayList<>();
                for (String folder : f) {
                    listOfMoviesOrSeries.addAll(searchFolder(folder));
                }
                
                l.addAll(transformToItemObjects(listOfMoviesOrSeries));
                l.sort(SeriesItem.c);
                System.out.println("files loaded");
                
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        seriesList.getItems().clear();
                        seriesList.getItems().addAll(l);
                        VBox leftPane=(VBox) bp.getLeft();
                        leftPane.getChildren().removeAll(pb);
                        if(!leftPane.getChildren().contains(seriesList)){
                            leftPane.getChildren().add(seriesList);
                        }
                    }
                });
                return null;
            }
            @Override
            protected void setException(Throwable t) {
                t.printStackTrace();
            }
        };
        
         Thread t= new Thread(task);
         pb.progressProperty().bind(task.progressProperty());
         t.setDaemon(true);
         t.start();
    }
    public ArrayList<SeriesItem> transformToItemObjects(ArrayList<MovieOrSeries> listOfMoviesOrSeries) {
        ArrayList<SeriesItem> ItemObjects = new ArrayList<>();
        for (MovieOrSeries mOS: listOfMoviesOrSeries) {
            if (mOS.getType() == MovieOrSeries.TYPE_SERIES) {
                ItemObjects.add(new SeriesItem(mOS));
            }

        }
        return ItemObjects;
    }

    private ArrayList<MovieOrSeries> searchFolder(String folder) {
        ArrayList<MovieOrSeries> list= new ArrayList<>();
        if(Files.exists(Paths.get(folder))){
            File file= new File(folder);
            if(file.isFile()){
                list.add(new MovieOrSeries(folder,file.getName()));
            }else if(file.isDirectory()){
                list.addAll(handleDirectory(file));
            }
        }
        return list;
    }

    private ArrayList<MovieOrSeries> handleDirectory(File file) {
         ArrayList<MovieOrSeries> list= new ArrayList<>();
         String pathOfFile=  file.getPath();
        if(containsSeries(pathOfFile)){
            ArrayList<Season> seasons= Season.findSeasons(pathOfFile);
            MovieOrSeries ms=new MovieOrSeries(file.getPath(),file.getName(), MovieOrSeries.TYPE_SERIES, seasons);
            setLastEpisodeWatched(ms);
            list.add(ms);
        }else{
            findOtherDirectoyesOrMovieFiles(file, list);
        }
         
         return list;
    }

    public void findOtherDirectoyesOrMovieFiles(File file, ArrayList<MovieOrSeries> list) {
        File[] listOfFiles= file.listFiles();
        for(File f: listOfFiles){
            if(f.isDirectory()){
                list.addAll(handleDirectory(f));
            }else if(f.isFile()){
                try{
                    Path source = f.toPath();
                    if(Files.probeContentType(source).contains("video")){
                        list.add(new MovieOrSeries(f.getPath(),f.getName()));
                    }
                }catch(Exception e){ e.printStackTrace();}
            }
        }
    }

    public void setLastEpisodeWatched(MovieOrSeries ms) throws NumberFormatException {
        for(String cf: cwl){
            if(cf.split(",")[0].equals(ms.getName())){
                int index= Integer.parseInt(cf.split(",")[1]);
                Episode e=ms.getSeasons().get(index).findEpisodeByName(cf.split(",")[2]);
                ms.setCurrentSeasonWatching(ms.getSeasons().get(index),e);
                break;
            }
        }
    }
    
    public boolean containsSeries(String folder) {
      
       File fldr= new File(folder);
       File[] listOfFiles = fldr.listFiles();
       int regularMatch1=0;
       int regularMatch2=0;
       int regularMatch3=0;
       int seasonFolders=0;
       for(File f: listOfFiles){
           try{
               Path source = f.toPath();
               if (Files.probeContentType(source).contains("video")) {
                  if(f.getName().matches(".+[\\d][\\W-xeEX&&\\S][\\d][\\d].*")){
                      regularMatch1++;
                  }else if(f.getName().matches(".+[^0-9][\\d][\\d].*")){
                      regularMatch2++;
                  }else if(f.getName().matches(".+[\\d][\\W-xeEX&&\\S][\\d].*")){
                      regularMatch3++;
                  }
               }else if(f.isDirectory()){
                   if(f.getName().matches(".*[Season|season].[\\d].*")){
                       seasonFolders++;
                   }
               }
        }catch(Exception e){e.printStackTrace();}
           
       }
       if(listOfFiles.length>0){
            double percent = (regularMatch1 * 100) / listOfFiles.length;
            double percent2 = (regularMatch2 * 100) / listOfFiles.length;
            double percent3 = (regularMatch3 * 100) / listOfFiles.length;
            double percent4= (seasonFolders*100)/listOfFiles.length;
            if (percent > 30 || percent2 > 30 || percent3>30||percent4>=70) {
                return true;
            }
       }
        return false;
    }
}
