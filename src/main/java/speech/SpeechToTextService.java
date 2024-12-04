package speech;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import speech.CallOpenAIWhisper;

public class SpeechToTextService {

    public static String ASR(File audioFile) throws Exception{
        System.out.println("Entered recognizeAudio method");

        CallOpenAIWhisper ASRModel = new CallOpenAIWhisper("whisper-1", "config/gpt3keys.txt");
        String asrText = ASRModel.call(audioFile);
        if (!asrText.isEmpty()) {
            System.out.println("Transcription: " + asrText);
            return asrText;
        }
        return "";

    }

    public static String CallWhisper(File audioFile) throws Exception{
        System.out.println("Entered recognizeAudio method");

        CallWhisper ASRModel = new CallWhisper();
        String asrText = ASRModel.call(audioFile);
        if (!asrText.isEmpty()) {
            System.out.println("Transcription: " + asrText);
            return asrText;
        }
        return "";

    }
}


