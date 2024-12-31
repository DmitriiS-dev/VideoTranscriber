package com.videoTranscriber.demo;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ProcessingService {

    public String extractAudio(String videoFilePath, String outputDir) throws IOException, InterruptedException{
        String audioFileName = "extracted-audio.mp3";
        String audioFilePath = outputDir+ File.separator+audioFileName;

        File outputDirectory = new File(outputDir);
//        Check the directory exists:
        if(!outputDirectory.exists()){
            outputDirectory.mkdirs();
        }

//      ffmpeg command to extract the audio:
        String command = String.format("ffmpeg -i %s -q:a 0 -map a %s", videoFilePath, audioFilePath);

        //Execute the command:
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if(exitCode != 0){
            throw new RuntimeException("FFmpeg audio extraction failed");
        }

        return audioFilePath;


    }
}
