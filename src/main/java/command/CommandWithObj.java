package command;

import dopFiles.Route;

public class CommandWithObj extends Command {
    Route route;

    public CommandWithObj(Commands com, Route route) {
        super(com);
        this.route = route;
    }

    @Override
    public Route returnObj() {
        return route;
    }
}
