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

    public static String RefTTS(String text) throws Exception {
        CallRefTTS TTSRefModel = new CallRefTTS("http://10.27.127.33:60001/v1/tts", "YOUR_API_KEY");
        File ttsFile = TTSRefModel.call(text);
        System.out.println("Audio File without Reference：" + ttsFile.getAbsolutePath());
        return ttsFile.getAbsolutePath();
    }
}