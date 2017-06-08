import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class for Jackson mapper
 */
public class ResourceEntity implements EntityInterface {
    @JsonProperty("type")
    private String type;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sound")
    private String sound;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSound() {
        return sound;
    }

    /**
     * car=90dB, bike=110dB, dog=95dB
     */
    public void Sound() {
        int volume = 0;
        switch (type) {
            case "car":
                volume = 90;
                break;
            case "bike":
                volume = 110;
                break;
            case "dog":
                volume = 95;
                break;
        }

        System.out.println(sound + " " + volume + "dB");
    }
}
