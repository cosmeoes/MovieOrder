/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieorder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.scene.control.Alert;

/**
 *
 * @author cosme
 */
public class Episode {
    private String path, name;
    private Season s;

    public Episode(String path, String name) {
        this.path = path;
        this.name = name;
       
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public static void sortByEpisode(ArrayList<Episode> e){
        Comparator<Episode> c= new Comparator<Episode>() {
            @Override
            public int compare(Episode o1, Episode o2) {
                 int numEpisode1=0;
                  int numEpisode2=0;
                  String stringNum;
                  
                if(o1.getName().matches("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d][\\d].*")){
                     
                    String name;
                      name=o1.getName().substring(0,o1.getName().indexOf(o1.getName().split("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d][\\d]",2)[1]));
                      stringNum= name.substring(name.length()-2);
                      numEpisode1= Integer.parseInt(stringNum);
                      
                      
               
                }else if(o1.getName().matches("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d].*")){
                    String name;
                    name=o1.getName().substring(0,o1.getName().indexOf(o1.getName().split("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d]",2)[1]));
                    stringNum= name.substring(name.length()-1);
                    numEpisode1=Integer.parseInt(stringNum);
                   
                }
                
                  if(o2.getName().matches("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d][\\d].*")){
                      String name;
                      name=o2.getName().substring(0,o2.getName().indexOf(o2.getName().split("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d][\\d]",2)[1]));
                      stringNum= name.substring(name.length()-2);
                      numEpisode2=Integer.parseInt(stringNum);
                      
               
                }else if(o2.getName().matches("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d].*")){
                    String name;
                       name=o2.getName().substring(0,o2.getName().indexOf(o2.getName().split("[\\D]*[\\d]{1,2}[\\W-xeEX&&\\S][\\d]",2)[1]));
                    stringNum= name.substring(name.length()-1);
                    
                    numEpisode2=Integer.parseInt(stringNum);
                    
                }
                  
                 if(numEpisode1==0&& numEpisode2==0){
                     if (o1.getName().matches(".+[\\W-_xeXE][\\d]{1,2}[\\W-_xeXE].*")&& o2.getName().matches(".+[\\W-_xeXE][\\d]{1,2}[\\W-_xeXE].*")) {
                         String o1Name=o1.getName();
                         String o2Name=o2.getName();
                         if(o1Name.matches(".+[\\W-_xeXE][\\d]{1,2}[\\W-_xeXE]")){
                             o1Name=o1Name.substring(0,o1Name.length()-1);
                         }
                         if(o2Name.matches(".+[\\W-_xeXE][\\d]{1,2}[\\W-_xeXE]")){
                             o2Name=o2Name.substring(0,o1Name.length()-1);
                         }
                         String n1 = o1Name.substring(0, o1Name.indexOf(o1Name.split(".+[\\W-_xeXE][\\d]{1,2}[\\W-_xeXE]")[1]));
                         String n2=o2Name.substring(0, o2Name.indexOf(o2Name.split(".+[\\W-_xeXE][\\d]{1,2}[\\W-_xeXE]")[1]));String lastchar2= n2.substring(n2.length()-1);
                         if(n1.matches(".+[\\W-_xeXE][\\d][\\W-_xeXE]")){
                             numEpisode1=Integer.parseInt(n1.substring(n1.length()-2,n1.length()-1));

                         }else{
                             numEpisode1=Integer.parseInt(n1.substring(n1.length()-3,n1.length()-1));
                         }

                         if (n2.matches(".+[\\W-_xeXE][\\d][\\W-_xeXE]")) {
                             numEpisode2 = Integer.parseInt(n2.substring(n2.length() - 2, n2.length() - 1));

                         } else {
                             numEpisode2 = Integer.parseInt(n2.substring(n2.length() - 3, n2.length() - 1));
                         }
                         
                     }
                 } 
                 
                  
               return numEpisode1-numEpisode2;
               
            }
        };
        e.sort(c);
    }
    
    
    public static void playEpisode(MovieOrSeries mOrs, Season s, Episode e){
        try{
            String[] cm = new String[]{"vlc", e.getPath()};
            Process proc = new ProcessBuilder(cm).start();
            if (!Files.exists(Paths.get(MovieOrder.CONFIGPATH + "/cwl"))) {
                Files.createFile(Paths.get(MovieOrder.CONFIGPATH + "/cwl"));
            }

            ArrayList<String> cwl = (ArrayList) Files.readAllLines(Paths.get(MovieOrder.CONFIGPATH + "/cwl"));
            String toWrite = "";
            for (String cw : cwl) {
                if(!cw.split(",")[0].equals(mOrs.getName())){
                    toWrite += cw + "\n";
                }
            }
            toWrite += mOrs.getName() + "," + s.getSeasonNumber() + "," + e.getName() + "\n";
            Files.write(Paths.get(MovieOrder.CONFIGPATH + "/cwl"), toWrite.getBytes());
        }catch(Exception ex){
            Alert a= new Alert(Alert.AlertType.ERROR);
            a.setContentText("Error playing video:\n"+ ex.getMessage());
            a.show();
            ex.printStackTrace();
        }
    }
}
 
 
