package speech;

import java.io.File;

public class TextToSpeechService {

    public static String OpenAITTS(String text) throws Exception {
        CallOpenAITTS TTSModel = new CallOpenAITTS("tts-1", "config/gpt3keys.txt");
        File ttsFile = TTSModel.call(text,"alloy");
        System.out.println("Audio File：" + ttsFile.getAbsolutePath());
        return ttsFile.getAbsolutePath();
    }

    public static String RefTTS(String text,String npcName) throws Exception {
        CallRefTTS TTSRefModel = new CallRefTTS("http://10.27.127.33:60002/v1/tts", "YOUR_API_KEY");
        File ttsFile = TTSRefModel.call(text,npcName);
        System.out.println("Audio File with Reference：" + ttsFile.getAbsolutePath());
        return ttsFile.getAbsolutePath();
    }
}