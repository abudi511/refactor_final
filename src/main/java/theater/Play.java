package theater;

/**
 * Represents a play in theater with a name and a type.
 * This class holds information about the play like its name and type.
 */
public class Play {
    // make it private
    private String name;
    private String type;

    /**
     * Constructs a new {@code Play} with a specified name and type.
     *
     * @param name the name of the play.
     * @param type the type of the play.
     */
    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}
