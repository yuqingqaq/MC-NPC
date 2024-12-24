package speech;

import java.io.File;

public class TextToSpeechService {

    public static String OpenAITTS(String text) throws Exception {
        CallOpenAITTS TTSModel = new CallOpenAITTS("tts-1", "config/gpt3keys.txt");
        File ttsFile = TTSModel.call(text,"alloy");
        System.out.println("Audio File：" + ttsFile.getAbsolutePath());
        return ttsFile.getAbsolutePath();
    }

    public static String RefTTS(String text, String npcName) throws Exception {
        File ttsFile = null;
        CallRefTTS TTSRefModel = new CallRefTTS("http://10.27.127.33:60002/v1/tts1", "YOUR_API_KEY");

        if (npcName.equals("图书馆工作人员")) {
            CallOpenAITTS TTSModel = new CallOpenAITTS("tts-1", "config/gpt3keys.txt");
            ttsFile = TTSModel.call(text, "alloy");
        } else if (npcName.equals("体育馆工作人员")) {
            CallOpenAITTS TTSModel = new CallOpenAITTS("tts-1", "config/gpt3keys.txt");
            ttsFile = TTSModel.call(text, "echo");
        } else if (npcName.equals("朋辈心理辅导员")) {
            CallOpenAITTS TTSModel = new CallOpenAITTS("tts-1", "config/gpt3keys.txt");
            ttsFile = TTSModel.call(text, "nova");
        } else if (npcName.equals("全科医生")) {
            CallOpenAITTS TTSModel = new CallOpenAITTS("tts-1", "config/gpt3keys.txt");
            ttsFile = TTSModel.call(text, "onyx");
        } else {
            ttsFile = TTSRefModel.call(text, npcName);
        }

        System.out.println("Audio File with Reference：" + ttsFile.getAbsolutePath());
        return ttsFile.getAbsolutePath();
    }

}