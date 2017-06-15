/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieorder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cosme
 */
public class MovieOrSeries {
    public static final int TYPE_MOVIE=0;
    public static final int TYPE_SERIES=1;
    private String path, name;
    private int type;
    private List<Season>  seasons;
    private Season currentSeasonWatching;
    

    public MovieOrSeries(String path,String name, int type, List<Season> seasons) {
        this.path = path;
        this.name = name;
        this.type = type;
        this.seasons = seasons;
        this.currentSeasonWatching=this.seasons.get(0);
    }

    public MovieOrSeries(String path, String name) {
        this.path = path;
        this.name = name;
        this.type= TYPE_MOVIE;
        this.seasons=null;
        this.currentSeasonWatching=null;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addSeasons(Season season) {
        this.seasons.add(season);
    }

    public void setCurrentSeasonWatching(Season currentSeasonWatching,Episode e) {
        this.currentSeasonWatching = currentSeasonWatching;
        currentSeasonWatching.setCurrentEpisodeWhatching(e);
    }
    
    public String getPath() {
        return path;
    }

    public int getType() {
        return type;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public Season getCurrentSeasonWatching() {
        if(currentSeasonWatching.getEpisodes().isEmpty()){
            for(Season s: seasons){
                if(!s.getEpisodes().isEmpty()){
                    currentSeasonWatching=s;
                    break;
                }
            }
        }
        return currentSeasonWatching;
    }
    
    public String getName() {
        return name;
    }
    
    
    public void playAllEpisodes(){
        if(seasons!=null){
            ArrayList<String> paths= new ArrayList<>();
            paths.add("vlc");
            for(Season s: seasons){
                for(Episode e: s.getEpisodes()){
                    paths.add(e.getPath());
                }
            }
            try{
                String[] cmd=new String[paths.size()];
                paths.toArray(cmd);

                (new ProcessBuilder(cmd)).start();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    
}
