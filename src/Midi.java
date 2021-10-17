import java.io.*;
import javax.sound.midi.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;


public class Midi {
    public static void main(String[] args) throws InvalidMidiDataException, IOException, MidiUnavailableException {

        String down = findMidi();
        getMidi(down);

        String fileName = "test.mid";
        Sequence test = MidiSystem.getSequence(new File(fileName));
        String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        int[] noteCount = new int[12];

        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.setSequence(test);

        sequencer.start();

        //https://stackoverflow.com/questions/3850688/reading-midi-files-in-java
        //goes through the midi file note by note and adds them to the array
        for(Track track: test.getTracks()){
            for(int i = 0; i < track.size(); i++){
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if(message instanceof ShortMessage){
                    ShortMessage sm = (ShortMessage) message;
                    int note = sm.getData1()%12;
                    noteCount[note]++;
                }
            }
        }
        float sum = 0;
        for(int i = 0; i < notes.length; i++){
            sum += noteCount[i];
        }
        System.out.println("Number of notes: " + sum + "\n");
        for(int i = 0; i < notes.length; i++) {
            System.out.println("Number of " + notes[i] + " notes: " + noteCount[i]);
            float num = noteCount[i];
            System.out.println("Percentage of notes that are " + notes[i] + ": " + (num / sum) * 100 + "%\n");
        }

       saveMidi();
    }

    public static String findMidi() throws IOException{

        //https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
        //Opens a url and reads each line in as a string
        URL oracle = new URL("https://bitmidi.com/random");
        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
        String inputLine;
        String title = "";
        String down = "";
        int titleCount = 0;
        int downloadCount = 0;

        while ((inputLine = in.readLine()) != null) {
            if(inputLine.contains("title") && titleCount == 0){
                titleCount++;
                System.out.println("Title: " + inputLine.substring(7,inputLine.length()-17));
                inputLine = inputLine.toLowerCase();
                title = inputLine.substring(7,inputLine.indexOf(".mid")) + "-mid";
            }
            if(inputLine.contains("downloadUrl") && downloadCount == 0){
                downloadCount++;
                down = inputLine.substring(inputLine.indexOf("downloadUrl"));
                down = "https://bitmidi.com" + down.substring(14, down.indexOf("}")-1);
            }
        }
        in.close();
        for(int i = 0; i < title.length(); i++){
            if(title.substring(i,i+1).equals(" ") && (title.substring(i+1, i+2).equals("-"))){
                title = title.substring(0,i) + title.substring(i+1);
            }
            else if(title.substring(i,i+1).equals(" ") && !title.substring(i+1,i+2).equals(" ") && title.substring(i-1,i).equals("-")){
                title = title.substring(0,i) + title.substring(i+1);
            }
            else if(title.substring(i,i+1).equals(" ") && !title.substring(i+1,i+2).equals(" ") && !title.substring(i-1,i).equals("-")){
                title = title.substring(0,i) + "-" + title.substring(i+1);
            }
        }
        title = title.replace(".", "-");
        System.out.println("https://bitmidi.com/" + title + "\n");
        return down;
    }

    public static void getMidi(String down) throws IOException{
        //https://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java
        //Downloads the url which is the midi file
        URL website = new URL(down);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("test.mid");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    public static void saveMidi(){
        Scanner reader = new Scanner(System.in);
        System.out.print("Do you want to save the file? (Y/N): ");
        String input = reader.next();
        while(!input.toLowerCase().equals("y") && !input.toLowerCase().equals("n")){
            System.out.println(input);
            System.out.print("Please enter a valid input: ");
            input = reader.next();
        }
        if(input.toLowerCase().equals("y")){
            System.out.print("What do you want to rename it to: ");
            input = reader.next();
            File a = new File("test.mid");
            a.renameTo(new File(input + ".mid"));
        }else {
            File a = new File("test.mid");
            a.delete();
        }
    }
}
