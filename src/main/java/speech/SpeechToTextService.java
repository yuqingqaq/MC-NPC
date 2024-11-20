package speech;
import com.google.cloud.speech.v1.*;
//import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;

public class SpeechToTextService {

    public static String recognizeAudio(byte[] audioData) throws Exception {
        System.out.println("Entered recognizeAudio method");

        try (SpeechClient speechClient = SpeechClient.create()) {
            System.out.println("SpeechClient created");
            ByteString audioBytes = ByteString.copyFrom(audioData);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            if (!results.isEmpty()) {
                SpeechRecognitionAlternative alternative = results.get(0).getAlternativesList().get(0);
                System.out.println("Transcription: " + alternative.getTranscript());
                return alternative.getTranscript();
            }
        } catch (Exception e) {
            System.err.println("Failed to process speech recognition: " + e.getMessage());
        }
        return "";
    }
}