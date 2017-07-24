package dd.com.myq.Fragment.Challenges;

public class Challenge{

    private String challenge_name;
    private String challenge_number;
    private String challenge_image;
    private String challenge_id;
    private String start_time;
    private String end_time;
    public String isChallengeStarted;

    public Challenge(String challenge_name, String challenge_number, String challenge_image, String challenge_id,
                     String start_time, String end_time, String isChallengeStarted) {

        this.challenge_name = challenge_name;
        this.challenge_number = challenge_number;
        this.challenge_image = challenge_image;
        this.challenge_id = challenge_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.isChallengeStarted = isChallengeStarted;
    }

    public String getName() {
        return challenge_name;
    }
    public String getNumber() {
        return challenge_number;
    }
    public String getImage() {
        return challenge_image;
    }
    public String getId() {
        return challenge_id;
    }
    public String getIsChallengeStarted(){
        return isChallengeStarted;}
    public String getStart_time(){
        return start_time;}
    public String getEnd_time(){
        return end_time;}
}
