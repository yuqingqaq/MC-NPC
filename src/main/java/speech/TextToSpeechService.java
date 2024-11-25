package speech;

import java.io.FileOutputStream;
import java.io.OutputStream;
import speech.CallTTS;
import java.io.File;
import java.nio.file.Path;

public class TextToSpeechService {

    public static String OpenAITTS(String text) throws Exception {
        CallTTS TTSModel = new CallTTS("tts-1", "config/gpt3keys.txt");
        File ttsFile = TTSModel.call(text,"alloy");
        System.out.println("Audio File：" + ttsFile.getAbsolutePath());
        return ttsFile.getAbsolutePath();
    }
}