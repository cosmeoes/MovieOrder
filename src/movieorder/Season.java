 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieorder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author cosme
 */
public class Season {

    
    private String seasonNumber,path;
    private ArrayList<Episode> episodes;
    private Episode CurrentEpisodeWhatching;

    public Season(String seasonNumber, String path, ArrayList<Episode> episodes) {
        this.seasonNumber = seasonNumber;
        this.path = path;
        this.episodes = episodes;
        this.CurrentEpisodeWhatching=null;
    }

    public String getSeasonNumber() {
        return seasonNumber;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public Episode getCurrentEpisodeWhatching() {
        if(CurrentEpisodeWhatching==null){
            CurrentEpisodeWhatching=episodes.get(0);
        }
        return CurrentEpisodeWhatching;
    }

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setCurrentEpisodeWhatching(Episode _CurrentEpisodeWhatching) {
        if(_CurrentEpisodeWhatching != null){
            this.CurrentEpisodeWhatching = _CurrentEpisodeWhatching;
        }
    }
    
    
    public static ArrayList<Season> findSeasons(String pathToSeries){
        ArrayList<Season> seasons= new ArrayList<>();
        ArrayList<Episode> episodes= new ArrayList<>(); 
        File file = new File(pathToSeries);
        episodes.addAll(handleSeriesDirectory(file));
        sortBySeason(episodes);
       
          ArrayList<Episode> seasonE;
          String stringSeasonNumer;
          int season=0;
        for(Episode e: episodes){
            Pattern pattern = Pattern.compile("(.+?)([\\d]{1,3})([\\W-xeEX&&\\S])([\\d][\\d])(.*)");
            Matcher matcher = pattern.matcher(e.getName());
            if(matcher.matches()){
                stringSeasonNumer = matcher.group(2);
                season = Integer.parseInt(stringSeasonNumer);
            }else {
                 season =1;   
            }
            
            int index=season;
            if (seasons.size()<=season){
                for(int i=seasons.size(); i<season;i++){
                    seasonE = new ArrayList<Episode>();
                    seasons.add(i, new Season(i + "", pathToSeries, seasonE));
                }
            }
            if (seasons.size() <= index) {
                seasonE = new ArrayList<Episode>();
                seasonE.add(e);
                
                seasons.add(index, new Season(season + "", pathToSeries, seasonE));
            } else {
                seasonE = seasons.get(index).getEpisodes();
                seasonE.add(e);
                seasons.get(index).setEpisodes(seasonE);
            }
        }
        for(Season s: seasons){
            
            Episode.sortByEpisode(s.getEpisodes());
        }
        return seasons;
        
    }
    private static ArrayList<Episode> handleSeriesDirectory(File f) {
        ArrayList<Episode> episodes= new ArrayList<>(); 
        File[] listOfFiles = f.listFiles();
        
        
        for(File file: listOfFiles){
            if(file.isDirectory()){
                episodes.addAll(handleSeriesDirectory(file));
            }else if(file.isFile()){
                try {
                    Path source = file.toPath();
                    if (Files.probeContentType(source).contains("video")) {
                        String eName = file.getName();
                        episodes.add(new Episode(file.getPath(), eName));
                    }
                } catch (Exception e) {e.printStackTrace();}
                
            }
        }
        
        
        return episodes;
    }
    
    public static void sortBySeason(ArrayList<Episode> episodes){
        Comparator<Episode> c= new Comparator<Episode>() {
            @Override
            public int compare(Episode o1, Episode o2) {
                int sEpisode1=0;
                int sEpisode2=0;
                Pattern pattern = Pattern.compile("(.+?)([\\d]{1,3})([\\W-xeEX&&\\S])([\\d][\\d])(.*)");
                Matcher matcherE1 = pattern.matcher(o1.getName());
                
                if(matcherE1.matches()){
                   String stringSeasonNumer= matcherE1.group(2);
                   sEpisode1=Integer.parseInt(stringSeasonNumer);
               
                }
                Matcher matcherE2 = pattern.matcher(o2.getName());
                if(matcherE2.matches()){
                   String stringSeasonNumer=matcherE2.group(2);
                   sEpisode2=Integer.parseInt(stringSeasonNumer);
                }
               return sEpisode1-sEpisode2;
               
            }
        };
        episodes.sort(c);
    }
    
    public Episode findEpisodeByName(String _n){
        for(Episode e: episodes){
            if(_n.equals(e.getName())){
                return e;
            }
        }
        return null;
    }
    
    public void playAllEpisodes(){
        ArrayList<String> arr= new ArrayList<String>();
        arr.add("vlc");
        arr.add("--no-random");
        for(Episode e: episodes){
            arr.add(e.getPath());
        }
        try{
            new ProcessBuilder(arr.toArray(new String[arr.size()])).start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
