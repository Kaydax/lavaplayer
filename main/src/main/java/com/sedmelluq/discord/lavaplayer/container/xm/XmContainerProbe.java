package com.sedmelluq.discord.lavaplayer.container.xm;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetectionResult;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerHints;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerProbe;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import ibxm.Module;
import ibxm.IBXM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection.UNKNOWN_ARTIST;
import static com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection.UNKNOWN_TITLE;
import static com.sedmelluq.discord.lavaplayer.container.MediaContainerDetectionResult.supportedFormat;
import static com.sedmelluq.discord.lavaplayer.tools.DataFormatTools.defaultOnNull;

public class XmContainerProbe implements MediaContainerProbe {
    private static final Logger log = LoggerFactory.getLogger(XmContainerProbe.class);

    @Override
    public String getName() {
        return "module";
    }

    @Override
    public boolean matchesHints(MediaContainerHints hints) {
        return false;
    }

    @Override
    public MediaContainerDetectionResult probe(AudioReference reference, SeekableInputStream inputStream) throws IOException {
        Module module;
        try {
            module = new Module(inputStream);
        } catch (IllegalArgumentException e) {
            return null;
        }

        log.debug("Track {} is a module.", reference.identifier);

        inputStream.seek(0);

        IBXM ibxm = new IBXM( module, 44100 );

        return supportedFormat(this, null, new AudioTrackInfo(
                defaultOnNull(module.songName, UNKNOWN_TITLE),
                UNKNOWN_ARTIST,
                defaultOnNull(ibxm.calculateSongDuration(), Long.MAX_VALUE),
                reference.identifier,
                true,
                reference.identifier
        ));
    }

    @Override
    public AudioTrack createTrack(String parameters, AudioTrackInfo trackInfo, SeekableInputStream inputStream) {
        return new XmAudioTrack(trackInfo, inputStream);
    }
}
