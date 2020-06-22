package command;

public class RemoveById extends Command {
    Integer id;

    public RemoveById(Integer id) {
        super(Commands.REMOVE_BY_ID);
        this.id = id;
    }

    @Override
    public Integer returnObj() {
        return id;
    }
}