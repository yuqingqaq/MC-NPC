package speech;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.OutputStream;
import speech.CallTTS;
import java.io.File;
import java.nio.file.Path;

public class TextToSpeechService {

    public static void synthesizeSpeechToFile(String text, String outputFile) throws Exception {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();
            try (OutputStream out = new FileOutputStream(outputFile)) {
                out.write(audioContents.toByteArray());
            }
        }
    }

    public static String OpenAITTS(String text) throws Exception {
        CallTTS TTSModel = new CallTTS("tts-1", "config/gpt3keys.txt");
        File ttsFile = TTSModel.call(text,"alloy");
        System.out.println("Audio File：" + ttsFile.getAbsolutePath());
        return ttsFile.getAbsolutePath();
    }
}