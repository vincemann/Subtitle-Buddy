package com.youneedsoftware.subtitleBuddy.srt.subtitleFile;
import com.google.inject.Singleton;
import com.youneedsoftware.subtitleBuddy.srt.Subtitle;
import com.youneedsoftware.subtitleBuddy.srt.Timestamp;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Log4j
@Singleton
public class SubtitleFileImpl implements SubtitleFile{

    @Getter
    private List<Subtitle> subtitles;
    private Subtitle firstSubtitle;
    private Subtitle lastSubtitle;

    /**
     * geordnete Liste an Subtitles
     * @param subtitles
     */
    public SubtitleFileImpl(@NotNull List<Subtitle> subtitles) throws EmptySubtitleListException {
        this.subtitles = subtitles;
        this.firstSubtitle = subtitles.get(0);
        if(firstSubtitle==null){
            throw new EmptySubtitleListException("Emtpy subtitlelist");
        }
        log.debug("first subtitle StartingTime : " + firstSubtitle.getStartTime());
        this.lastSubtitle = subtitles.get(subtitles.size()-1);
        log.debug("last subtitle EndTime: " + lastSubtitle.getEndTime());
    }

    @Override
    public Optional<Subtitle> getSubtitleAtTimeStamp(Timestamp timestamp) throws TimeStampOutOfBoundsException {
        if(timestamp.isNegative()){
            throw new TimeStampOutOfBoundsException("negative TimeStamp");
        }

        if(timestamp.compareTo(firstSubtitle.getStartTime())<0){
            return Optional.empty();
        }

        if(timestamp.compareTo(lastSubtitle.getEndTime())>0){
            log.warn("The Timestamp :" + timestamp +" comes after the last subtitle of the srt file");
            throw new TimeStampOutOfBoundsException("The Timestamp : " + timestamp + " comes after the last subtitle of the srt file");
        }

        for(Subtitle subtitle: subtitles){
            if(subtitle.getEndTime().compareTo(timestamp)>0 && subtitle.getStartTime().compareTo(timestamp)<0){
                return Optional.of(subtitle);
            }
        }
        //wir befinden uns in einer Lücke
        return Optional.empty();
    }
}
