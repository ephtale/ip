package aoko.command;

import aoko.parser.Parser;

/**
 * Creates concrete {@link AokoCommand} instances from parsed user input.
 */
public class CommandFactory {
    /**
     * Maps a parsed command to the corresponding executable command object.
     */
    public static AokoCommand fromParsed(Parser.ParsedCommand parsed) {
        return switch (parsed.command) {
        case LIST -> new ListCommand();
        case ON -> new OnCommand(parsed.remainder);
        case FIND -> new FindCommand(parsed.remainder);
        case DELETE -> new DeleteCommand(parsed.parts);
        case MARK -> new MarkCommand(parsed.parts);
        case UNMARK -> new UnmarkCommand(parsed.parts);
        case TODO -> new TodoCommand(parsed.remainder);
        case DEADLINE -> new DeadlineCommand(parsed.remainder);
        case EVENT -> new EventCommand(parsed.remainder);
        case UNDO -> new UnknownCommand();
        case BYE -> new ByeCommand();
        case UNKNOWN -> new UnknownCommand();
        };
    }
}
